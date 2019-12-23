package io.choerodon.api.dto;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("平台积分")
public class IntegralDTO {
    @ApiModelProperty(hidden = true)
    private Integer id;

    private Integer baseId;

    @ApiModelProperty(value = "积分分数")
    private Integer point;

    @ApiModelProperty(value = "积分来源")
    private String cause;

    @ApiModelProperty(hidden = true)
    private Date createTime;

    @ApiModelProperty(hidden = true)
    private Date updateTime;

    @ApiModelProperty(hidden = true)
    private Boolean isDel;

    @ApiModelProperty(hidden = true)
    private Integer version;
}