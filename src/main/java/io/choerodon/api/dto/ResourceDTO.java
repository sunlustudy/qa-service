package io.choerodon.api.dto;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "主页相关信息")
public class ResourceDTO {

    private Integer baseId;

    @ApiModelProperty("会员信息")
    private MemberDTO memberDTO;

    @ApiModelProperty("卡包数量数量")
    private Integer cardSize;

    @ApiModelProperty("是否已有商机卡")
    private Boolean hasBusinessCard;

    @ApiModelProperty("是否已有任务卡")
    private Boolean hasMissionCard;

    @ApiModelProperty("个人信息完善率")
    private Double baseCompletePer;

    @ApiModelProperty("客户资源完善率")
    private Double resourceCompletePer;

    @ApiModelProperty("性格测试完成率")
    private Double characterPer;


}

