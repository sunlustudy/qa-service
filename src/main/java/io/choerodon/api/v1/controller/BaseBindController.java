package io.choerodon.api.v1.controller;

import io.choerodon.api.dto.BaseRelationDTO;
import io.choerodon.app.service.BaseInfoService;
import io.choerodon.domain.DomainName;
import io.choerodon.infra.annotation.LogAspect;
import io.choerodon.infra.config.security.AuthoritiesConstants;
import io.choerodon.infra.mapper.DomainNameMapper;
import io.choerodon.infra.utils.web.ResponseUtils;
import io.choerodon.infra.utils.web.dto.Data;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Map;


/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-08-27 10:53
 */
@RestController
@RequestMapping("bind")
@Slf4j
@Api(description = "手机号、微信号、短信相关操作")
public class BaseBindController {

    @Autowired
    private  BaseInfoService baseInfoService;

    @Autowired
    private DomainNameMapper domainNameMapper;

    @PostMapping
    @ApiOperation("绑定手机号与微信号，（测试验证码为 hand ）")
    @LogAspect
    public ResponseEntity<Data<Boolean>> bind(@RequestBody BaseRelationDTO baseRelationDTO,
                                              @RequestParam("code") String code) {

        log.info("请求绑定手机号与微信号: {},code:{}", baseRelationDTO, code);
        return ResponseUtils.res(baseInfoService.checkAndBind(baseRelationDTO, code));
    }


    @PutMapping("/unBind")
    @ApiOperation("解绑手机号微信号")
    @LogAspect
    public ResponseEntity<Data<Boolean>> unBind(@RequestBody BaseRelationDTO baseRelationDTO) {
        log.info("请求解绑手机号微信号: relation: {}", baseRelationDTO);
        return ResponseUtils.res(baseInfoService.unBind(baseRelationDTO));
    }


    @GetMapping("/wechatId")
    @ApiOperation("通过微信 id 获取绑定信息，以及填写状态")
    @LogAspect
    public ResponseEntity<Data<Map<String, Object>>> obtainRelation(@RequestParam("wechatId") String wechatId) {
        log.info("请求通过微信 id 获取: {}", wechatId);
        return ResponseUtils.res(baseInfoService.obtainByWechatId(wechatId));
    }


    @PutMapping("/sendCode")
    @ApiOperation("发送短信验证码")
    @LogAspect
    public ResponseEntity<Data<Boolean>> sendCode(@RequestParam("phone") String phone,
                                                  @RequestParam("wechatId") String wechatId) {
        log.info("请求发送短信验证码: phone:{},wechatId:{}", phone, wechatId);
        return ResponseUtils.res(baseInfoService.sendCode(phone, wechatId));
    }


    @PutMapping("/sendOrderInfo")
    @ApiOperation("发送预约信息")
    @LogAspect
    public ResponseEntity<Data<Boolean>> sendInfo(@RequestParam("wechatId") String wechatId,
                                                  @RequestParam("date") Long date,
                                                  @RequestParam("address") String address) {
        log.info("请求发送预约信息: wechatId:{},date:{},address:{}", wechatId, date, address);
        return ResponseUtils.res(baseInfoService.sendOrderInfo(wechatId, new Date(date), address));
    }


    @GetMapping("/domain/url")
    @ApiOperation("获取域名信息")
    public ResponseEntity<Data<DomainName>> get() {
        log.info("请求获取域名");
        return ResponseUtils.res(domainNameMapper.selectByPrimaryKey(1));
    }


}
