package io.choerodon.infra.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
 * @description:
 * @author: syun
 * @create: 2019-08-19 10:59
 */
@Slf4j
public class OssUtils {
    public static Map<String, String> generateAccess(OSS client, Map<String, String> params) {

        // 上传内容到指定的存储空间（bucketName）并保存为指定的文件名称（objectName）。
        try {
            long expireTime = 300;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, params.get("fileName"));

            String postPolicy = client.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes(StandardCharsets.UTF_8);
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = client.calculatePostSignature(postPolicy);

            Map<String, String> respMap = new LinkedHashMap<>();
            respMap.put("accessid", params.get("accessId"));
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", params.get("dir"));
            respMap.put("host", params.get("host"));
            respMap.put("expire", String.valueOf(expireEndTime / 1000));


            return respMap;
        } catch (Exception e) {
            log.warn("generate error {}", e.getMessage());
        }

        return null;
    }


    public static PutObjectResult upload(OSS client, MultipartFile file, String bucketName) {

        log.info("start upload file name:{}", file.getOriginalFilename());
        try {
            return client.putObject(bucketName, file.getOriginalFilename(), file.getInputStream());
        } catch (IOException e) {
            log.error("file upload error {}", e.getMessage(), e);
        }

        return null;
    }


    public static String generateUrl(OSS client, String objectName, String bucketName) {
        Date expiration = new Date(new Date().getTime() + 3600 * 1000);
        URL url = client.generatePresignedUrl(bucketName, objectName, expiration);
        return url.toString();
    }


    public static void deleteObject(OSS client, String bucketName, String objectName) {
        client.deleteObject(bucketName, objectName);
    }


    /**
     * 删除存储的对象
     *
     * @param client
     * @param bucketName
     * @param objectNames
     * @return 删除失败的文件列表
     */
    public static List<String> deleteObject(OSS client, String bucketName, List<String> objectNames) {

        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName).withKeys(objectNames);
//        true 为简单模式，返回删除失败的文件列表

        deleteObjectsRequest.setQuiet(true);
        DeleteObjectsResult deleteObjectsResult = client.deleteObjects(deleteObjectsRequest);

        return deleteObjectsResult.getDeletedObjects();
    }


}
