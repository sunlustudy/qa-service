package io.choerodon.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.choerodon.domain.WorkHistoryExtend;

import java.util.List;

public interface WorkHistoryExtendMapper extends BaseMapper<WorkHistoryExtend> {

    List<WorkHistoryExtend> selectByBaseIdLimit(Integer baseId);

}