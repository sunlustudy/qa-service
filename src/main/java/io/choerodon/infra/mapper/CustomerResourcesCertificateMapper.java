package io.choerodon.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.choerodon.domain.CustomerResourcesCertificate;

import java.util.List;
import java.util.Map;

public interface CustomerResourcesCertificateMapper extends BaseMapper<CustomerResourcesCertificate> {
    int deleteByPrimaryKey(Integer id);

    int insert(CustomerResourcesCertificate record);

    int insertSelective(CustomerResourcesCertificate record);

    CustomerResourcesCertificate selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CustomerResourcesCertificate record);

    int updateByPrimaryKey(CustomerResourcesCertificate record);

    List<Map<String, Object>> selectIdByResourcesChildId(Integer resourcesChildId);

}