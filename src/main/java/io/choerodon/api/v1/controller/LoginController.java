package io.choerodon.api.v1.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.choerodon.infra.utils.HttpUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.jsoup.Connection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;


/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-09-04 10:14
 */
@RestController
@RequestMapping("login")
@Slf4j
@Api(description = "小程序登陆相关")
public class LoginController {

    @Value("${wechat.appid}")
    private String appid;

    @Value("${wechat.secret}")
    private String secret;

    @GetMapping
    @ApiOperation("通过 code 获取 session")
    public Map<String, Object> login(@RequestParam("code") String code) throws IOException {
        log.info("请求登陆 code:{}", code);

        String request = "https://api.weixin.qq.com/sns/jscode2session?appid=" +
                appid + "&secret=" +
                secret + "&js_code=" + code +
                "&grant_type=authorization_code";
        Connection.Response response =
                HttpUtils.get(request);
        log.info("login info :{}", response.body());
        return (JSONObject) JSONObject.parse(response.body());
    }


    @GetMapping("/decrypt")
    @ApiOperation("解密手机号")
    public String decrypt(@RequestParam("keyStr") String keyStr,
                          @RequestParam("ivStr") String ivStr,
                          @RequestParam("encDataStr") String encDataStr) throws Exception {

        log.info("请求解密手机号: keyStr:{},ivStr:{}, encDataStr:{} ", keyStr, ivStr, encDataStr);

        byte[] encData = Base64.decodeBase64(encDataStr);
        byte[] iv = Base64.decodeBase64(ivStr);
        byte[] key = Base64.decodeBase64(keyStr);

        AlgorithmParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        return new String(cipher.doFinal(encData), StandardCharsets.UTF_8);
    }


//    @GetMapping("/token")
//    @ApiOperation("小程序获取 access_token")
//    public Map<String, Object> getAccessToken() throws IOException {
//        log.info("请求登陆 access_token");
//        String request = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" +
//                appid + "&secret=" +
//                secret;
//        Connection.Response response =
//                HttpUtils.get(request);
//        log.info("login info :{}", response.body());
//        return (JSONObject) JSONObject.parse(response.body());
//    }


    @GetMapping("/qc")
    @ApiOperation("获取小程序二维码")
    public byte[] getQC(@RequestParam("baseId") Integer baseId,
                        @RequestParam("width") @ApiParam("二维码宽度") Integer width) throws IOException {

        log.info("请求获取小程序二维码 baseId={}, width={}", baseId, width);
//        获取 access_token
        String tokenStr = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" +
                appid + "&secret=" + secret;
        Connection.Response response =
                HttpUtils.get(tokenStr);
        JSONObject jsonObject = (JSONObject) JSON.parse(response.body());
        String token = (String) jsonObject.get("access_token");

//        获取二维码
        String getQC = "https://api.weixin.qq.com/cgi-bin/wxaapp/createwxaqrcode?access_token=" + token;
        Map<String, String> params = new HashMap<>();
        params.put("path", "?baseId=" + baseId);
        params.put("width", String.valueOf(width));


        BufferedInputStream bufferedInputStream = HttpUtils.post(getQC, JSON.toJSONString(params)).bodyStream();
        return StreamUtils.copyToByteArray(bufferedInputStream);
    }


}
