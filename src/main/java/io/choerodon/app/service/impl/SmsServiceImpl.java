package io.choerodon.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import io.choerodon.infra.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-08-28 11:02
 */
@Slf4j
@Service
public class SmsServiceImpl {


    private final IAcsClient iAcsClient;

    @Value("${ali.sms.sign-name}")
    private String signName;

    @Value("${ali.sms.template-code.code}")
    private String codeTemplate;

    @Value("${ali.sms.template-code.order}")
    private String orderTemplate;

    @Value("${ali.sms.template-code.register}")
    private String registerTemplate;

    @Value("${ali.sms.template-code.changePWD}")
    private String changePWDTemplate;


    public SmsServiceImpl(IAcsClient iAcsClient) {
        this.iAcsClient = iAcsClient;
    }


    private Boolean sendSms(String templateCode, String templateParam, String phone) {
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("TemplateParam", templateParam);
        try {
            CommonResponse response = iAcsClient.getCommonResponse(request);
            JSONObject jsonObject = (JSONObject) JSON.parse(response.getData());
            if ("OK".equals(jsonObject.get("Message"))) {
                return true;
            } else {
                throw new CommonException("发送失败");
            }

        } catch (ClientException e) {
            log.error("send code error {}", e.getMessage(), e);
        }

        return false;
    }

    /**
     * 发送短信验证码
     *
     * @param code
     * @param phone
     * @return
     */
    public Boolean sendCode(String code, String phone) {
        String templateParam = "{\"code\":\"" + code + "\"}";
        return sendSms(codeTemplate, templateParam, phone);
    }


    public Boolean sendRegisterCode(String code, String phone) {
        String templateParam = "{\"code\":\"" + code + "\"}";
        return sendSms(registerTemplate, templateParam, phone);
    }


    public Boolean sendChangePWDCode(String code, String phone) {
        String templateParam = "{\"code\":\"" + code + "\"}";
        return sendSms(changePWDTemplate, templateParam, phone);
    }

    /**
     * 发送预约结果信息
     *
     * @param startTime
     * @param endTime
     * @param address
     * @param phone
     * @param name
     * @return
     */
    public Boolean sendOrderInfo(String startTime, String endTime, String address, String phone, String name) {
//             添加模板参数
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        params.put("address", address);
        String templateParam = JSON.toJSON(params).toString();
        log.info("短信参数生成: {}", templateParam);
//        发送
        return sendSms(orderTemplate, templateParam, phone);
    }


}
