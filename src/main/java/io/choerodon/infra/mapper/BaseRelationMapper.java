package io.choerodon.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.choerodon.domain.BaseRelation;

import java.util.Map;

public interface BaseRelationMapper extends BaseMapper<BaseRelation> {

    int deleteByParams(Map<String, Object> params);


}