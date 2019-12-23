package io.choerodon.api.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@ApiModel(value = "个人报告手机端")
public class PersonalReportMobileDTO {


    @ApiModelProperty(value = "性格特质")
    private List<String> characterDetails;

    @ApiModelProperty(value = "性格描述文案")
    private String characterText;

    @ApiModelProperty(value = "性格特质是否存在")
    private Boolean isCharacterExist = false;

    //    自我管理
    @ApiModelProperty(value = "自我管理")
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
    @ApiModelProperty(value = "业务实现")
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

    @ApiModelProperty(value = "雷达图数据是否存在")
    private Boolean isQualityExist = false;

    @ApiModelProperty(value = "发展建议是否存在")
    private Boolean isProposalExist = false;

    @ApiModelProperty(value = "习得优势")
    private String proposal1;

    @ApiModelProperty(value = "自然优势")
    private String proposal2;

    @ApiModelProperty(value = "显著优势")
    private String proposal3;

    @ApiModelProperty(value = "风险领域")
    private String proposal4;

    @ApiModelProperty(value = "待发展领域")
    private String proposal5;

    @ApiModelProperty(value = "可快速提升领域")
    private String proposal6;

    @ApiModelProperty(value = "发展建议")
    private Map<String, String> proposal;
}

