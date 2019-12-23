package io.choerodon.api.v1.controller;

import com.google.common.collect.ImmutableMap;
import io.choerodon.api.dto.CustomerResourcesDTO;
import io.choerodon.app.service.CertificateService;
import io.choerodon.app.service.CustomerResourcesService;
import io.choerodon.infra.annotation.LogAspect;
import io.choerodon.infra.exception.CommonException;
import io.choerodon.infra.utils.web.ResponseUtils;
import io.choerodon.infra.utils.web.dto.Data;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;


/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-08-20 13:37
 */
@RestController
@RequestMapping("custom/resources")
@Slf4j
@Api(description = "客户资源相关")
public class CustomResourcesController {


    @Autowired
    private  CustomerResourcesService customerResourcesService;

    @Autowired
    private  CertificateService certificateService;


    @PostMapping
    @ApiOperation("创建客户资源调查问卷数据(单条)")
    @LogAspect
    public ResponseEntity<Data<Boolean>> create(@RequestBody @ApiParam("客户资源信息") CustomerResourcesDTO customerResourcesDTO) {
        log.info("请求创建 customer resources 记录: {}", customerResourcesDTO);
        return ResponseUtils.res(customerResourcesService.create(customerResourcesDTO));
    }


    @PostMapping("/list")
    @ApiOperation("创建客户资源调查问卷数据(多条) ")
    @LogAspect
    public ResponseEntity<Data<Boolean>> creates(@RequestBody @ApiParam("多条客户资源信息") List<CustomerResourcesDTO> customerResourcesDTOs) {
        log.info("请求创建 customer resources 记录: {}", customerResourcesDTOs);
        return ResponseUtils.res(customerResourcesService.creates(customerResourcesDTOs));
    }


    @PutMapping
    @ApiOperation("更新客户资源问卷数据(单条,子表数据通过子表的 id 更新)")
    @LogAspect
    public ResponseEntity<Data<Boolean>> update(@RequestBody @ApiParam("客户资源信息") CustomerResourcesDTO customerResourcesDTO) {
        log.info("请求更新 customer resources 数据：{}", customerResourcesDTO);
        if (customerResourcesDTO.getId() == null) {
            throw new CommonException("id 为空");
        }
        return ResponseUtils.res(customerResourcesService.update(customerResourcesDTO));
    }


    @PutMapping("/s")
    @ApiOperation("更新客户资源问卷数据(多条,子表数据通过子表的 id 更新)")
    @LogAspect
    public ResponseEntity<Data<Boolean>> updates(@RequestBody @ApiParam("多条客户资源信息") List<CustomerResourcesDTO> customerResourcesDTOs) {
        log.info("请求更新 customer resources 数据：{}", customerResourcesDTOs);
        return ResponseUtils.res(customerResourcesService.update(customerResourcesDTOs));
    }


    @DeleteMapping
    @ApiOperation("删除客户资源问卷数据，根据 id ")
    @LogAspect
    public ResponseEntity<Data<Boolean>> delete(@RequestParam("id") @ApiParam("客户资源的 id") Integer id) {
        log.info("请求删除 customer resources 数据 id:{}", id);
        if (id == null) {
            throw new CommonException("id 为空");
        }
        return ResponseUtils.res(customerResourcesService.delete(id));
    }


    @DeleteMapping("/baseId")
    @ApiOperation("删除客户资源（所有相关数据）,根据 baseId")
    @LogAspect
    public ResponseEntity<Data<Boolean>> deleteByBaseId(@RequestParam("baseId") @ApiParam("基础表 id") Integer baseId) {
        log.info("请求删除资源信息通过 baseId:{}", baseId);
        return ResponseUtils.res(customerResourcesService.deleteByBaseId(baseId));
    }


    @PostMapping("/certificate/upload")
    @ApiOperation("上传客户资源文件，返回文件存入数据后的记录 id")
    @LogAspect
    public ResponseEntity<Data<Map<String, Integer>>> uploadCertificate(@RequestParam("file") @ApiParam("凭证文件") MultipartFile file) throws IOException {
        log.info("上传凭证文件进数据库: file name :{}", file.getOriginalFilename());
        Integer id = customerResourcesService.uploadFile(file);
        return ResponseUtils.res(ImmutableMap.of("id", id));
    }


    @PostMapping("/certificate/uploads")
    @ApiOperation("上传多个客户资源文件，返回文件存入数据后的记录 id")
    @LogAspect
    public ResponseEntity<Data<ImmutableMap<String, List<Integer>>>> uploadCertificates(@RequestParam("files") MultipartFile[] files) throws IOException {
        log.info("上传凭证文件进数据库(多个) ");
        List<Integer> ids = customerResourcesService.uploadFiles(files);
        return ResponseUtils.res(ImmutableMap.of("ids", ids));
    }


//    @GetMapping("/file/{id}")
//    @ApiOperation("下载文件")
//    @LogAspect
//    public void getFile(@PathVariable("id") Integer id, HttpServletResponse response) throws IOException {
//        log.info("获取文件 id: {}", id);
//        CustomerResourcesCertificate customerResourcesCertificate = customerResourcesCertificateMapper.selectByPrimaryKey(id);
//        response.setContentType("application/force-download");
//        response.addHeader("Content-Disposition", "attachment;fileName=" + customerResourcesCertificate.getName());// 设置文件名
//        OutputStream os = response.getOutputStream();
//        os.write(customerResourcesCertificate.getFile());
//    }


    @GetMapping("/baseId/{baseId}")
    @ApiOperation("获取客户资源数据")
    @LogAspect
    public ResponseEntity<Data<ImmutableMap<String, Object>>> obtainByBaseId(@PathVariable("baseId") @ApiParam("基础信息 id") Integer baseId) {
        log.info("请求获取客户资源数据，通过 base id:{}", baseId);
        return ResponseUtils.res(customerResourcesService.obtainByBaseId(baseId));
    }

    @DeleteMapping("/certificate")
    @ApiOperation("删除凭证文件通过凭证 id,同时删除 oss 中文件")
    @LogAspect
    public ResponseEntity<Data<Boolean>> deleteCertificateId(@RequestParam("certificateId") Integer certificateId) {
        log.info("请求删除凭证文件");
        return ResponseUtils.res(certificateService.deleteById(certificateId));
    }


    @DeleteMapping("/certificate/child")
    @ApiOperation("删除凭证文件通过 客户子表 id，同时相关 oss中文件 ")
    public ResponseEntity<Data<Boolean>> deleteByChildId(@RequestParam("childId") @ApiParam("childId") Integer childId) {
        log.info("请求删除凭证文件通过 child id :{}", childId);
        return ResponseUtils.res(certificateService.deleteByChildId(childId));
    }


}
