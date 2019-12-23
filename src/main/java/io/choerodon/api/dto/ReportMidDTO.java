package io.choerodon.api.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-09-19 10:04
 */

@Data
@ApiModel(value = "个人报告修改类，所有必填")
public class ReportMidDTO {

    @ApiModelProperty(value = "个人报告 id", required = true)
    private Integer id;
    //    自我管理
    @ApiModelProperty(value = "自我管理", hidden = true)
    private BigDecimal quality1;
    //    理解业务  手输
    @ApiModelProperty(value = "理解业务")
    private BigDecimal quality2;
    //    不骄不躁 手输
    @ApiModelProperty(value = "不骄不躁")
    private BigDecimal quality3;
    //    自信 手输
    @ApiModelProperty(value = "自信")
    private BigDecimal quality4;
    //    业务实现
    @ApiModelProperty(value = "业务实现", hidden = true)
    private BigDecimal quality5;
    //    使命必达 手输
    @ApiModelProperty(value = "使命必达")
    private BigDecimal quality6;
    //  老谋深算 手输
    @ApiModelProperty(value = "老谋深算")
    private BigDecimal quality7;
    //融化客户 手输
    @ApiModelProperty(value = "融化客户")
    private BigDecimal quality8;
    //利益结盟 手输
    @ApiModelProperty(value = "利益结盟")
    private BigDecimal quality9;
    //经营关系 手输
    @ApiModelProperty(value = "经营关系")
    private BigDecimal quality10;
    //团队管理 手输
    @ApiModelProperty(value = "团队管理")
    private BigDecimal quality11;
    //带兵打仗 手输
    @ApiModelProperty(value = "带兵打仗")
    private BigDecimal quality12;
    // 推送状态
    @ApiModelProperty(value = "推送状态", hidden = true)
    private String status;

}
