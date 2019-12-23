package io.choerodon.app.service;

import io.choerodon.api.dto.CardDTO;
import io.choerodon.domain.Card;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CardService {
    Boolean saveCard(CardDTO cardDTO);

    Boolean saveCard(Card card);

    @Transactional
    Boolean saveCard(List<CardDTO> cardDTOS);

    Boolean saveIfCompleteResource(Integer baseId);

    Boolean saveIfCompleteBaseInfo(Integer baseId);

    Integer countCard(Integer baseId);

    Boolean hasBusinessCardByBaseInfo(Integer baseId);

    Boolean hasMissionCardByResources(Integer baseId);
}
