package io.choerodon.api.v1.controller;

import com.github.pagehelper.PageInfo;
import io.choerodon.api.dto.*;
import io.choerodon.app.service.ComprehensiveReportService;
import io.choerodon.app.service.ExcelService;
import io.choerodon.app.service.PersonalReportService;
import io.choerodon.infra.annotation.LogAspect;
import io.choerodon.infra.config.security.AuthoritiesConstants;
import io.choerodon.infra.utils.web.ResponseUtils;
import io.choerodon.infra.utils.web.dto.Data;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-09-17 16:14
 */
@RestController
@RequestMapping("api/report")
@Slf4j
@Api(description = "报告部分")
public class ReportController {

    @Autowired
    private PersonalReportService personalReportService;

    @Autowired
    private ComprehensiveReportService comprehensiveReportService;

    @Autowired
    private ExcelService excelService;


    @PostMapping
    @LogAspect
    @ApiOperation("搜索个人报告（管理员）")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Data<PageInfo<PersonalReportDTO>>> searchAdmin(@RequestParam("page") Integer page,
                                                                         @RequestParam("size") Integer size,
                                                                         @RequestParam(value = "demandId", required = false) @ApiParam("需求 id") Integer demandId,
                                                                         @RequestParam(value = "status", required = false) @ApiParam("简历状态") String status,
                                                                         @RequestParam(value = "phone", required = false) String phone,
                                                                         @RequestParam(value = "name", required = false) String name) {

        log.info("请求搜索个人报告记录");
        Map<String, Object> map = new HashMap<>();
        map.put("phone", phone);
        map.put("name", name);
        map.put("status", status);
        map.put("demandId", demandId);
        return ResponseUtils.res(personalReportService.search(page, size, map));
    }


    @GetMapping("/personal/admin/{baseId}")
    @LogAspect
    @ApiOperation("获取个人报告(管理员)")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ANONYMOUS + "\")")
    public ResponseEntity<Data<PersonalReportDTO>> obtainPersonalByBaseId1(@PathVariable("baseId") Integer baseId) {
        log.info("请求获取个人报告记录 baseId:{}", baseId);
        return ResponseUtils.res(personalReportService.obtainPersonalByBaseId(baseId));
    }

    @GetMapping("/personal/hr")
    @LogAspect
    @ApiOperation("获取个人报告(HR)")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.USER + "\")")
    public ResponseEntity<Data<PersonalReportDTO>> obtainPersonalByBaseId2(@RequestParam("baseId") Integer baseId,
                                                                           @RequestParam("demandId") @ApiParam("需求Id") Integer demandId) {

        log.info("请求获取个人报告记录 baseId:{},demandId:{}", baseId, demandId);
        return ResponseUtils.res(personalReportService.obtainByBase(baseId, demandId));
    }


    @PutMapping("/personal")
    @LogAspect
    @ApiOperation("更新个人报告，手输部分、推送状态")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Data<Boolean>> updatePersonalReport(@RequestBody ReportMidDTO reportMidDTO) {
        log.info("请求更新个人报告");
        return ResponseUtils.res(personalReportService.update(reportMidDTO));
    }


    @DeleteMapping
    @LogAspect
    @ApiOperation("删除个人报告")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Data<Boolean>> deletePersonalReport(@RequestParam("id") Integer id) {
        log.info("请求删除个人报告");
        return ResponseUtils.res(personalReportService.delete(id));
    }


    @PostMapping("init/{baseId}/personal")
    @ApiOperation("生成个人报告")
    @LogAspect
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Data<Boolean>> init(@PathVariable("baseId") Integer baseId) {
        return ResponseUtils.res(personalReportService.initPersonalReport(baseId));
    }

