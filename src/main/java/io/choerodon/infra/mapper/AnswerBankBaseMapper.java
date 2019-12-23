package io.choerodon.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.choerodon.api.dto.AnswerBankBaseDTO;
import io.choerodon.domain.AnswerBankBase;

import java.util.List;

public interface AnswerBankBaseMapper extends BaseMapper<AnswerBankBase> {

    List<AnswerBankBase> selectByWechatId(String wechatId);

    /**
     * 获取用户邀请的所有用户
     * @param inviterId
     * @return
     */
    List<AnswerBankBase> selectByInviterId(Integer inviterId);

    List<Integer> selectIds();

    /**
     * 获取用户邀请的所有用户同时获取用户手机号
     * @param inviterId
     * @return
     */
    List<AnswerBankBaseDTO> selectByInviterIdAddPhone(Integer inviterId);

    List<AnswerBankBaseDTO> selectHasInviter();


}