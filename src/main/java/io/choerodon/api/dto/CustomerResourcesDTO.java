package io.choerodon.api.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import lombok.Data;

import java.util.List;

@Data
public class CustomerResourcesDTO {
    private Integer id;

    private Integer baseId;

    @Excel(name = "客户名称", width = 20)
    private String customer;

    @Excel(name = "组织机构代码", width = 30)
    private String organizationalCode;

    @Excel(name = "项目标的", width = 10)
    private String target;

    @Excel(name = "项目周期", width = 10)
    private String cycle;

    @Excel(name = "项目金额", width = 10)
    private String amount;

    private Integer sort;

    @Excel(name = "收入规模", width = 10)
    private String incomeScale;

    @Excel(name = "人员规模", width = 10)
    private String staffSize;

    @Excel(name = "当年客户信息化预算", width = 10)
    private String budgetYear;

    @Excel(name = "企业性质", width = 10)
    private String enterpriseQuality;

    @ExcelCollection(name = "客户子表")
    private List<CustomerResourcesChildDTO> customerResourcesChildDTOS;

    //    近三年签单情况
    @ExcelCollection(name = "签单情况")
    private List<SigningRecordDTO> signingRecordDTOS;

    private Integer customerResourcesChildCount;

    private Integer signingRecordCount;


}