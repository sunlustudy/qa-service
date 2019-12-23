package io.choerodon.app.service;


import io.choerodon.api.dto.InvitationDTO;

import java.util.List;

public interface InvitationService {

    List<InvitationDTO> listInvitation(Integer baseId);

    Integer getInviterId(Integer invitedId);

    Integer countByBaseId(Integer baseId);
}

