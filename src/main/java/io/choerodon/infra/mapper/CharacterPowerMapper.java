package io.choerodon.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.choerodon.domain.CharacterPower;
import io.choerodon.domain.CharacterTemp;

public interface CharacterPowerMapper extends BaseMapper<CharacterPower> {

    CharacterTemp seleteByBaseIdTemp(Integer baseId);
    


}