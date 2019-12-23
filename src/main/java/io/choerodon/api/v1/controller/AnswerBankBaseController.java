package io.choerodon.api.v1.controller;

import com.google.common.collect.ImmutableMap;
import io.choerodon.api.dto.AnswerBankBaseDTO;
import io.choerodon.api.dto.BaseInfoDTO;
import io.choerodon.api.dto.ResourceDTO;
import io.choerodon.app.service.AnswerBankBaseService;
import io.choerodon.app.service.BaseInfoService;
import io.choerodon.app.service.ExcelService;
import io.choerodon.app.service.PersonalReportService;
import io.choerodon.domain.BaseUser;
import io.choerodon.infra.annotation.LogAspect;
import io.choerodon.infra.exception.CommonException;
import io.choerodon.infra.utils.web.ResponseUtils;
import io.choerodon.infra.utils.web.dto.Data;
import io.netty.util.internal.UnstableApi;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;


/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-08-20 10:19
 */
@RestController
@RequestMapping("v1/answerBankBase")
@Slf4j
@Api(description = "个人信息调查问卷相关")
public class AnswerBankBaseController {

    @Autowired
    private AnswerBankBaseService answerBankBaseService;

    @Autowired
    private BaseInfoService baseInfoService;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private ExcelService excelService;


    @PostMapping
    @ApiOperation("创建个人信息调查问卷数据")
    @LogAspect
    public ResponseEntity<Data<Map<String, Integer>>> create(@RequestBody @ApiParam("个人信息") AnswerBankBaseDTO answerBankBaseDTO) {

        log.info("请求创建 answer bank base 记录: {} ", answerBankBaseDTO);
//        返回创建完成后的 id
        Integer id = answerBankBaseService.create(answerBankBaseDTO);
        return ResponseUtils.res(ImmutableMap.of("id", id));
    }


    @PutMapping
    @ApiOperation("更新个人信息调查问卷数据")
    @LogAspect
    public ResponseEntity<Data<Boolean>> update(@RequestBody @ApiParam("个人信息") AnswerBankBaseDTO answerBankBaseDTO) {
        log.info("请求更新 answer_bank_base 数据：{}", answerBankBaseDTO);
        if (answerBankBaseDTO.getId() == null) {
            throw new CommonException("id 为空");
        }
        return ResponseUtils.res(answerBankBaseService.update(answerBankBaseDTO));
    }


    @DeleteMapping
    @ApiOperation("删除个人信息调查问卷数据")
    @LogAspect
    public ResponseEntity<Data<Boolean>> delete(@RequestParam("id") @ApiParam("个人信息 id") Integer id) {
        log.info("请求删除 answer_bank_base 数据 id:{}", id);
        if (id == null) {
            throw new CommonException("id 为空");
        }
        return ResponseUtils.res(answerBankBaseService.delete(id));
    }


    @GetMapping
    @ApiOperation("通过微信 id 获取个人信息调查问卷")
    @LogAspect
    public ResponseEntity<Data<AnswerBankBaseDTO>> obtainByWechatId(@RequestParam("wechatId") String wechatId) {
        log.info("请求获取个人信息调查问卷，通过 wechat id :{}", wechatId);
        return ResponseUtils.res(answerBankBaseService.getByWechatId(wechatId));
    }

    /**
     * 本地数据导出接口
     */
//    @GetMapping("/excel/get/{baseId}")
//    @ApiOperation("通过 baseId 获取下载 excel ")
//    @LogAspect
//    public void getExcel(@PathVariable("baseId") Integer baseId, HttpServletResponse response) {
//        baseInfoService.exportExcel(baseId, response);
//    }
    @GetMapping("/excel/list")
    @ApiOperation("导出所有的数据")
    @LogAspect
    public void getExcel(HttpServletResponse response) {
        Map<String, Object> params = null;
        baseInfoService.exportExcel(params, response);
    }


    @GetMapping("/synthesis/{baseId}")
    @ApiOperation("获取个人信息（原多个调查问卷综合）")
    @LogAspect
    public ResponseEntity<Data<BaseInfoDTO>> obtainBaseInfo(@PathVariable("baseId") Integer baseId) throws InterruptedException {
        log.info("请求获取个人信息 baseId:{}", baseId);
        return ResponseUtils.res(baseInfoService.getBaseInfoByBaseId(baseId));
    }


    @PostMapping("/synthesis")
    @ApiOperation("创建或更新个人综合信息")
    @LogAspect
    public ResponseEntity<Data<Boolean>> saveAndUpdateBaseInfo(@RequestBody BaseInfoDTO baseInfoDTO) throws InterruptedException {
        log.info("请求创建或者更新个人信息 {}", baseInfoDTO);
        return ResponseUtils.res(baseInfoService.saveAndUpdate(baseInfoDTO));
    }


    @GetMapping("/home/{baseId}")
    @ApiOperation("获取用户会员、卡包信息、填写状态")
    @LogAspect
    public ResponseEntity<Data<ResourceDTO>> obtainHomeInfo(@PathVariable("baseId") Integer baseId) throws InterruptedException {
        log.info("请求获取主页信息 baseId:{}", baseId);
        return ResponseUtils.res(baseInfoService.getResource(baseId));
    }

    @PutMapping("/old/import")
    @ApiOperation("同步旧数据，处理卡片的获取")
    @LogAspect
    public String importOldDate() {
        log.info("未获的卡片以及会员检测");
        taskExecutor.execute(() -> baseInfoService.checkResources());
        return "success";
    }

    @GetMapping("/excel/invitation")
    @ApiOperation("请求导出邀请链")
    @LogAspect
    public void getInvitationExcel(HttpServletResponse response) {
        log.info("请求导出邀请链");
        baseInfoService.getInvitationExcel(response);
    }


    @GetMapping("/excel/personal-report/download")
    @LogAspect
    @ApiOperation("下载个人报告 excel")
    public String downloadPersonalExcel(HttpServletResponse response) {
        log.info("请求导出报告 excel 文件");
        excelService.exportPersonReport(response);
        return "success";
    }

    @GetMapping("/excel/comprehensive-report/download")
    @LogAspect
    @ApiOperation("下载综合报告 excel")
    public String downloadComExcel(HttpServletResponse response) {
        log.info("请求导出报告 excel 文件");
        excelService.exportComprehensiveReport(response);
        return "success";
    }
}
