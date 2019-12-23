package io.choerodon.app.service;

import io.choerodon.api.dto.AnswerBankBaseDTO;
import io.choerodon.domain.AnswerBankBase;
import io.choerodon.domain.Integral;

import java.util.List;

/*
 * @program: springboot
 * @author: syun
 * @create: 2019-08-20 10:11
 */
public interface AnswerBankBaseService {

    Integer create(AnswerBankBaseDTO record);

    Boolean delete(Integer id);

    Boolean update(AnswerBankBaseDTO record);


    /**
     * 通过微信 id 获取基础问卷表
     *
     * @param wechatId
     * @return
     */
    AnswerBankBaseDTO getByWechatId(String wechatId);

    AnswerBankBaseDTO getById(Integer id);

    List<AnswerBankBase> listAnswerBankBaseByInviter(Integer inviterId);

    List<AnswerBankBaseDTO> listAnswerBaseByInviter(Integer inviterId);

    /**
     * 所有必填字段填写完成的用户数量
     *
     * @param baseId
     * @return
     */
    long countComplete(List<AnswerBankBase> answerBankBases);

    List<Integer> listBaseIds();

    List<AnswerBankBase> listAll();
    
}

