package io.choerodon.api.v1.controller;

import io.choerodon.api.dto.ProfessionalSkillsDTO;
import io.choerodon.app.service.ProfessionalSkillsService;
import io.choerodon.infra.annotation.LogAspect;
import io.choerodon.infra.exception.CommonException;
import io.choerodon.infra.utils.web.ResponseUtils;
import io.choerodon.infra.utils.web.dto.Data;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;


/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-08-20 11:25
 */
@RestController
@RequestMapping("skills")
@Slf4j
@Api(description = "专业技能相关")
public class ProfessionalSkillsController {

    @Autowired
    private  ProfessionalSkillsService professionalSkillsService;



    @PostMapping
    @ApiOperation("创建专业技能问卷数据")
    @LogAspect
    public ResponseEntity<Data<Boolean>> create(@RequestBody @ApiParam("专业技能") ProfessionalSkillsDTO professionalSkillsDTO) {
        log.info("请求创建 professional skill 记录: {}", professionalSkillsDTO);
        return ResponseUtils.res(professionalSkillsService.create(professionalSkillsDTO));
    }


    @PutMapping
    @ApiOperation("更新专业技能调查问卷数据")
    @LogAspect
    public ResponseEntity<Data<Boolean>> update(@RequestBody @ApiParam("专业技能") ProfessionalSkillsDTO professionalSkillsDTO) {
        log.info("请求更新 professional skill 数据：{}", professionalSkillsDTO);
        if (professionalSkillsDTO.getId() == null) {
            throw new CommonException("id 为空");
        }
        return ResponseUtils.res(professionalSkillsService.update(professionalSkillsDTO));
    }


    @DeleteMapping
    @ApiOperation("删除专业技能调查问卷数据")
    @LogAspect
    public ResponseEntity<Data<Boolean>> delete(@RequestParam("id") @ApiParam("专业技能 id") Integer id) {
        log.info("请求删除 professional skill 数据 id:{}", id);
        if (id == null) {
            throw new CommonException("id 为空");
        }
        return ResponseUtils.res(professionalSkillsService.delete(id));
    }


    @GetMapping("baseId/{baseId}")
    @ApiOperation("通过基础信息 id 获取专业技能信息")
    public ResponseEntity<Data<ProfessionalSkillsDTO>> obtainByBaseId(@PathVariable("baseId") @ApiParam("基础信息 id") Integer baseId) {
        log.info("请求获取求职信息通过基础信息表 id ：{}", baseId);
        return ResponseUtils.res(professionalSkillsService.get(baseId));
    }
}
