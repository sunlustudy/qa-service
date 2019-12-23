package io.choerodon.app.service;


import io.choerodon.api.dto.MemberDTO;

public interface MemberService {

    Boolean save(MemberDTO memberDTO);

    MemberDTO getMember(Integer baseId);

    /**
     * 达成条件获得会员身份
     * @param baseId
     * @return
     */
    Boolean saveIfInvitedReach(Integer baseId);
}

