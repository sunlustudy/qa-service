package io.choerodon.api.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CustomerResourcesChildDTO {
    private Integer id;

    private Integer customerId;

    @Excel(name = "姓名", width = 10)
    private String name;

    @Excel(name = "联系方式", width = 15)
    private String tel;

    @Excel(name = "客户关系深度", width = 10)
    private String relationship;

    private Integer sort;

    @Excel(name = "所属部门", width = 10)
    private String department;

    @Excel(name = "客户职务", width = 10)
    private String duty;

    @Excel(name = "邮箱", width = 10)
    private String email;

    private List<Integer> certificateIds;
    //    凭证的id
    private List<Map<String, Object>> certificates;

    private Integer certificateCount;
}