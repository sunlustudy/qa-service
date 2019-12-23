package io.choerodon.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-09-19 11:07
 */
@Data
@ApiModel(value = "工作经历")
public class WorkHistoryReport {

    private Integer id;

    private Integer baseId;
    @ApiModelProperty(value = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date startTime;

    @ApiModelProperty(value = "离职时间")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date endTime;

    @ApiModelProperty(value = "任职公司")
    private String moodyhas;

    @ApiModelProperty(value = "岗位名称")
    private String job;

    @ApiModelProperty(value = "管理规模")
    private String scale;

    @ApiModelProperty(value = "公司主营业务")
    private String mainBusiness;

    @ApiModelProperty(value = "负责区域")
    private String responsibleArea;

    @ApiModelProperty(value = "负责行业")
    private String industry;

    @ApiModelProperty(value = "负责产品和服务")
    private String productsServices;

    @ApiModelProperty(value = "销售指标")
    private BigDecimal salesIndicators;

    @ApiModelProperty(value = "年度完成金额")
    private BigDecimal completion;

    @ApiModelProperty(value = "关键经历")
    private String keyExperiences;

    @ApiModelProperty(value = "薪资水平")
    private String salary;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "离职原因")
    private String dimissionCause;

    @ApiModelProperty(value = "其他主营业务")
    private String otherBusiness;

    @ApiModelProperty(value = "其他离职原因")
    private String otherCause;

    @ApiModelProperty(value = "公司平均业绩指标")
    private BigDecimal averagePerformance;
    //    销售指标/完成情况
    @ApiModelProperty(value = "销售指标/完成情况")
    private String saleAndCompletion;
    //    销售指标完成率
    @ApiModelProperty(value = "销售指标完成率")
    private BigDecimal salePerCompletion;


}
