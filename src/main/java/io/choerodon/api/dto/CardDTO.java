package io.choerodon.api.dto;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("卡包")
public class CardDTO {
    @ApiModelProperty(hidden = true)
    private Integer id;

    @ApiModelProperty(hidden = true)
    private Integer baseId;

    @ApiModelProperty("卡片类型")
    private String type;

    @ApiModelProperty("有效期开始时间")
    private Date startTime;

    @ApiModelProperty("有效期结束时间")
    private Date endTime;

    @ApiModelProperty("获取原因")
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