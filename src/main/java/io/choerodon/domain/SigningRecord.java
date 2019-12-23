package io.choerodon.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class SigningRecord {


    @TableId(type = IdType.INPUT)
    private Integer id;

    private Integer customerId;

    private String signYear;

    private String target;

    private String targetOther;

    private String amount;



}