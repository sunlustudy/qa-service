package io.choerodon.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
public class Card {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer baseId;

    private String type;

    private Date startTime;

    private Date endTime;

    private String cause;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic
    private Boolean isDel;

    @Version
    private Integer version;


}