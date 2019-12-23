package io.choerodon.app.service.impl;

import com.aliyun.oss.OSS;
import com.google.common.collect.ImmutableMap;
import io.choerodon.api.dto.CustomerResourcesChildDTO;
import io.choerodon.api.dto.CustomerResourcesDTO;
import io.choerodon.api.dto.SigningRecordDTO;
import io.choerodon.app.service.CardService;
import io.choerodon.app.service.CustomerResourcesService;
import io.choerodon.domain.*;
import io.choerodon.infra.exception.CommonException;
import io.choerodon.infra.mapper.*;
import io.choerodon.infra.utils.OssUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.choerodon.infra.utils.BeanUtil.*;


/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-08-20 11:00
 */
@Service
@Slf4j
public class CustomerResourcesServiceImpl implements CustomerResourcesService {


    private final CustomerResourcesMapper customerResourcesMapper;

    private final CustomerResourcesChildMapper customerResourcesChildMapper;

    private final SigningRecordMapper signingRecordMapper;

    private final CustomerResourcesCertificateMapper customerResourcesCertificateMapper;

    private final ResourcesCertificateRelationMapper resourcesCertificateRelationMapper;

    private final OSS client;

    @Value("${ali.oss.bucketName}")
    private String bucketName;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private CardService cardService;

    @Autowired
    private RedissonClient redissonClient;

    @Value("${redis.topic.update-report-all}")
    private String updateReportAll;


    @SuppressWarnings("all")
    public CustomerResourcesServiceImpl(CustomerResourcesMapper customerResourcesMapper, CustomerResourcesChildMapper customerResourcesChildMapper, SigningRecordMapper signingRecordMapper, CustomerResourcesCertificateMapper customerResourcesCertificateMapper, ResourcesCertificateRelationMapper resourcesCertificateRelationMapper, OSS client) {
        this.customerResourcesMapper = customerResourcesMapper;
        this.customerResourcesChildMapper = customerResourcesChildMapper;

        this.signingRecordMapper = signingRecordMapper;
        this.customerResourcesCertificateMapper = customerResourcesCertificateMapper;
        this.resourcesCertificateRelationMapper = resourcesCertificateRelationMapper;
        this.client = client;
    }


    @Override
    @Transactional
    public boolean create(CustomerResourcesDTO record) {
        CustomerResources customerResources = convert(record, CustomerResources.class);

//        插入客户资源主表
        if (customerResourcesMapper.insertSelective(customerResources) != 1) {
            throw new CommonException("创建客户资源主表失败: " + customerResources);
        }

        log.info("插入成功记录的 id :{}", customerResources.getId());

//        循环插入客户资源子表
        if (isNotEmpty(record.getCustomerResourcesChildDTOS())) {
            record.getCustomerResourcesChildDTOS().forEach(p -> {
//                插入客户资源子表数据
                p.setCustomerId(customerResources.getId());
                CustomerResourcesChild customerResourcesChild = convert(p, CustomerResourcesChild.class);
                if (customerResourcesChildMapper.insertSelective(customerResourcesChild) != 1) {
                    throw new CommonException("创建客户子表失败 child:" + p);
                }
//                        插入凭证与客户资源的关联表
                if (p.getCertificateIds() != null) {
                    p.getCertificateIds().forEach(certificateId -> {
                        if (resourcesCertificateRelationMapper.insert(new ResourcesCertificateRelation(null, customerResourcesChild.getId(), certificateId)) != 1) {
                            throw new CommonException("插入凭证关联信息失败");
                        }
                    });
                }

            });
        }

//        循环插入签单情况表
        if (isNotEmpty(record.getSigningRecordDTOS())) {
            record.getSigningRecordDTOS().stream()
                    .map(p -> convert(p, SigningRecord.class))
                    .forEach(p -> {
                        p.setCustomerId(customerResources.getId());
                        if (signingRecordMapper.insert(p) != 1) {
                            throw new CommonException("插入签单信息失败: " + p);
                        }
                    });
        }

        //        异步创建商机卡
        taskExecutor.execute(() -> {
            if (cardService.saveIfCompleteResource(record.getBaseId())) {
                log.info("save business card success，baseId = {}", record.getBaseId());
            } else {
                log.info("save business card failure，baseId = {}", record.getBaseId());
            }
        });


        //  更新个人和综合报告
        redissonClient.getTopic(updateReportAll)
                .publish(record.getBaseId());

        return true;
    }


