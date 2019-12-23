package io.choerodon.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
public class Demand {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String companyName;

    private String post;

    private String postDescription;

    private String hrName;

    private String contact;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic
    private Boolean isDel;

    @Version
    private Integer version;

    @TableField(fill = FieldFill.INSERT)
    private Integer createdBy;



}