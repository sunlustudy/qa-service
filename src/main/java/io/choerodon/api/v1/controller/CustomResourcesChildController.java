package io.choerodon.api.v1.controller;

import io.choerodon.api.dto.CustomerResourcesChildDTO;
import io.choerodon.app.service.CustomerResourcesChildService;
import io.choerodon.infra.annotation.LogAspect;
import io.choerodon.infra.utils.web.ResponseUtils;
import io.choerodon.infra.utils.web.dto.Data;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-09-04 09:16
 */
@RestController
@RequestMapping("resources/child")
@Slf4j
@Api(description = "客户资源子表相关控制")
public class CustomResourcesChildController {


    @Autowired
    private  CustomerResourcesChildService customerResourcesChildService;




    @PostMapping("/s")
    @ApiOperation("创建资源子表")
    @LogAspect
    public ResponseEntity<Data<Boolean>> createResourcesChild(@RequestBody @ApiParam("资源子表") List<CustomerResourcesChildDTO> customerResourcesChildDTOS) {
        log.info("请求创建客户资源子表");
        return ResponseUtils.res(customerResourcesChildService.create(customerResourcesChildDTOS));
    }


    @PutMapping("/s")
    @ApiOperation("批量更新用户资源子表,根据子表 id")
    @LogAspect
    public ResponseEntity<Data<Boolean>> update(@RequestBody @ApiParam("资源子表") List<CustomerResourcesChildDTO> customerResourcesChildDTOS) {
        log.info("请求更新客户资源子表");
        return ResponseUtils.res(customerResourcesChildService.update(customerResourcesChildDTOS));
    }

    @DeleteMapping
    @ApiOperation("请求删除资源子表,将删除凭证文件关联信息")
    @LogAspect
    public ResponseEntity<Data<Boolean>> delete(@RequestParam("id") @ApiParam("客户资源子表id") Integer id) {
        log.info("请求删除客户资源子表 id");
        return ResponseUtils.res(customerResourcesChildService.delete(id));
    }

    @GetMapping
    @ApiOperation("获取用户资源子表信息")
    @LogAspect
    public ResponseEntity<Data<List<CustomerResourcesChildDTO>>> obtain(@RequestParam("customerId") @ApiParam("客户主表 id") Integer customerId) {
        log.info("请求获取客户资源信息，通过客户主表 id");
        return ResponseUtils.res(customerResourcesChildService.obtainByCustomer(customerId));
    }


}
