package io.choerodon.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class AnswerBankBase {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String wechatId;

    @TableField(fill = FieldFill.INSERT)
    private Date createdTime;

    private String name;

    private String nickname;

    private String englishName;

    private Integer gender;

    private String marryStatus;

    private String habitation;

    private String workPlace;

    private String education;

    private String subject;

    private String professionalName;

    private String academy;

    private Integer status;

    private Date birthday;

    private Integer inviterId;

}