package io.choerodon.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkHistory {

    @TableId(type = IdType.INPUT)
    private Integer id;

    private Integer baseId;

    @Excel(name = "开始时间", width = 15, format = "yyyy-MM-dd" , needMerge = true)
    private Date startTime;

    @Excel(name = "结束时间", width = 15, format = "yyyy-MM-dd", needMerge = true)
    private Date endTime;

    @Excel(name = "任职公司", width = 15, needMerge = true)
    private String moodyhas;

    @Excel(name = "岗位名称", width = 15, needMerge = true)
    private String job;

    @Excel(name = "管理规模", width = 15, needMerge = true)
    private String scale;

    @Excel(name = "公司主营业务", width = 15, needMerge = true)
    private String mainBusiness;

    @Excel(name = "负责区域", width = 15, needMerge = true)
    private String responsibleArea;

    @Excel(name = "负责行业", width = 15, needMerge = true)
    private String industry;

    @Excel(name = "负责产品和服务", width = 15, needMerge = true)
    private String productsServices;

    @Excel(name = "销售指标", width = 15, needMerge = true)
    private BigDecimal salesIndicators;

    @Excel(name = "年度完成金额", width = 15, needMerge = true)
    private BigDecimal completion;

    @Excel(name = "关键经历", width = 15, needMerge = true)
    private String keyExperiences;

    @Excel(name = "薪资水平", width = 15, needMerge = true)
    private String salary;

    private Integer sort;

    @Excel(name = "离职原因", width = 15, needMerge = true)
    private String dimissionCause;

    @Excel(name = "其他主营业务", width = 15, needMerge = true)
    private String otherBusiness;

    @Excel(name = "其他离职原因", width = 15, needMerge = true)
    private String otherCause;

    @Excel(name = "公司平均业绩指标", width = 15, needMerge = true)
    private BigDecimal averagePerformance;

    @Excel(name = "组织机构代码", width = 15, needMerge = true)
    private String organizationalCode;

    
}