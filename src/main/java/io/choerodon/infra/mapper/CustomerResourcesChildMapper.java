package io.choerodon.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.choerodon.domain.CustomerResourcesChild;

import java.util.List;

public interface CustomerResourcesChildMapper extends BaseMapper<CustomerResourcesChild> {

    int deleteByCustomerId(Integer customerId);


    int insertSelective(CustomerResourcesChild customerResourcesChild);

    List<Integer> selectCountGroup(Integer baseId);

    List<CustomerResourcesChild> selectByBaseId(Integer baseId);

}