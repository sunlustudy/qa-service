package io.choerodon.app.service;

import com.sun.org.apache.xpath.internal.operations.Bool;
import io.choerodon.api.dto.BaseInfoDTO;
import io.choerodon.api.dto.BaseRelationDTO;
import io.choerodon.api.dto.ResourceDTO;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

/*
 * @program: springboot
 * @author: syun
 * @create: 2019-08-27 10:58
 */
public interface BaseInfoService {

    /**
     * 绑定手机号与微信号
     *
     * @param baseRelationDTO
     * @return
     */
    Boolean bind(BaseRelationDTO baseRelationDTO);

    /**
     * 检验验证码绑定信息
     *
     * @param baseRelationDTO
     * @param code
     * @return
     */
    Boolean checkAndBind(BaseRelationDTO baseRelationDTO, String code);

    boolean isSubmit(Integer BaseId);

    /**
     * 通过微信号获取绑定信息
     *
     * @param wechatId
     * @return
     */
    Map<String, Object> obtainByWechatId(String wechatId);

    /**
     * 解除绑定信息
     *
     * @param baseRelationDTO
     * @return
     */
    Boolean unBind(BaseRelationDTO baseRelationDTO);

    /**
     * 发送短信验证码
     *
     * @param phone
     * @param wechatId
     * @return
     */
    boolean sendCode(String phone, String wechatId);

    /**
     * 发送预约信息
     *
     * @param wechatId
     * @param date
     * @param address
     * @return
     */
    Boolean sendOrderInfo(String wechatId, Date date, String address);

    void exportExcel(Map<String, Object> params, HttpServletResponse response);

    void exportExcel(Integer baseId, HttpServletResponse response);

    BaseInfoDTO getBaseInfoByBaseId(Integer baseId) throws InterruptedException;

    Boolean saveAndUpdate(BaseInfoDTO baseInfoDTO) throws InterruptedException;


    /**
     * 处理卡片的获取
     * @param baseId
     */
    void handleCardGet(Integer baseId);

    /**
     * 获取用户会员，卡包，积分（未定）信息
     */
    ResourceDTO getResource(Integer baseId) throws InterruptedException;

    /**
     * 计算基础信息完成度
     */
    Double calBaseInfoCompetePer(Integer baseId);

    /**
     * 基础信息是否填写完成
     */
    Boolean isBaseInfoCompete(Integer baseId);

    Double calResourcesCompletePer(Integer baseId);

    int  getCompletePer(Integer baseId);

    void checkResources();

    /**
     * 获取邀请树状结构
     * @param response
     */
    void getInvitationExcel(HttpServletResponse response);
}
