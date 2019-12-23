package io.choerodon.api.v1.controller;

import io.choerodon.api.dto.InvitationDTO;
import io.choerodon.app.service.InvitationService;
import io.choerodon.infra.annotation.LogAspect;
import io.choerodon.infra.utils.web.ResponseUtils;
import io.choerodon.infra.utils.web.dto.Data;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("v1/invitation")
@Slf4j
@Api(description = "邀请用户相关")
public class InvitationController {

    @Autowired
    private InvitationService invitationService;


    @GetMapping("/{baseId}")
    @ApiOperation(value = "获取用户邀请的所有用户")
    @LogAspect
    public ResponseEntity<Data<List<InvitationDTO>>> obtainInvitations(@PathVariable("baseId") @ApiParam(value = "邀请人 id") Integer baseId) {
        log.info("请求获取用户邀请的所有用户 baseId:{}", baseId);
        return ResponseUtils.res(invitationService.listInvitation(baseId));
    }


}


