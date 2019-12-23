package io.choerodon.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-09-27 10:17
 */

@Data
@ApiModel(value = "业绩情况")
public class HistoricalPerformance {

    @ApiModelProperty(value = "时间（年）")
    private Integer year;

    @ApiModelProperty("销售指标")
    private BigDecimal salesIndicators;

    @ApiModelProperty("负责区域")
    private String responsibleArea;

    @ApiModelProperty("行业")
    private String industry;

    @ApiModelProperty("负责产品和服务 直接显示产品和服务中文")
    private String productsServices;

    @ApiModelProperty("年完成金额")
    private BigDecimal completion;

    @ApiModelProperty("完成率")
    private BigDecimal percentComplete;
}
