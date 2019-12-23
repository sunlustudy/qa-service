package io.choerodon.api.v1.controller;

import io.choerodon.app.service.SchoolService;
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
import java.util.Map;


/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-08-22 10:04
 */
@RestController
@RequestMapping("v1/school")
@Slf4j
@Api(description = "学校信息相关")
public class SchoolController {
    @Autowired
    private  SchoolService schoolService;


    @GetMapping("/hello/{params}")
    public String hello(@PathVariable("params") String params) {
        return "hello world " + params;
    }

    @GetMapping("/provinces")
    @ApiOperation("获取省级信息")
    public ResponseEntity<Data<List<Map<String, Object>>>> getProvinces() {
        log.info("请求获取省级信息");
        return ResponseUtils.res(schoolService.getProvince());
    }

    @GetMapping("/cities")
    @ApiOperation("通过省 id 获取市级信息")
    public ResponseEntity<Data<List<Map<String, Object>>>> getCities(@RequestParam("pid") @ApiParam("省 id") String pid) {
        log.info("请求获取市级数据 省 pid={}", pid);
        return ResponseUtils.res(schoolService.getCityByProvince(pid));
    }

    @GetMapping("/schools")
    @ApiOperation("通过市级 id 获取学校数据")
    public ResponseEntity<Data<List<Map<String, Object>>>> getSchools(@RequestParam("pid") @ApiParam("市 id") String pid) {
        log.info("请求获取学校信息 市 pid={}", pid);
        return ResponseUtils.res(schoolService.getSchoolByCity(pid));
    }


    @GetMapping("/schools/keyword")
    @ApiOperation("模糊查询学校数据")
    public ResponseEntity<Data<List<String>>> getSchoolsByKW(@RequestParam("keyword") String keyword) {
        log.info("请求获取学校信息  keyword={}", keyword);
        return ResponseUtils.res(schoolService.searchSchool(keyword));
    }


}
