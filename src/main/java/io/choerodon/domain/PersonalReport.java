package io.choerodon.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;


@Data
public class PersonalReport {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer baseId;

    private String selfEvaluation;

    private String name;

    private String gender;

    private String birthday;

    private String marryStatus;

    private String workYears;

    private String habitation;

    private String expectPost;

    private String workPlace;

    private String education;

    private String academy;

    private String subject;

    private String professionalName;

    private String knowledge;

    private String skill;

    private BigDecimal quality1;

    private BigDecimal quality2;

    private BigDecimal quality3;

    private BigDecimal quality4;

    private BigDecimal quality5;

    private BigDecimal quality6;

    private BigDecimal quality7;

    private BigDecimal quality8;

    private BigDecimal quality9;

    private BigDecimal quality10;

    private BigDecimal quality11;

    private BigDecimal quality12;

    private Integer character1;

    private Integer character2;

    private Integer character3;

    private Integer character4;

    private Integer character5;

    private Integer character6;

    private Integer character7;

    private Integer character8;

    private Integer character9;

    private Integer character10;

    private Integer character11;

    private String drivingFactors;

    private String expectIndustry;

    private String expectCompany;

    private String expectSalary;

    private String proposal1;

    private String proposal2;

    private String proposal3;

    private String proposal4;

    private String proposal5;

    private String proposal6;

    private String status;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    private String phone;

    private Integer recommendCount;

}