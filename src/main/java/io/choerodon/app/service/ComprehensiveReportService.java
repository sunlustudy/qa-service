package io.choerodon.app.service;

import io.choerodon.api.dto.ComprehensiveReportDTO;

import java.util.List;

/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-09-23 09:42
 */
public interface ComprehensiveReportService {

    Boolean create(Integer baseId);

    boolean delete(Integer baseId);

    boolean reInitReport(Integer baseId);

    Boolean updateOrSave(Integer baseId);

    void asyncUpdate(Integer baseId);

    ComprehensiveReportDTO obtainByBaseId(Integer baseId);

    ComprehensiveReportDTO obtainByBaseId(Integer baseId, Integer demandId);

    List<ComprehensiveReportDTO> listComprehensiveReportDTO();


}
