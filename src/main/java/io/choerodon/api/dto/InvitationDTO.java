package io.choerodon.api.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@ApiModel(value = "邀请关联相关信息")
public class InvitationDTO {
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(hidden = true)
    private Integer id;

    @ApiModelProperty(value = "邀请人id")
    private Integer inviterId;

    @ApiModelProperty(value = "被邀请人id")
    private Integer invitedId;

    @ApiModelProperty(value = "邀请人昵称")
    private String inviterNickname;

    @ApiModelProperty(value = "被邀请人昵称")
    private String invitedNickname;

    @ApiModelProperty(value = "被邀请头像 url")
    private String invitedPicUrl;

    @ApiModelProperty(value = "人员是否填写完的字段")
    private Boolean isBaseInfoComplete = false;

    @ApiModelProperty(value = "基础信息填写完成率")
    private Double baseInfoCompletePer;

    @ApiModelProperty(hidden = true)
    private Date createTime;

    @ApiModelProperty(hidden = true)
    private Date updateTime;

    @ApiModelProperty(hidden = true)
    private Boolean isDel;

    @ApiModelProperty(hidden = true)
    private Integer version;

}