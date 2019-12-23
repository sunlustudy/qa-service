package io.choerodon.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomerResources {
    @TableId(type = IdType.INPUT)
    private Integer id;

    private Integer baseId;

    private String customer;

    private String organizationalCode;

    private String target;

    private String cycle;

    private String amount;

    private Integer sort;

    private String incomeScale;

    private String staffSize;

    private String budgetYear;

    private String enterpriseQuality;


}