    @Override
    @Transactional
    public boolean creates(List<CustomerResourcesDTO> customerResourcesDTOS) {
        customerResourcesDTOS.forEach(this::create);
        return true;
    }


    @Override
    @Transactional
    public boolean delete(Integer id) {
        if (customerResourcesMapper.deleteById(id) != 1) {
            throw new CommonException("customerResources 删除失败 id: " + id);
        }

//        删除凭证中相关信息
        customerResourcesChildMapper
                .selectByMap(ImmutableMap.of("customer_id", id))
                .forEach(p -> {

//                    List<Map<String, Object>> certificates = customerResourcesCertificateMapper.selectIdByResourcesChildId(p.getId());
//                    删除 oss 中的文件
//                    if (certificates != null && !certificates.contains(null)) {
//                        certificates.forEach(certificate -> OssUtils.deleteObject(client, bucketName, certificate.get("name").toString()));
//                    }
//                  删除客户子表凭证关联表的数据
//                    resourcesCertificateRelationMapper.selectByMap(ImmutableMap.of("resources_child_id", p.getId()))
//                            .forEach(relation -> {
////                                删除数据库中文件信息
//                                customerResourcesCertificateMapper.deleteByPrimaryKey(relation.getCertificateId());
//                            });
                    resourcesCertificateRelationMapper.deleteByMap(ImmutableMap.of("resources_child_id", p.getId()));
                });

//        删除相子表中相关信息
        customerResourcesChildMapper.deleteByCustomerId(id);
//        删除签单数据
        signingRecordMapper.deleteByCustomerId(id);
        return true;
    }


    @Override
    @Transactional
    public boolean deleteByBaseId(Integer baseId) {

        List<CustomerResources> customerResources = customerResourcesMapper.selectByMap(ImmutableMap.of("base_id", baseId));
        if (isNotEmpty(customerResources)) {
            customerResources.forEach(p -> delete(p.getId()));
        }
        return true;
    }


    /**
     * 子表数据通过子表的 id 更新
     *
     * @param record
     * @return
     */
    @Override
    @Transactional
    public boolean update(CustomerResourcesDTO record) {
        CustomerResources customerResources = convert(record, CustomerResources.class);
        if (customerResourcesMapper.updateById(customerResources) != 1) {
            throw new CommonException("更新失败 customerResources: " + record);
        }

//        循环更新子表相关信息
        if (isNotEmpty(record.getCustomerResourcesChildDTOS())) {
            record.getCustomerResourcesChildDTOS()
                    .forEach(p -> {
                        CustomerResourcesChild customerResourcesChild = convert(p, CustomerResourcesChild.class);
                        customerResources.setBaseId(record.getBaseId());
                        if (customerResourcesChildMapper.updateById(customerResourcesChild) != 1) {
                            throw new CommonException("更新客户资源子表失败 customer: " + p);
                        }

//                        处理资源子表与凭证文件关联信息
                        if (isNotEmpty(p.getCertificateIds())) {
//                            删除关联表
                            resourcesCertificateRelationMapper.deleteByMap(ImmutableMap.of("resources_child_id", p.getId()));
//                        插入新的关联表
                            p.getCertificateIds().forEach(certificateId -> {
                                if (resourcesCertificateRelationMapper.insert(new ResourcesCertificateRelation(null, customerResourcesChild.getId(), certificateId)) != 1) {
                                    throw new CommonException("插入凭证关联信息失败");
                                }
                            });
                        }

                    });
        }


        if (isNotEmpty(record.getSigningRecordDTOS())) {
            record.getSigningRecordDTOS().stream()
                    .map(p -> convert(p, SigningRecord.class))
                    .forEach(p -> {
                        p.setCustomerId(record.getId());
                        if (signingRecordMapper.updateById(p) != 1) {
                            throw new CommonException("更新客户资源子表失败 customer: " + p);
                        }
                    });
        }

        return true;
    }


