package io.choerodon.app.service.impl;

import com.aliyun.oss.OSS;
import io.choerodon.app.service.CertificateService;
import io.choerodon.domain.CustomerResourcesCertificate;
import io.choerodon.infra.exception.CommonException;
import io.choerodon.infra.mapper.CustomerResourcesCertificateMapper;
import io.choerodon.infra.utils.OssUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-09-04 14:00
 */
@Service
public class CertificateServiceImpl implements CertificateService {

    private final OSS client;

    @Value("${ali.oss.bucketName}")
    private String bucketName;

    private final CustomerResourcesCertificateMapper certificateMapper;


    public CertificateServiceImpl(OSS client, CustomerResourcesCertificateMapper certificateMapper) {
        this.client = client;
        this.certificateMapper = certificateMapper;
    }


    @Override
    public boolean deleteById(Integer id) {
        CustomerResourcesCertificate certificate = certificateMapper.selectByPrimaryKey(id);
        if (certificate != null) {
//            不做删除成功的确认
            OssUtils.deleteObject(client, bucketName, certificate.getName());
        } else {
            throw new CommonException("无此凭证文件");
        }
        return certificateMapper.deleteByPrimaryKey(id) > 0;
    }


    @Override
    public Boolean deleteByChildId(Integer childId) {
        List<Map<String, Object>> certificates = certificateMapper.selectIdByResourcesChildId(childId);
        if (certificates != null && certificates.size() > 0) {
            certificates.forEach(p -> {
//                删除数据中信息
                certificateMapper.deleteByPrimaryKey((Integer) p.get("id"));
//                删除 oss 中数据
                OssUtils.deleteObject(client, bucketName, p.get("name").toString());
            });
        }
        return true;
    }


}
