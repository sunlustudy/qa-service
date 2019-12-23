package io.choerodon.api.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "基本表")
public class AnswerBankBaseDTO implements Serializable {

    @ApiModelProperty(hidden = true)
    private Integer id;

    @ApiModelProperty(value = "邀请人 id")
    private Integer inviterId;

    @ApiModelProperty(value = "被邀请人的头像链接")
    private String invitedPicUrl;

    @Excel(name = "微信号", width = 15, needMerge = true)
    private String wechatId;

    @Excel(name = "手机号", width = 20)
    private String phone;

    @Excel(name = "姓名", width = 10, needMerge = true)
    private String name;

    @ApiModelProperty(value = "昵称")
    @Excel(name = "昵称", width = 10, needMerge = true)
    private String nickname;

    @ApiModelProperty(value = "英文名")
    @Excel(name = "英文名", width = 10, needMerge = true)
    private String englishName;

    @Excel(name = "性别", width = 10, needMerge = true)
    private Integer gender;

    @Excel(name = "婚姻状态", width = 10, needMerge = true)
    private String marryStatus;

    @Excel(name = "居住地", width = 20, needMerge = true)
    private String habitation;

    @Excel(name = "工作地", width = 20, needMerge = true)
    private String workPlace;

    @Excel(name = "最高学历", width = 10, needMerge = true)
    private String education;

    @Excel(name = "学科", width = 10, needMerge = true)
    private String subject;

    @Excel(name = "专业名称", width = 20, needMerge = true)
    private String professionalName;

    @Excel(name = "毕业院校", width = 20, needMerge = true)
    private String academy;

    private Integer status;

    @Excel(name = "出生日期", width = 15, format = "yyyy-MM-dd", needMerge = true)
    private Date birthday;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date createdTime;

    private Integer completePer;


}