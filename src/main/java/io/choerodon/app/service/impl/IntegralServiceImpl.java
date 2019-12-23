package io.choerodon.app.service.impl;

import io.choerodon.app.service.IntegralService;
import io.choerodon.domain.Integral;
import io.choerodon.infra.mapper.IntegralMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("integralService")
public class IntegralServiceImpl implements IntegralService {

    @Autowired
    private IntegralMapper integralMapper;

    public Boolean savePoint(Integer baseId, Integer point) {
        return integralMapper.insert(new Integral(baseId, point)) > 0;
    }

}



