package io.choerodon.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class UserRoleRelation {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private Integer roleId;

    public UserRoleRelation(Integer userId, Integer roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }
}