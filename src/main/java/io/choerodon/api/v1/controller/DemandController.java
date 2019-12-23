package io.choerodon.api.v1.controller;

import com.github.pagehelper.PageInfo;
import io.choerodon.api.dto.DemandDTO;
import io.choerodon.app.service.DemandService;
import io.choerodon.infra.annotation.LogAspect;
import io.choerodon.infra.config.security.AuthoritiesConstants;
import io.choerodon.infra.utils.web.ResponseUtils;
import io.choerodon.infra.utils.web.dto.Data;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;


/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-09-17 17:22
 */
@RestController
@RequestMapping("api/demand")
@Slf4j
@Api(description = "需求相关")
public class DemandController {

    @Autowired
    private  DemandService demandService;


    @PostMapping
    @LogAspect
    @ApiOperation("创建需求")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.USER + "\")")
    public ResponseEntity<Data<Boolean>> create(@RequestBody DemandDTO demandDTO) {
        log.info("请求创建需求 {}", demandDTO);
        return ResponseUtils.res(demandService.create(demandDTO));
    }

    @DeleteMapping
    @LogAspect
    @ApiOperation("删除需求，即关闭需求，通过 id")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.USER + "\")")
    public ResponseEntity<Data<Boolean>> delete(@RequestParam("id") Integer id) {
        log.info("请求删除需求通过 id：{}", id);
        return ResponseUtils.res(demandService.delete(id));
    }

    @PutMapping
    @LogAspect
    @ApiOperation("修改需求，通过 id")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.USER + "\")")
    public ResponseEntity<Data<Boolean>> update(@RequestBody DemandDTO demandDTO) {
        log.info("请求修改需求：{}", demandDTO);
        return ResponseUtils.res(demandService.update(demandDTO));
    }


    @PutMapping("/status")
    @LogAspect
    @ApiOperation("修改需求状态，启用关闭，通过 id")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.USER + "\")")
    public ResponseEntity<Data<Boolean>> updateStatus(@RequestParam("id") Integer id,
                                                      @RequestParam("isDel") Boolean isDel) {
        log.info("请求修改需求：id={},isDel={}", id, isDel);
        return ResponseUtils.res(demandService.updateStatus(id, isDel));
    }


    @PostMapping("/search")
    @LogAspect
    @ApiOperation("搜索、对于 companyName、post、postDescription 模糊匹配（HR）")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.USER + "\")")
    public ResponseEntity<Data<PageInfo<DemandDTO>>> search(@RequestBody DemandDTO demandDTO,
                                                            @RequestParam("size") Integer size,
                                                            @RequestParam("page") Integer page) {


        log.info("请求模糊搜索 {},size = {},page = {}", demandDTO, size, page);
        Map<String, Object> params = new HashMap<>();
        params.put("companyName", demandDTO.getCompanyName());
        params.put("post", demandDTO.getPost());
        params.put("postDescription", demandDTO.getPostDescription());
        params.put("isDel", demandDTO.getIsDel());
        return ResponseUtils.res(demandService.search(page, size, params));
    }


    @PostMapping("/get/recommended")
    @LogAspect
    @ApiOperation("获取候选人已推荐的需求")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Data<PageInfo<DemandDTO>>> getRecommended(
            @RequestParam("size") Integer size,
            @RequestParam("page") Integer page,
            @RequestParam("baseId") @ApiParam("候选人的 baseId") Integer baseId) {
        log.info("请求获取所有已推荐的需求,size = {},page = {}", size, page);
        return ResponseUtils.res(demandService.obtainRecommended(page, size, baseId));
    }

}
