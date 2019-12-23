package io.choerodon.app.service;

import io.choerodon.api.dto.ReportListDTO;

/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-09-29 16:06
 */
public interface ReportListService {

    ReportListDTO obtainByBaseIdAndDemandId(Integer baseId, Integer demandId);
}
