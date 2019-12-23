package io.choerodon.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.choerodon.domain.Card;

public interface CardMapper extends BaseMapper<Card> {
    /**
     * 获取用户的卡包数量
     * @param baseId
     * @return
     */
    Integer countCardByBaseId(Integer baseId);
}