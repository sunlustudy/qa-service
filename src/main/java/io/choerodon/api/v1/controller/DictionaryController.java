package io.choerodon.api.v1.controller;

import io.choerodon.app.service.DictionaryService;
import io.choerodon.domain.DataDictionary;
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
 * @create: 2019-08-28 15:44
 */
@RestController
@RequestMapping("dictionary")
@Slf4j
@Api(description = "选项相关")
public class DictionaryController {

    @Autowired
    private  DictionaryService dictionaryService;

    @GetMapping
    @ApiOperation("获取所有问题字段")
    public ResponseEntity<Data<List<String>>> getFirstLevel() {
        log.info("请求获取所有问题字段");
        return ResponseUtils.res(dictionaryService.getFirstLevelAll());
    }

    @GetMapping("/first")
    @LogAspect
    @ApiOperation("通过题目获取一级选项信息/一级选项")
    public ResponseEntity<Data<List<String>>> getFirstLevel(@RequestParam("question") String question) {
        log.info("请求获取选项信息，通过 question: {}", question);
        return ResponseUtils.res(dictionaryService.getByFirstLevel(question));
    }


    @GetMapping("/second")
    @LogAspect
    @ApiOperation("二级选项，通过一级选项获取")
    public ResponseEntity<Data<List<String>>> getThreeBySecond(@RequestParam("sectionLevel") @ApiParam("一级选项") String sectionLevel) {
        log.info("请求获取二级选项，通过一级选项 sectionLevel:{}", sectionLevel);
        return ResponseUtils.res(dictionaryService.getBySectionLevel(sectionLevel));
    }


    @PostMapping
    @LogAspect
    @ApiOperation("创建题目的选项")
    public ResponseEntity<Data<Boolean>> create(@RequestBody DataDictionary dictionary) {
        log.info("请求创建字典:{}", dictionary);
        return ResponseUtils.res(dictionaryService.create(dictionary));
    }


}
