package io.choerodon.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
public class BaseRelation {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String phone;

    private String wechatId;

    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;

    @TableLogic
    private Boolean isDel;

    @Version
    private Integer version;

}