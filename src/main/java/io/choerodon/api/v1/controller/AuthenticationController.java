package io.choerodon.api.v1.controller;

import io.choerodon.api.dto.BaseUserDTO;
import io.choerodon.app.service.AuthenticationService;
import io.choerodon.domain.LoginVM;
import io.choerodon.infra.annotation.LogAspect;
import io.choerodon.infra.utils.web.ResponseUtils;
import io.choerodon.infra.utils.web.dto.Data;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-09-16 17:03
 */
@RestController
@RequestMapping("api/auth")
@Slf4j
@Api(description = "登陆注册相关")
public class AuthenticationController {

    @Autowired
    private  AuthenticationService authenticationService;



    @PostMapping("/authenticate")
    @ApiOperation("登陆验证")
    @LogAspect
    public ResponseEntity<Data<Map<String, Object>>> authorize(@RequestBody LoginVM loginVM) {
        log.info("请求登陆 ：{}", loginVM);
        return ResponseUtils.res(authenticationService.auth(loginVM));
    }

    @PostMapping("/sendCode/register")
    @ApiOperation("发送短信验证码(注册)")
    @LogAspect
    public ResponseEntity<Data<Boolean>> sendCode(@RequestParam("phone") String phone) {
        log.info("请求发送验证码: phone:{}", phone);
        return ResponseUtils.res(authenticationService.sendCode(phone));
    }

    @PostMapping("/sendCode/password/phone")
    @ApiOperation("发送短信验证码(修改密码)")
    @LogAspect
    public ResponseEntity<Data<Boolean>> sendCodeChangePWD(@RequestParam("phone") @ApiParam("手机号") String phone) {
        log.info("请求发送验证码: phone:{}", phone);
        return ResponseUtils.res(authenticationService.sendChangePWDCode(phone));
    }


    @PostMapping("/sendCode/password/username")
    @ApiOperation("发送短信验证码(修改密码)")
    @LogAspect
    public ResponseEntity<Data<Boolean>> sendCodeChange(@RequestParam("username") @ApiParam("用户名") String username) {
        log.info("请求发送验证码: username:{}", username);
        return ResponseUtils.res(authenticationService.sendPwdChange(username));
    }


    @PostMapping("/register")
    @ApiOperation("注册创建用户（测试用验证码 hand ）")
    @LogAspect
    public ResponseEntity<Data<Boolean>> register(@RequestBody @ApiParam("用户信息") BaseUserDTO baseUser,
                                                  @RequestParam("code") @ApiParam("验证码") String code) {

        log.info("请求注册用户: {}，code: {}", baseUser, code);
        return ResponseUtils.res(authenticationService.register(baseUser, code));
    }

    @PostMapping("/refresh")
    @ApiOperation("刷新 token")
    @LogAspect
    public ResponseEntity<Data<Map<String, Object>>> refreshToken(@RequestParam("username") String username,
                                                                  @RequestParam("refreshToken") String refreshToken) {
        log.info("请求刷新 token");
        return ResponseUtils.res(authenticationService.refreshToken(username, refreshToken));
    }


    @GetMapping("/check/username")
    @ApiOperation("检测用户名是否存在,true 存在，false 不存在")
    @LogAspect
    public ResponseEntity<Data<Boolean>> checkUsername(@RequestParam("usernmae") @ApiParam("用户名") String username) {
        log.info("请求检测用户名是否存在 username:{}", username);
        return ResponseUtils.res(authenticationService.isUsernameExist(username));
    }

    @PutMapping("/password")
    @ApiOperation("修改密码")
    @LogAspect
    public ResponseEntity<Data<Boolean>> changePassword(
            @RequestParam("username") @ApiParam("用户名") String username,
            @RequestParam("password") @ApiParam("新密码") String newPassword,
            @RequestParam("code") @ApiParam("验证码") String code) {
        log.info("请求修改密码 username :{},code: {}", username, code);
        return ResponseUtils.res(authenticationService.changePassword(username, newPassword, code));
    }


}
