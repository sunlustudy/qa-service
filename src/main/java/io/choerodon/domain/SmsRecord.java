package io.choerodon.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;


@Data
@AllArgsConstructor
public class SmsRecord {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String wechatId;

    private String phone;

    private String code;

    private Date sendTime;

    private Date expireTime;
}