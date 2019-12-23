package io.choerodon.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.choerodon.domain.PersonalReport;

import java.util.List;
import java.util.Map;

public interface PersonalReportMapper extends BaseMapper<PersonalReport> {

    List<PersonalReport> selectByParams(Map<String, Object> params);

    PersonalReport selectByBaseId(Integer baseId);


}