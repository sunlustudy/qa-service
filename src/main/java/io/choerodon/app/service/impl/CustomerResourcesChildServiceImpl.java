package io.choerodon.app.service.impl;

import com.aliyun.oss.OSS;
import com.google.common.collect.ImmutableMap;
import io.choerodon.api.dto.CustomerResourcesChildDTO;
import io.choerodon.app.service.CustomerResourcesChildService;
import io.choerodon.domain.CustomerResources;
import io.choerodon.domain.CustomerResourcesChild;
import io.choerodon.domain.ResourcesCertificateRelation;
import io.choerodon.infra.exception.CommonException;
import io.choerodon.infra.mapper.CustomerResourcesCertificateMapper;
import io.choerodon.infra.mapper.CustomerResourcesChildMapper;
import io.choerodon.infra.mapper.ResourcesCertificateRelationMapper;
import io.choerodon.infra.utils.OssUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.choerodon.infra.utils.BeanUtil.convert;
import static io.choerodon.infra.utils.BeanUtil.isNotEmpty;


/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-08-20 11:00
 */
@Service
public class CustomerResourcesChildServiceImpl implements CustomerResourcesChildService {


    private final CustomerResourcesChildMapper customerResourcesChildMapper;

    private final ResourcesCertificateRelationMapper certificateRelationMapper;

    private final OSS client;

    @Value("${ali.oss.bucketName}")
    private String bucketName;


    private final CustomerResourcesCertificateMapper customerResourcesCertificateMapper;

    @SuppressWarnings("all")
    public CustomerResourcesChildServiceImpl(CustomerResourcesChildMapper customerResourcesChildMapper, ResourcesCertificateRelationMapper certificateRelationMapper, OSS client, CustomerResourcesCertificateMapper customerResourcesCertificateMapper) {
        this.customerResourcesChildMapper = customerResourcesChildMapper;
        this.certificateRelationMapper = certificateRelationMapper;
        this.client = client;
        this.customerResourcesCertificateMapper = customerResourcesCertificateMapper;
    }

    @Override
    @Transactional
    public boolean create(CustomerResourcesChildDTO record) {
        CustomerResourcesChild customerResourcesChild = convert(record, CustomerResourcesChild.class);

        if (customerResourcesChildMapper.insertSelective(customerResourcesChild) != 1) {
            throw new CommonException("插入失败: " + record);
        }
        if (isNotEmpty(record.getCertificateIds())) {
            record.getCertificateIds().forEach(p -> createCertificateRelation(p, customerResourcesChild.getId()));
        }
        return true;
    }

    @Override
    @Transactional
    public boolean create(List<CustomerResourcesChildDTO> records) {
        if (isNotEmpty(records)) {
            records.forEach(this::create);
        }
        return true;
    }


    @Override
    @Transactional
    public boolean delete(Integer id) {
//        删除关联表数据，防止数据冗余
        certificateRelationMapper.deleteByMap(ImmutableMap.of("resources_child_id", id));
        if (customerResourcesChildMapper.deleteById(id) != 1) {
            throw new CommonException("删除失败 id: " + id);
        }
        return true;
    }


    private int createCertificateRelation(Integer certificatedId, Integer childId) {
        return certificateRelationMapper.insert(new ResourcesCertificateRelation(null, childId, certificatedId));
    }


    @Override
    @Transactional
    public boolean update(CustomerResourcesChildDTO record) {
        CustomerResourcesChild customerResourcesChild = convert(record, CustomerResourcesChild.class);

//        更新凭证关联信息
        if (isNotEmpty(record.getCertificateIds())) {
//            删除关联信息
            certificateRelationMapper.deleteByMap(ImmutableMap.of("resources_child_id", record.getId()));
//            添加关联信息
            record.getCertificateIds().forEach(p -> createCertificateRelation(p, record.getId()));
        }

//          更新主表
        if (customerResourcesChildMapper.updateById(customerResourcesChild) != 1) {
            throw new CommonException("更新失败 customerResourcesChild: " + record);
        }
        return true;
    }


    @Override
    @Transactional
    public boolean update(List<CustomerResourcesChildDTO> childDTOS) {
        if (isNotEmpty(childDTOS)) {
            childDTOS.forEach(this::update);
        }
        return true;
    }


    private List<Map<String, Object>> obtainCertificateByChildId(Integer childId) {
        List<Map<String, Object>> result = customerResourcesCertificateMapper.selectIdByResourcesChildId(childId);
        if (isNotEmpty(result) && !result.contains(null)) {
            result.forEach(certificate -> {
                String url = OssUtils.generateUrl(client, certificate.get("name").toString(), bucketName);
                certificate.put("url", url);
            });
            return result;
        }
        return Collections.emptyList();
    }


    @Override
    @Transactional
    public List<CustomerResourcesChildDTO> obtainByCustomer(Integer customerId) {
        List<CustomerResourcesChild> children = customerResourcesChildMapper.selectByMap(ImmutableMap.of("customer_id", customerId));

        if (isNotEmpty(children)) {
            return children.stream().map(p -> convert(p, CustomerResourcesChildDTO.class))
                    .peek(p -> {
                        List<Map<String, Object>> list = obtainCertificateByChildId(p.getId());
                        p.setCertificates(list);
                        p.setCertificateCount(0);
                        if (isNotEmpty(list)) {
                            p.setCertificateCount(list.size());
                        }
                    })
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }


    @Override
    public List<CustomerResourcesChild> listResourcesByBaseId(Integer baseId) {
        return customerResourcesChildMapper.selectByBaseId(baseId);
    }




}
