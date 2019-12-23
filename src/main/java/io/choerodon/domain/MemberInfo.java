package io.choerodon.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
public class MemberInfo {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer baseId;

    private String level;

    private Date startTime;

    private Date endTime;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic
    private Boolean isDel;

    @Version
    private Integer version;

}