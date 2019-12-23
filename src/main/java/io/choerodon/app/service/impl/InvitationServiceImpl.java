package io.choerodon.app.service.impl;

import com.google.common.collect.ImmutableMap;
import io.choerodon.api.dto.InvitationDTO;
import io.choerodon.app.service.BaseInfoService;
import io.choerodon.app.service.InvitationService;
import io.choerodon.domain.Invitation;
import io.choerodon.infra.mapper.InvitationMapper;
import io.choerodon.infra.utils.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static io.choerodon.infra.utils.BeanUtil.isNotEmpty;

@Service("invitationService")
public class InvitationServiceImpl implements InvitationService {

    @Autowired
    private InvitationMapper invitationMapper;

    @Autowired
    private BaseInfoService baseInfoService;

    /**
     * 获取邀请人邀请的所有的用户
     *
     * @param baseId
     * @return
     */
    @Override
    public List<InvitationDTO> listInvitation(Integer baseId) {
        List<Invitation> invitations = invitationMapper.selectByMap(ImmutableMap.of("inviter_id", baseId));
        if (isNotEmpty(invitations)) {
            List<InvitationDTO> invitationDTOS = BeanUtil.convertList(invitations, InvitationDTO.class);
//            添加填写完成状态
            for (InvitationDTO invitationDTO : invitationDTOS) {
                Double baseInfoCompletePer = baseInfoService.calBaseInfoCompetePer(invitationDTO.getInvitedId());
                invitationDTO.setIsBaseInfoComplete(baseInfoCompletePer == 1);
                invitationDTO.setBaseInfoCompletePer(baseInfoCompletePer);
            }
            return invitationDTOS;
        } else {
            return Collections.emptyList();
        }
    }


    /**
     * 获取邀请人的 id
     *
     * @param invitedId
     * @return
     */
    @Override
    public Integer getInviterId(Integer invitedId) {
        List<Invitation> invitations = invitationMapper.selectByMap(ImmutableMap.of("invited_id", invitedId));
        if (isNotEmpty(invitations)) {
            return invitations.get(0).getInviterId();
        } else {
            return null;
        }
    }


    /**
     * 获取当前用户邀请人的数量
     * @param baseId
     * @return
     */
    @Override
    public Integer countByBaseId(Integer baseId) {
        return invitationMapper.selectCountByBaseId(baseId);
    }


}



