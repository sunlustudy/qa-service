package io.choerodon.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class Invitation {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer inviterId;

    private Integer invitedId;

    private String inviterNickname;

    private String invitedNickname;

    private String invitedPicUrl;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic
    private Boolean isDel;

    @Version
    private Integer version;


    public Invitation(Integer inviterId, Integer invitedId, String inviterNickname, String invitedNickname, String invitedPicUrl) {
        this.inviterId = inviterId;
        this.invitedId = invitedId;
        this.inviterNickname = inviterNickname;
        this.invitedNickname = invitedNickname;
        this.invitedPicUrl = invitedPicUrl;
    }
}