    @Override
    @Transactional
    public boolean update(List<CustomerResourcesDTO> records) {
        if (isNotEmpty(records)) {
            records.forEach(this::update);
        }
        return true;
    }


    private CustomerResourcesDTO handleCustomerResource(CustomerResourcesDTO customerResourcesDTO) {

        if (customerResourcesDTO != null) {
            // 获取客户资源子表数据
            List<CustomerResourcesChild> customerResourcesChildrens =
                    customerResourcesChildMapper.selectByMap(ImmutableMap.of("customer_id", customerResourcesDTO.getId()));
            if (customerResourcesChildrens != null) {
                List<CustomerResourcesChildDTO> customerResourcesChildDTOS = customerResourcesChildrens.stream()
                        .map(p -> convert(p, CustomerResourcesChildDTO.class))
//                        获取凭证文件的 id
                        .peek(p -> {
                            List<Map<String, Object>> certificates = customerResourcesCertificateMapper.selectIdByResourcesChildId(p.getId());
                            if (isNotEmpty(certificates) && !certificates.contains(null)) {
                                p.setCertificates(certificates);
                                p.setCertificateCount(certificates.size());
//                                获取图片访问的url
                                certificates.forEach(certificate -> {
                                    String url = OssUtils.generateUrl(client, certificate.get("name").toString(), bucketName);
                                    certificate.put("url", url);
                                });

                            } else {
                                p.setCertificateCount(0);
                            }
                        })
                        .collect(Collectors.toList());
                customerResourcesDTO.setCustomerResourcesChildDTOS(customerResourcesChildDTOS);
//                设置长度
                customerResourcesDTO.setCustomerResourcesChildCount(customerResourcesChildDTOS.size());
            }
            // 获取签单信息
            List<SigningRecord> signingRecords = signingRecordMapper.selectByCustomerId(customerResourcesDTO.getId());

            if (signingRecords != null) {
                customerResourcesDTO.setSigningRecordDTOS(convertList(signingRecords, SigningRecordDTO.class));
//               设置长度
                customerResourcesDTO.setSigningRecordCount(signingRecords.size());
            }
        }
        return customerResourcesDTO;

    }


    @Override
    public ImmutableMap<String, Object> obtainByBaseId(Integer baseId) {

        // 通过基础信息 id 获取数据
        List<CustomerResources> customerResources = customerResourcesMapper.selectByMap(ImmutableMap.of("base_id", baseId));

        List<CustomerResourcesDTO> customerResourcesDTOS = new ArrayList<>();

        if (customerResources != null) {
            customerResourcesDTOS = customerResources.stream()
                    .map(p -> convert(p, CustomerResourcesDTO.class))
                    .map(this::handleCustomerResource)
                    .collect(Collectors.toList());
        }


        return ImmutableMap.of("count", customerResourcesDTOS.size(), "customerResources", customerResourcesDTOS);
    }


    @Override
    public Integer uploadFile(MultipartFile file) {

        OssUtils.upload(client, file, bucketName);
        CustomerResourcesCertificate customerResourcesCertificate = new CustomerResourcesCertificate(null, file.getOriginalFilename(), null);
        if (customerResourcesCertificateMapper.insertSelective(customerResourcesCertificate) != 1) {
            throw new CommonException("上传文件失败: " + file.getOriginalFilename());
        }
        return customerResourcesCertificate.getId();
    }


    @Override
    @Transactional
    public List<Integer> uploadFiles(MultipartFile[] files) {

        if (files != null && files.length > 0) {
            return Stream.of(files)
                    .map(this::uploadFile)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    @Override
    public boolean deleteCertificate(Integer certificateId) {
        if (resourcesCertificateRelationMapper.deleteById(certificateId) != 1) {
            throw new CommonException("删除凭证文件失败，可能不存在此文件 id: " + certificateId);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean deleteCertificate(List<Integer> certificateIds) {
        certificateIds.forEach(this::deleteCertificate);
        return true;
    }


    @Override
    public List<CustomerResources> listResourceByBaseId(Integer baseId) {
        return customerResourcesMapper.selectByMap(ImmutableMap.of("base_id", baseId));
    }


}
