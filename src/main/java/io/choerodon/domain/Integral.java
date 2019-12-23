package io.choerodon.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class Integral {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer baseId;

    private Integer point;

    private String cause;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic
    private Boolean isDel;

    @Version
    private Integer version;

    public Integral(Integer baseId, Integer point) {
        this.baseId = baseId;
        this.point = point;
    }
}