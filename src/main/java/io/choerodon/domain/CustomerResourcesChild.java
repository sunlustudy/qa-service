package io.choerodon.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class CustomerResourcesChild {

    @TableId(type = IdType.INPUT)
    private Integer id;

    private Integer customerId;

    private String name;

    private String tel;

    private String relationship;

    private Integer sort;

    private String department;

    private String duty;

    private String email;

}