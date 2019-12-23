package io.choerodon.app.service.impl;

import com.google.common.collect.ImmutableMap;
import io.choerodon.api.dto.MemberDTO;
import io.choerodon.app.service.AnswerBankBaseService;
import io.choerodon.app.service.MemberService;
import io.choerodon.domain.AnswerBankBase;
import io.choerodon.domain.MemberInfo;
import io.choerodon.infra.exception.CommonException;
import io.choerodon.infra.mapper.InvitationMapper;
import io.choerodon.infra.mapper.MemberInfoMapper;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static io.choerodon.infra.utils.BeanUtil.convert;
import static io.choerodon.infra.utils.BeanUtil.isNotEmpty;

@Service("memberService")
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberInfoMapper memberInfoMapper;

    @Autowired
    private InvitationMapper invitationMapper;

    @Autowired
    private AnswerBankBaseService answerBankBaseService;

    @Override
    public Boolean save(MemberDTO memberDTO) {
        MemberInfo memberInfo = convert(memberDTO, MemberInfo.class);
        return memberInfoMapper.insert(memberInfo) > 0;
    }

    /**
     * 通过 baseId 获取会员信息
     *
     * @param baseId
     * @return
     */
    @Override
    public MemberDTO getMember(Integer baseId) {
        List<MemberInfo> memberInfos = memberInfoMapper.selectByMap(ImmutableMap.of("base_id", baseId));
        if (isNotEmpty(memberInfos)) {
            return convert(memberInfos.get(0), MemberDTO.class);
        } else {
            return null;
        }
    }

    private static final String LEVEL1 = "VIP 1";
    private static final String LEVEL2 = "VIP 2";

    @Override
    public Boolean saveIfInvitedReach(Integer baseId) {
        Integer count = invitationMapper.selectCountByBaseId(baseId);
        MemberDTO member = getMember(baseId);
        MemberDTO memberDTO = new MemberDTO();
        if (count >= 3 && count < 10 && member == null) {  // 若是达到邀请人数3则创建会员身份
            List<AnswerBankBase> bankBases = answerBankBaseService.listAnswerBankBaseByInviter(baseId);
            if (answerBankBaseService.countComplete(bankBases) >= 3) {
                memberDTO.setBaseId(baseId);
                memberDTO.setLevel(LEVEL1);
                Date date = new Date();
                memberDTO.setStartTime(date);
                memberDTO.setEndTime(DateUtils.addYears(date, 1));
                return save(memberDTO);
            } else {
                return false;
            }

        } else if (count >= 10 && member != null) {  // 若是已经是会员且达到邀请人数10则增加3年会员时长
            List<AnswerBankBase> bankBases = answerBankBaseService.listAnswerBankBaseByInviter(baseId);
            if (answerBankBaseService.countComplete(bankBases) >= 10) {
                member.setEndTime(DateUtils.addYears(member.getEndTime(), 3));
                member.setLevel(LEVEL2);
                return updateById(member);
            } else {
                return false;
            }
        } else if (count >= 10 && member == null) { //  可能性非常小的即达成了 10 人邀请数，还未有会员身份
            List<AnswerBankBase> bankBases = answerBankBaseService.listAnswerBankBaseByInviter(baseId);
            if (answerBankBaseService.countComplete(bankBases) >= 10) {
                memberDTO.setBaseId(baseId);
                memberDTO.setLevel(LEVEL2);
                Date date = new Date();
                memberDTO.setStartTime(date);
                memberDTO.setEndTime(DateUtils.addYears(date, 4));
                return save(memberDTO);
            } else {
                return false;
            }

        } else {
            return false;
        }

    }

    public Boolean deleteById(Long id) {
        if (memberInfoMapper.deleteById(id) != 1) {
            throw new CommonException("删除失败: id = " + id);
        }
        return true;
    }


    public Boolean updateById(MemberDTO memberDTO) {
        MemberInfo memberInfoDO = convert(memberDTO, MemberInfo.class);
        return memberInfoMapper.updateById(memberInfoDO) > 0;
    }


}



