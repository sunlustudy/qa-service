package io.choerodon.api.v1.controller;

import io.choerodon.api.dto.CharacterPowerDTO;
import io.choerodon.app.service.CharacterPowerService;
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
 * @create: 2019-08-20 11:19
 */
@RestController
@RequestMapping("character")
@Api(description = "性格特质相关控制")
@Slf4j
public class CharacterPowerController {

    @Autowired
    private  CharacterPowerService characterPowerService;


    @PostMapping
    @ApiOperation("创建性格调查问卷数据")
    @LogAspect
    public ResponseEntity<Data<Boolean>> create(@RequestBody @ApiParam("性格信息") CharacterPowerDTO characterPowerDTO) {
        log.info("请求创建 answer bank base 记录: {}", characterPowerDTO);
        return ResponseUtils.res(characterPowerService.create(characterPowerDTO));
    }


    @PutMapping
    @ApiOperation("更新性格调查问卷数据")
    @LogAspect
    public ResponseEntity<Data<Boolean>> update(@RequestBody @ApiParam("性格信息") CharacterPowerDTO characterPowerDTO) {
        log.info("请求更新 answer_bank_base 数据：{}", characterPowerDTO);
        if (characterPowerDTO.getId() == null) {
            throw new CommonException("id 为空");
        }
        return ResponseUtils.res(characterPowerService.update(characterPowerDTO));
    }


    @DeleteMapping
    @ApiOperation("删除性格调查问卷数据，根据 id")
    @LogAspect
    public ResponseEntity<Data<Boolean>> delete(@RequestParam("id") @ApiParam("性格信息的 id") Integer id) {
        log.info("请求删除 answer_bank_base 数据 id:{}", id);
        if (id == null) {
            throw new CommonException("id 为空");
        }
        return ResponseUtils.res(characterPowerService.delete(id));
    }


    @GetMapping("/{baseId}")
    @ApiOperation("通过基础信息表的 id 获取")
    public ResponseEntity<Data<CharacterPowerDTO>> getById(@PathVariable("baseId") Integer baseId) {
        log.info("请求获取性格问卷信息: baseId: {}", baseId);
        return ResponseUtils.res(characterPowerService.get(baseId));
    }


}
