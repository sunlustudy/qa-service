package io.choerodon.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class ReportList {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer baseId;

    private String name;

    private String workYears;

    private String education;

    private String expectPost;

    private Date deliveryTime;

    private String fitDegree;

    private Integer demandId;

    private String phone;

    private Integer status;

}