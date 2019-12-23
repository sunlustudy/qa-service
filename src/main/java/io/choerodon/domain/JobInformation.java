package io.choerodon.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class JobInformation {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer baseId;

    private String expectPost;

    private String industry;

    private String organizationScale;

    private String enterpriseQuality;

    private String functionField;

    private String expectSalary;

    private String selfEvaluation;

    private String workPlace;
}