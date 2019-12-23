package io.choerodon.api.v1.controller;

import io.choerodon.api.dto.WorkHistoryDTO;
import io.choerodon.app.service.WorkHistoryService;
import io.choerodon.infra.annotation.LogAspect;
import io.choerodon.infra.exception.CommonException;
import io.choerodon.infra.utils.web.ResponseUtils;
import io.choerodon.infra.utils.web.dto.Data;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;


/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-08-20 11:20
 */
@RestController
@RequestMapping("work/history")
@Slf4j
@Api(description = "工作经历相关")
public class WorkHistoryController {

    @Autowired
    private WorkHistoryService workHistoryService;


    @PostMapping
    @ApiOperation("创建工作经历问卷数据")
    @LogAspect
    public ResponseEntity<Data<Boolean>> create(@RequestBody WorkHistoryDTO workHistoryDTO) {
        log.info("请求创建工作经历，{}", workHistoryDTO);
        return ResponseUtils.res(workHistoryService.create(workHistoryDTO));
    }


    @DeleteMapping
    @ApiOperation("删除工作经历基础调查问卷数据(单条)")
    @LogAspect
    public ResponseEntity<Data<Boolean>> delete(@RequestParam("id") @ApiParam("工作经历 id") Integer id) {
        log.info("请求删除 work_history 数据 id:{}", id);
        if (id == null) {
            throw new CommonException("id 为空");
        }
        return ResponseUtils.res(workHistoryService.delete(id));
    }

    @DeleteMapping("/s")
    @ApiOperation("删除工作经历基础调查问卷数据(多条)")
    @LogAspect
    public ResponseEntity<Data<Boolean>> deletes(@RequestParam("ids") @ApiParam("工作经历 ids") List<Integer> ids) {
        log.info("请求删除 work_history 数据 ids:{}", ids);
        return ResponseUtils.res(workHistoryService.delete(ids));
    }


    @DeleteMapping("/extend")
    @ApiOperation("删除工作经历扩展表")
    @LogAspect
    public ResponseEntity<Data<Boolean>> deleteExtend(@RequestBody List<Integer> ids) {
        log.info("请求删除工作经历扩展数据 ids:{}", ids);
        return ResponseUtils.res(workHistoryService.deleteExtend(ids));
    }


    @PutMapping
    @ApiOperation("更新工作经历调查问卷数据")
    @LogAspect
    public ResponseEntity<Data<Boolean>> updates(@RequestBody @ApiParam("工作经历") WorkHistoryDTO workHistoryDTO) {
        log.info("请求更新 work_history 数据：{}", workHistoryDTO);
        return ResponseUtils.res(workHistoryService.update(workHistoryDTO.getWorkHistories(), workHistoryDTO.getWorkHistoryExtends()));
    }


    @GetMapping("baseId/{baseId}")
    @ApiOperation("通过基础信息 id 获取工作经历数据")
    public ResponseEntity<Data<Object>> obtainByBaseId(@PathVariable("baseId") @ApiParam("基础信息 id") Integer baseId) {
        log.info("请求获取求职信息通过基础信息表 id ：{}", baseId);
        return ResponseUtils.res(workHistoryService.get(baseId));
    }


    @DeleteMapping("baseId/{baseId}")
    @ApiOperation("通过 baseId 删除工作经历相关数据")
    @LogAspect
    public ResponseEntity<Data<Boolean>> deleteByBaseId(@PathVariable("baseId") @ApiParam("基础信息 id") Integer baseId) {
        log.info("请求删除，通过 baseId:{}", baseId);
        return ResponseUtils.res(workHistoryService.deleteByBaseId(baseId));
    }
}
