package io.choerodon.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.choerodon.domain.BaseUser;

public interface BaseUserMapper extends BaseMapper<BaseUser> {
    BaseUser selectByUsername(String username);
}