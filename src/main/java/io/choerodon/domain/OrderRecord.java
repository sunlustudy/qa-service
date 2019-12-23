package io.choerodon.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class OrderRecord {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String wechatId;

    private String phone;

    private Date orderTime;

    private String address;

    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    @TableLogic
    private Boolean isDel;


    public OrderRecord(String wechatId, String phone, Date orderTime, String address) {
        this.wechatId = wechatId;
        this.phone = phone;
        this.orderTime = orderTime;
        this.address = address;
    }
}