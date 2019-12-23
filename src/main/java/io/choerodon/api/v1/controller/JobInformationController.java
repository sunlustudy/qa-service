package io.choerodon.api.v1.controller;

import io.choerodon.api.dto.JobInformationDTO;
import io.choerodon.app.service.JobInformationService;
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
import org.springframework.web.bind.annotation.*;


/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-08-20 14:09
 */
@RestController
@RequestMapping("job/info")
@Slf4j
@Api(description = "求职信息相关控制")
public class JobInformationController {

    @Autowired
    private  JobInformationService jobInformationService;



    @PostMapping
    @ApiOperation("创建求职信息")
    @LogAspect
    public ResponseEntity<Data<Boolean>> create(@RequestBody @ApiParam("求职信息") JobInformationDTO jobInformationDTO) {
        log.info("请求创建 professional skill 记录: {}", jobInformationDTO);
        return ResponseUtils.res(jobInformationService.create(jobInformationDTO));
    }


    @PutMapping
    @ApiOperation("更新求职信息")
    @LogAspect
    public ResponseEntity<Data<Boolean>> update(@RequestBody @ApiParam("求职信息") JobInformationDTO jobInformationDTO) {
        log.info("请求更新 professional skill 数据：{}", jobInformationDTO);
        if (jobInformationDTO.getId() == null) {
            throw new CommonException("id 为空");
        }
        return ResponseUtils.res(jobInformationService.update(jobInformationDTO));
    }


    @DeleteMapping
    @ApiOperation("删除求职信息")
    @LogAspect
    public ResponseEntity<Data<Boolean>> delete(@RequestParam("id") @ApiParam("求职信息 id") Integer id) {
        log.info("请求删除 professional skill 数据 id:{}", id);
        if (id == null) {
            throw new CommonException("id 为空");
        }
        return ResponseUtils.res(jobInformationService.delete(id));
    }


    @GetMapping("baseId/{baseId}")
    @ApiOperation("通过基础信息 id 获取求职信息")
    public ResponseEntity<Data<JobInformationDTO>> obtainByBaseId(@PathVariable("baseId") @ApiParam("基础信息 id") Integer baseId) {
        log.info("请求获取求职信息通过基础信息表 id ：{}", baseId);
        return ResponseUtils.res(jobInformationService.get(baseId));
    }


}
