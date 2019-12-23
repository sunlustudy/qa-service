package io.choerodon.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.choerodon.domain.Invitation;

public interface InvitationMapper extends BaseMapper<Invitation> {
    Integer selectCountByBaseId(Integer baseId);
}