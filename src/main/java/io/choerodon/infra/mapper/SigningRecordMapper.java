package io.choerodon.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.choerodon.domain.SigningRecord;

import java.util.List;

public interface SigningRecordMapper  extends BaseMapper<SigningRecord> {
    int deleteByCustomerId(Integer customerId);

    List<SigningRecord> selectByCustomerId(Integer customId);

}