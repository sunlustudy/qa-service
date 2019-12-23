package io.choerodon.app.service.impl;

import com.google.common.collect.ImmutableMap;
import io.choerodon.api.dto.ReportListDTO;
import io.choerodon.app.service.ReportListService;
import io.choerodon.infra.mapper.ReportListMapper;
import io.choerodon.infra.utils.BeanUtil;
import io.vavr.control.Option;
import org.springframework.stereotype.Service;

import java.util.Optional;

/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-09-29 16:07
 */
@Service
public class ReportListServiceImpl implements ReportListService {


    private final ReportListMapper reportListMapper;

    @SuppressWarnings("all")
    public ReportListServiceImpl(ReportListMapper reportListMapper) {
        this.reportListMapper = reportListMapper;
    }

    @Override
    public ReportListDTO obtainByBaseIdAndDemandId(Integer baseId, Integer demandId) {
        return Option.of(reportListMapper.selectByMap(ImmutableMap.of("base_id", baseId,
                "demand_id", demandId)))
                .filter(BeanUtil::isNotEmpty)
                .map(p -> BeanUtil.convert(p.get(0), ReportListDTO.class))
                .getOrNull();
    }


}
