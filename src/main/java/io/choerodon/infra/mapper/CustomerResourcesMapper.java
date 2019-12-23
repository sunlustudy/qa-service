package io.choerodon.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.choerodon.domain.CustomerResources;

public interface CustomerResourcesMapper extends BaseMapper<CustomerResources> {


    int insertSelective(CustomerResources record);

}