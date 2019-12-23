package io.choerodon.api.dto;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
public class BaseRelationDTO {


    private Integer id;

    private String phone;

    private String wechatId;

    private Date createDate;

    private Date updateDate;

    private Boolean isDel;

    private Integer version;

}