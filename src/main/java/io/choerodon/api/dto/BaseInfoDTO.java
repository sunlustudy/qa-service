package io.choerodon.api.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "个人信息综合")
public class BaseInfoDTO {

    @ApiModelProperty("基本信息表")
    private AnswerBankBaseDTO answerBankBaseDTO;

    @ApiModelProperty("求职信息")
    private JobInformationDTO jobInformationDTO;

//    @ApiModelProperty("专业技能")
//    private ProfessionalSkillsDTO professionalSkillsDTO;

    @ApiModelProperty("工作经历")
    private WorkHistoryDTO workHistoryDTO;


}

