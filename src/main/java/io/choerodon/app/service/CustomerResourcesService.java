package io.choerodon.app.service;

import com.google.common.collect.ImmutableMap;
import io.choerodon.api.dto.CustomerResourcesDTO;
import io.choerodon.domain.CustomerResources;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-08-20 10:58
 */
public interface CustomerResourcesService {


    boolean create(CustomerResourcesDTO record);

    boolean creates(List<CustomerResourcesDTO> customerResourcesDTOS);

    boolean delete(Integer id);

    boolean deleteByBaseId(Integer baseId);

    boolean update(CustomerResourcesDTO record);

    boolean update(List<CustomerResourcesDTO> records);

    /**
     * 通过基础表 id 获取客户资源数据
     * @param baseId
     * @return
     */
    ImmutableMap<String, Object> obtainByBaseId(Integer baseId);

    Integer uploadFile(MultipartFile file);

    /**
     * 上传文件入数据库
     * @param files
     * @return
     */
    List<Integer> uploadFiles(MultipartFile[] files);

    /**
     * 删除凭证文件通过 id
     * @param certificateId
     * @return
     */
    boolean deleteCertificate(Integer certificateId);

    boolean deleteCertificate(List<Integer> certificateIds);

    List<CustomerResources> listResourceByBaseId(Integer baseId);
}
