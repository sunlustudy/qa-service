package io.choerodon.app.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.ImmutableMap;
import io.choerodon.api.dto.DemandDTO;
import io.choerodon.app.service.DemandService;
import io.choerodon.domain.Demand;
import io.choerodon.infra.exception.CommonException;
import io.choerodon.infra.mapper.DemandMapper;
import io.choerodon.infra.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static io.choerodon.infra.utils.BeanUtil.convert;
import static io.choerodon.infra.utils.BeanUtil.convertPage;


/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-09-17 17:15
 */
@Service
public class DemandServiceImpl implements DemandService {

    @Autowired
    private DemandMapper demandMapper;


    @Override
    public boolean create(DemandDTO record) {
        Demand demand = convert(record, Demand.class);
        if (demandMapper.insert(demand) != 1) {
            throw new CommonException("插入失败 demand: " + record);
        }
        return true;
    }


    @Override
    public boolean delete(Integer id) {
        if (demandMapper.deleteByMap(ImmutableMap.of("id", id,
                "created_by", SecurityUtils.getCurrentUser().getId())) != 1) {
            throw new CommonException("删除失败,可能非用户所创数据 id: " + id);
        }
        return true;
    }


    /**
     * 只能由创建者更新
     *
     * @param record
     * @return
     */
    @Override
    public boolean update(DemandDTO record) {
        Demand demand = convert(record, Demand.class);
        Demand oldDemand = demandMapper.selectById(record.getId());
        demand.setVersion(oldDemand.getVersion());
        demand.setCreatedBy(SecurityUtils.getCurrentUser().getId());
        if (demandMapper.updateByPrimaryKeySelective(demand) != 1) {
            throw new CommonException("更新失败 可能非用户创建数据，demand: " + record);
        }
        return true;
    }


    @Override
    public boolean updateStatus(Integer id, Boolean isDel) {
        return demandMapper.updateStatus(id, isDel, SecurityUtils.getCurrentUser().getId()) > 0;
    }


    @Override
    public PageInfo<DemandDTO> search(Integer page, Integer size, Map<String, Object> params) {
//        管理员
        if (SecurityUtils.isAdmin()) {
            Page<Demand> temps = PageHelper.startPage(page, size);
            demandMapper.selectByParams(params);
            Page<DemandDTO> result = convertPage(temps, DemandDTO.class);
            return new PageInfo<>(result);
        }
//          hr
        if (SecurityUtils.isUser()) {
            params.put("userId", SecurityUtils.getCurrentUser().getId());
            Page<Demand> temps = PageHelper.startPage(page, size);
            demandMapper.selectByParamsAll(params);
            Page<DemandDTO> result = convertPage(temps, DemandDTO.class);
            return new PageInfo<>(result);
        }
//          理论上不会运行这里
        return new PageInfo<>();
    }


    @Override
    public PageInfo<DemandDTO> obtainRecommended(Integer page, Integer size, Integer baseId) {
        Page<Demand> temps = PageHelper.startPage(page, size);
        demandMapper.selectRecommended(baseId);
        Page<DemandDTO> result = convertPage(temps, DemandDTO.class);
        return new PageInfo<>(result);
    }


}
