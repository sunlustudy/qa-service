package io.choerodon.app.service.impl;

import io.choerodon.api.dto.AnswerBankBaseDTO;
import io.choerodon.app.service.AnswerBankBaseService;
import io.choerodon.app.service.BaseInfoService;
import io.choerodon.domain.AnswerBankBase;
import io.choerodon.domain.Integral;
import io.choerodon.domain.Invitation;
import io.choerodon.infra.exception.CommonException;
import io.choerodon.infra.mapper.AnswerBankBaseMapper;
import io.choerodon.infra.mapper.InvitationMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static io.choerodon.infra.utils.BeanUtil.convert;
import static io.choerodon.infra.utils.BeanUtil.isNotEmpty;


/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-08-20 10:20
 */
@Service("answerBankBaseService")
@Slf4j
public class AnswerBankBaseServiceImpl implements AnswerBankBaseService {

    @Autowired
    private AnswerBankBaseMapper answerBankBaseMapper;

    @Autowired
    private InvitationMapper invitationMapper;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private BaseInfoService baseInfoService;

    @Value("${redis.topic.update-report-all}")
    private String updateReportAll;

    @Override
    @Transactional
    public Integer create(AnswerBankBaseDTO record) {
        AnswerBankBase answerBankBase = convert(record, AnswerBankBase.class);


//        关联邀请人
        Integer inviterId = record.getInviterId();
        if (inviterId != null) {
            AnswerBankBase inviter = answerBankBaseMapper.selectById(inviterId);
            if (inviter != null) {
                if (answerBankBaseMapper.insert(answerBankBase) != 1) {
                    throw new CommonException("创建基础信息失败: " + record);
                }

                Invitation invitation = new Invitation();
                invitation.setInvitedId(answerBankBase.getId());
                invitation.setInviterId(inviter.getId());
                invitation.setInvitedPicUrl(record.getInvitedPicUrl());
                invitation.setInvitedNickname(answerBankBase.getNickname());
                invitation.setInviterNickname(inviter.getNickname());
                if (invitationMapper.insert(invitation) != 1) {
                    throw new CommonException("创建邀请关联表失败: " + invitation.toString());
                }
            } else {
                answerBankBase.setInviterId(null);
                if (answerBankBaseMapper.insert(answerBankBase) != 1) {
                    throw new CommonException("创建基础信息失败: " + record);
                }
            }
        } else {
            if (answerBankBaseMapper.insert(answerBankBase) != 1) {
                throw new CommonException("创建基础信息失败: " + record);
            }
        }


//        返回创建完成后记录的 id 以便后续问卷关联数据的插入
        return answerBankBase.getId();
    }

    @Override
    public Boolean delete(Integer id) {
        if (answerBankBaseMapper.deleteById(id) != 1) {
            throw new CommonException("删除失败 id: " + id);
        }
        return true;
    }

    @Override
    public Boolean update(AnswerBankBaseDTO record) {
        AnswerBankBase answerBankBase = convert(record, AnswerBankBase.class);
        if (answerBankBaseMapper.updateById(answerBankBase) != 1) {
            throw new CommonException("更新失败 answerBankBase : " + record);
        }
        //      尝试更新报告
        redissonClient.getTopic(updateReportAll)
                .publish(record.getId());
        return true;
    }


    @Override
    public AnswerBankBaseDTO getByWechatId(String wechatId) {

        List<AnswerBankBase> list = answerBankBaseMapper.selectByWechatId(wechatId);
        if (isNotEmpty(list)) {
            return convert(list.get(0), AnswerBankBaseDTO.class);
        }
        return null;
    }

    @Override
    public AnswerBankBaseDTO getById(Integer id) {
        AnswerBankBase answerBankBase = answerBankBaseMapper.selectById(id);
        if (answerBankBase != null) {
            return convert(answerBankBase, AnswerBankBaseDTO.class);
        } else {
            return null;
        }
    }


    /**
     * 获取用户邀请的所有被邀请用户
     *
     * @param inviterId
     * @return
     */
    @Override
    public List<AnswerBankBase> listAnswerBankBaseByInviter(Integer inviterId) {
        return answerBankBaseMapper.selectByInviterId(inviterId);
    }

    /**
     * 添加手机号
     *
     * @param inviterId
     * @return
     */
    @Override
    public List<AnswerBankBaseDTO> listAnswerBaseByInviter(Integer inviterId) {
        return answerBankBaseMapper.selectByInviterIdAddPhone(inviterId);
    }


    @Override
    public long countComplete(List<AnswerBankBase> answerBankBases) {
        return isNotEmpty(answerBankBases) ? answerBankBases.stream()
                .filter(p -> baseInfoService.isBaseInfoCompete(p.getId())).count()
                : 0;
    }

    @Override
    public List<Integer> listBaseIds() {
        List<Integer> ids = answerBankBaseMapper.selectIds();
        return isNotEmpty(ids) ? ids : Collections.emptyList();
    }

    /**
     * 获取所有的基础信息表
     *
     * @return
     */
    @Override
    public List<AnswerBankBase> listAll() {
        List<AnswerBankBase> answerBankBases = answerBankBaseMapper.selectByMap(new HashMap<>());
        return isNotEmpty(answerBankBases) ? answerBankBases : Collections.emptyList();
    }


    /**
     * 检测必填字段填写状态
     *
     * @param answerBankBase
     * @return
     */
    public boolean isComplete(AnswerBankBase answerBankBase) {
        return ObjectUtils.allNotNull(answerBankBase.getEducation(),
                answerBankBase.getAcademy(),
                answerBankBase.getName(),
                answerBankBase.getEnglishName(),
                answerBankBase.getGender(),
                answerBankBase.getBirthday(),
                answerBankBase.getNickname());
    }

}
