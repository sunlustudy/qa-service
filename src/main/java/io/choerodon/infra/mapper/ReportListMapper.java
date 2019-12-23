package io.choerodon.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.choerodon.api.dto.ReportListDTO;
import io.choerodon.domain.ReportList;

import java.util.List;
import java.util.Map;

public interface ReportListMapper extends BaseMapper<ReportList> {

    List<ReportList> selectByParams(Map<String, Object> params);

    /**
     * 通过 demandId 约束获取推送简历
     */
    List<ReportListDTO> selectByDemand(Map<String, Object> params);

    List<ReportList> selectByParamsAdminAll(Map<String, Object> params);
}