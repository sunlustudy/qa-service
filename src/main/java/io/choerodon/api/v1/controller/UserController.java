package io.choerodon.api.v1.controller;

import io.choerodon.api.dto.BaseUserDTO;
import io.choerodon.app.service.AuthenticationService;
import io.choerodon.domain.BaseUser;
import io.choerodon.infra.annotation.LogAspect;
import io.choerodon.infra.config.security.AuthoritiesConstants;
import io.choerodon.infra.config.security.CustomUser;
import io.choerodon.infra.utils.SecurityUtils;
import io.choerodon.infra.utils.web.ResponseUtils;
import io.choerodon.infra.utils.web.dto.Data;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;


/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-09-17 14:10
 */
@RestController
@RequestMapping("api/user")
@Slf4j
@Api(description = "用户编辑相关")
public class UserController {

    @Autowired
    private  AuthenticationService authenticationService;




    @PostMapping("/create")
    @ApiOperation("创建用户")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    @LogAspect
    public ResponseEntity<Data<Boolean>> createUser(@RequestBody BaseUserDTO user) {
        log.info("请求创建用户");
        return ResponseUtils.res(authenticationService.createUser(user));
    }

    @GetMapping("/username")
    @ApiOperation("获取用户")
    @LogAspect
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.USER + "\")")
    public ResponseEntity<Data<BaseUser>> obtainUser(@RequestParam("username") String username) throws AccessDeniedException {
        log.info("请求获取用户信息，通过 username:{}", username);
        return ResponseUtils.res(authenticationService.obtainUser(username));
    }

    @DeleteMapping("/id")
    @ApiOperation("删除用户")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    @LogAspect
    public ResponseEntity<Data<Boolean>> deleteUser(@RequestParam("id") Integer id) {
        log.info("请求删除用户 id:{}", id);
        return ResponseUtils.res(authenticationService.deleteUser(id));
    }

    @PutMapping
    @ApiOperation("更新用户，目前只能更新 phone、name、username 字段")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.USER + "\")")
    @LogAspect
    public ResponseEntity<Data<Boolean>> update(@RequestBody BaseUser user) {
        log.info("请求更新 user ：{}", user);
        return ResponseUtils.res(authenticationService.updateUser(user));
    }


    @GetMapping("/current")
    @ApiOperation("获取当前登陆用户身份")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.USER + "\")")
    @LogAspect
    public ResponseEntity<Data<BaseUser>> getCurrent() {
        BaseUser user = authenticationService.obtainUserByUsername(SecurityUtils.getUsername());
        user.setPassword(null);
        return ResponseUtils.res(user);
    }

}
