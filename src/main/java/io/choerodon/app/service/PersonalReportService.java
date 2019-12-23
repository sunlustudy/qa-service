package io.choerodon.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.api.dto.PersonalReportDTO;
import io.choerodon.api.dto.PersonalReportMobileDTO;
import io.choerodon.api.dto.ReportListDTO;
import io.choerodon.api.dto.ReportMidDTO;

import java.util.List;
import java.util.Map;

/*
 * @program: springboot
 * @author: syun
 * @create: 2019-09-17 15:57
 */
public interface PersonalReportService {

    Boolean initPersonalReport(Integer baseId);

    Boolean updateOrInit(Integer baseId);

    boolean delete(Integer id);

    boolean update(ReportMidDTO record);

    PageInfo<PersonalReportDTO> search(Integer page, Integer size, Map<String, Object> params);

    PersonalReportDTO obtainPersonalByBaseId(Integer baseId);

    Boolean createReportList(ReportListDTO reportListDTO);

    /**
     * 创建简历推送数据
     */
    Boolean createReportList(List<ReportListDTO> reportListDTOS);

    PageInfo<ReportListDTO> obtainReportList(Integer page, Integer size, Map<String, Object> params);

    PageInfo<ReportListDTO> obtainReportListByStatus(Integer page, Integer size, Integer status, String name, String phone);

    PageInfo<ReportListDTO> obtainByParams(String name, String phone, Integer demandId,
                                           Integer page, Integer size);

    /**
     * 获取所有的个人报告
     * @return
     */
    List<PersonalReportDTO> listPersonalReport();

    boolean updateReportList(ReportListDTO reportListDTO);

    PersonalReportDTO obtainByBase(Integer baseId, Integer demandId);

    /**
     * 生成所有的个人报告
     */
    boolean initPersonalReportAll();

    /**
     * 获取手机端个人报告数据
     */
    PersonalReportMobileDTO obtainMobile(Integer baseId);
}
