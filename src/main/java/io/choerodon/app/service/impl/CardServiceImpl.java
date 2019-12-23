package io.choerodon.app.service.impl;

import com.google.common.collect.ImmutableMap;
import io.choerodon.api.dto.CardDTO;
import io.choerodon.app.service.CardService;
import io.choerodon.domain.Card;
import io.choerodon.infra.exception.CommonException;
import io.choerodon.infra.mapper.CardMapper;
import io.choerodon.infra.utils.BeanUtil;
import io.choerodon.infra.utils.CardConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationUtils;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Service("cardService")
@Slf4j
public class CardServiceImpl implements CardService {

    @Autowired
    private CardMapper cardMapper;


    @Override
    public Boolean saveCard(CardDTO cardDTO) {
        Card card = BeanUtil.convert(cardDTO, Card.class);
        return saveCard(card);
    }

    @Override
    public Boolean saveCard(Card card) {
        return cardMapper.insert(card) > 0;
    }

    @Transactional
    @Override
    public Boolean saveCard(List<CardDTO> cardDTOS) {
        if (BeanUtil.isNotEmpty(cardDTOS)) {
            cardDTOS.forEach(this::saveCard);
        }
        return true;
    }


    /**
     * 填写客户资源获得一个任务优先卡
     *
     * @param baseId
     * @return
     */
    @Override
    public Boolean saveIfCompleteResource(Integer baseId) {
        if (Boolean.TRUE.equals(hasMissionCardByResources(baseId))) {
            log.info("已存在通过填写客户资源获取的卡片");
            return false;
        } else {
            Card card = new Card();
            card.setBaseId(baseId);
            card.setCause(CardConstants.COMPLETE_RESOURCES);
            card.setType(CardConstants.RULE_PRIORITY);
            return saveCard(card);
        }
    }


    /**
     * 填写个人信息获得一张商机卡
     *
     * @param baseId
     * @return
     */
    @Override
    public Boolean saveIfCompleteBaseInfo(Integer baseId) {

        if (Boolean.TRUE.equals(hasBusinessCardByBaseInfo(baseId))) {
            log.info("已存在通过填写个人信息获取的卡片");
            return false;
        } else {
//            改成一张商机卡
            Card card = new Card();
            card.setBaseId(baseId);
            card.setCause(CardConstants.COMPLETE_BASEINFO);
            card.setType(CardConstants.BUSINESS_OPPORTUNITY);
            return saveCard(card);
        }
    }

    /**
     * 获取用户的卡包数量
     *
     * @param baseId
     * @return
     */
    @Override
    public Integer countCard(Integer baseId) {
        return cardMapper.countCardByBaseId(baseId);
    }


    private List<Card> listCardByCause(Integer baseId, String cause) {
        return cardMapper.selectByMap(ImmutableMap.of(
                "base_id", baseId,
                "cause", cause));
    }


    /**
     * 是否拥有通过完成个人信息获取的商机卡
     *
     * @param baseId
     * @return
     */
    @Override
    public Boolean hasBusinessCardByBaseInfo(Integer baseId) {
        return BeanUtil.isNotEmpty(listCardByCause(baseId, CardConstants.COMPLETE_BASEINFO));
    }


    /**
     * 是否拥有通过完成客户资源获得任务卡
     *
     * @param baseId
     * @return
     */
    @Override
    public Boolean hasMissionCardByResources(Integer baseId) {
        return BeanUtil.isNotEmpty(listCardByCause(baseId, CardConstants.COMPLETE_RESOURCES));
    }


}