//    整合到获取综合报告 api
//    @PostMapping("init/{baseId}/comprehensive")
//    @ApiOperation("生成综合报告报告")
//    @LogAspect
//    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
//    public ResponseEntity<Data<Boolean>> initCom(@PathVariable("baseId") Integer baseId) {
//        return ResponseUtils.res(comprehensiveReportService.init(baseId));
//    }


    @PostMapping("/postReport/create")
    @ApiOperation("创建推送简历")
    @LogAspect
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Data<Boolean>> createReportList(@RequestBody List<ReportListDTO> reportListDTOS) {
        log.info("请求创建推送简历 ,{}", reportListDTOS);
        return ResponseUtils.res(personalReportService.createReportList(reportListDTOS));
    }


    @PostMapping("/postReport/search")
    @ApiOperation("搜索简历列表（HR）,只能看见与自己发布的需求相关的简历")
    @LogAspect
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.USER + "\")")
    public ResponseEntity<Data<PageInfo<ReportListDTO>>> searchReportList1(@RequestParam("page") Integer page,
                                                                           @RequestParam("size") Integer size,
                                                                           @RequestBody ReportListDTO reportListDTO) {
        log.info("请求搜索推送简历列表 page={},size={}.{}", page, size, reportListDTO);
        Map<String, Object> params = new HashMap<>();
        params.put("name", reportListDTO.getName());
        params.put("startTime", reportListDTO.getStartTime());
        params.put("endTime", reportListDTO.getEndTime());
        params.put("phone", reportListDTO.getPhone());
        params.put("status", reportListDTO.getStatus());
        return ResponseUtils.res(personalReportService.obtainReportList(page, size, params));
    }


    @PostMapping("/postReport/search/admin/all")
    @ApiOperation("搜索简历列表（管理员），所有的已推荐简历")
    @LogAspect
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Data<PageInfo<ReportListDTO>>> searchReportList2(@RequestParam("page") Integer page,
                                                                           @RequestParam("size") Integer size,
                                                                           @RequestParam(value = "phone", required = false) String phone,
                                                                           @RequestParam(value = "name", required = false) String name,
                                                                           @RequestParam("status") @ApiParam("推荐状态") Integer status) {

        log.info("请求搜索推送简历列表 page={},size={}.status ={}", page, size, status);
        return ResponseUtils.res(personalReportService.obtainReportListByStatus(page, size, status, name, phone));
    }


    @GetMapping("/postReport/search/admin")
    @LogAspect
    @ApiOperation("获取当前需求已推荐简历列表（管理员）")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Data<PageInfo<ReportListDTO>>> obtainReportList2(@RequestParam(value = "name", required = false) String name,
                                                                           @RequestParam(value = "phone", required = false) String phone,
                                                                           @RequestParam("demandId") Integer demandId,
                                                                           @RequestParam("page") Integer page,
                                                                           @RequestParam("size") Integer size) {
        log.info("请求获取推荐简历，通过 demandId={},name={},phone={} ,page={},size={},name={},phone={}",
                demandId, name, phone, page, size, name, phone);
        return ResponseUtils.res(personalReportService.obtainByParams(name, phone, demandId, page, size));
    }


    @PutMapping("/postReport/status")
    @LogAspect
    @ApiOperation("更新推荐简历的状态")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.USER + "\")")
    public ResponseEntity<Data<Boolean>> updateReportListStatus(@RequestBody ReportListDTO reportListDTO) {
        log.info("请求更新简历的状态,{}", reportListDTO);
        return ResponseUtils.res(personalReportService.updateReportList(reportListDTO));
    }


    @GetMapping("/comprehensive/admin/{baseId}")
    @LogAspect
    @ApiOperation("获取综合报告，通过 baseId（admin）")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.USER + "\")")
    public ResponseEntity<Data<ComprehensiveReportDTO>> obtainComprehensive1(@PathVariable("baseId") Integer baseId) {
        log.info("请求获取综合报告通过 baseId:{}", baseId);
        return ResponseUtils.res(comprehensiveReportService.obtainByBaseId(baseId));
    }


    @GetMapping("/comprehensive/HR")
    @LogAspect
    @ApiOperation("获取综合报告（HR）")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.USER + "\")")
    public ResponseEntity<Data<ComprehensiveReportDTO>> obtainComprehensive2(@RequestParam("baseId") Integer baseId,
                                                                             @RequestParam("demandId") Integer demandId) {

        log.info("请求获取综合报告通过 baseId:{},demandId:{}", baseId, demandId);
        return ResponseUtils.res(comprehensiveReportService.obtainByBaseId(baseId, demandId));
    }


    @PostMapping("/comprehensive/rewrite")
    @LogAspect
    @ApiOperation("重新生成综合报告，通过 baseId （管理员）")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Data<Boolean>> overWriteCom(@RequestParam("baseId") Integer baseId) {
        log.info("请求重新生成综合报告：{}", baseId);
        return ResponseUtils.res(comprehensiveReportService.reInitReport(baseId));
    }


    @DeleteMapping("/comprehensive/{baseId}")
    @LogAspect
    @ApiOperation("删除综合报告，通过 baseId （管理员）")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Data<Boolean>> deleteComprehensive(@PathVariable("baseId") Integer baseId) {
        log.info("请求删除综合报告：{}", baseId);
        return ResponseUtils.res(comprehensiveReportService.delete(baseId));
    }


    @PutMapping("/personal/init/all")
    @LogAspect
    @ApiOperation("生成所有个人报告")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Data<Boolean>> initPersonalAll() {
        log.info("请求生成所有个人报告");
        return ResponseUtils.res(personalReportService.initPersonalReportAll());
    }


    @GetMapping("/personal/mobile/{baseId}")
    @LogAspect
    @ApiOperation("获取手机端个人报告")
    public ResponseEntity<Data<PersonalReportMobileDTO>> getPersonalReportMob(@PathVariable("baseId") Integer baseId) {
        log.info("请求获取个人报告手机端: base={}", baseId);
        return ResponseUtils.res(personalReportService.obtainMobile(baseId));
    }




}
