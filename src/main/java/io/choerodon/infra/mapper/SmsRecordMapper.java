package io.choerodon.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.choerodon.domain.SmsRecord;

import java.util.Date;

public interface SmsRecordMapper extends BaseMapper<SmsRecord> {


    SmsRecord selectByPhoneAndWechatId(String phone, String wechatId);

    /**
     * 删除所有的超过时间的 id
     */
    int deleteByOvertime(Date now);

    int deleteByPhoneAndWechatId(String phone, String wechatId);

}