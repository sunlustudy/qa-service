package io.choerodon.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.choerodon.domain.Demand;

import java.util.List;
import java.util.Map;

public interface DemandMapper extends BaseMapper<Demand> {

    List<Demand> selectByParams(Map<String, Object> params);

    List<Demand> selectByParamsAll(Map<String, Object> params);

    int updateByPrimaryKeySelective(Demand demand);

    int updateStatus(Integer id, Boolean isDel,Integer createdBy);

    List<Demand> selectRecommended(Integer baseId);


}