package io.choerodon.api.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;


@Data
public class JobInformationDTO {
    private Integer id;

    private Integer baseId;

    @Excel(name = "期望岗位", width = 15, needMerge = true)
    private String expectPost;

    @Excel(name = "行业", width = 15, needMerge = true)
    private String industry;

    @Excel(name = "组织规模", width = 15, needMerge = true)
    private String organizationScale;

    @Excel(name = "企业性质", width = 15, needMerge = true)
    private String enterpriseQuality;

    @Excel(name = "职能领域", width = 15, needMerge = true)
    private String functionField;

    @Excel(name = "期望薪资", width = 15, needMerge = true)
    private String expectSalary;

    @Excel(name = "自我评价", width = 15, needMerge = true)
    private String selfEvaluation;

    @Excel(name = "期望工作地点", width = 15, needMerge = true)
    private String workPlace;


}