package io.choerodon.api.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
import io.choerodon.domain.JobInformation;
import io.choerodon.domain.ProfessionalSkills;
import io.choerodon.domain.WorkHistory;
import io.choerodon.domain.WorkHistoryExtend;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/*
 * @description: 用以导出 excel 类
 * @program: springboot
 * @author: syun
 * @create: 2019-10-15 15:35
 */
@Data
public class ReportExcelEntity {

    @Excel(name = "手机号", width = 15)
    private String phone;

    @ExcelEntity(name = "基本信息")
    private AnswerBankBaseDTO answerBankBaseDTO;

    @ExcelEntity(name = "专业技能")
    private ProfessionalSkillsDTO professionalSkillsDTO;

    @ExcelEntity(name = "性格特质")
    private CharacterPowerDTO characterPowerDTO;

    @ExcelEntity(name = "求职信息")
    private JobInformationDTO jobInformationDTO;

    @ExcelCollection(name = "工作经历主表")
    private List<WorkHistory> workHistories;

    @ExcelCollection(name = "工作经历拓展")
    private List<WorkHistoryExtend> workHistoryExtends;

    @ExcelCollection(name = "客户资源")
    private List<CustomerResourcesDTO> customerResources;


}
