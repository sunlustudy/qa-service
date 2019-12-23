package io.choerodon.api.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class SigningRecordDTO {


    private Integer id;

    private Integer customerId;

    @Excel(name = "签单年份", width = 10)
    private String signYear;

    @Excel(name = "签单标的", width = 10)
    private String target;

    @Excel(name = "当年签单总金额", width = 15)
    private String targetOther;

    @Excel(name = "其他签单标的", width = 15)
    private String amount;


}