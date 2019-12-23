package io.choerodon.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.choerodon.domain.WorkHistory;

import java.util.List;

public interface WorkHistoryMapper extends BaseMapper<WorkHistory> {

    List<WorkHistory> selectRecentlyByNull(Integer baseId);

    List<WorkHistory> selectRecently(Integer baseId);



}