package io.choerodon.app.service.impl;

import com.google.common.collect.ImmutableMap;
import io.choerodon.api.dto.WorkHistoryDTO;
import io.choerodon.app.service.WorkHistoryService;
import io.choerodon.domain.WorkHistory;
import io.choerodon.domain.WorkHistoryExtend;
import io.choerodon.infra.exception.CommonException;
import io.choerodon.infra.mapper.WorkHistoryExtendMapper;
import io.choerodon.infra.mapper.WorkHistoryMapper;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static io.choerodon.infra.utils.BeanUtil.isNotEmpty;


/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-08-20 11:01
 */
@Service
public class WorkHistoryServiceImpl implements WorkHistoryService {


    @Autowired
    private WorkHistoryMapper workHistoryMapper;

    @Autowired
    private WorkHistoryExtendMapper workHistoryExtendMapper;

    @Autowired
    private RedissonClient redissonClient;


    @Value("${redis.topic.update-report-all}")
    private String updateReportAll;


    @Override
    @Transactional
    public boolean create(WorkHistoryDTO record) {
        return create(record.getWorkHistories(), record.getWorkHistoryExtends());
    }

    private boolean create(List<WorkHistory> workHistories, List<WorkHistoryExtend> workHistoryExtends) {

        if (isNotEmpty(workHistories)) {
            workHistories.forEach(p -> {
                if (workHistoryMapper.insert(p) != 1) {
                    throw new CommonException("插入 work history 失败: " + p);
                }
            });
        }
        //      更新扩展表
        if (isNotEmpty(workHistoryExtends)) {
            workHistoryExtends.forEach(p -> {
                if (workHistoryExtendMapper.insert(p) != 1) {
                    throw new CommonException("插入扩展表失败 : " + p);
                }
            });
        }

        //      尝试更新报告
        if (isNotEmpty(workHistories)) {
            redissonClient.getTopic(updateReportAll)
                    .publish(workHistories.get(0).getBaseId());
        } else if (isNotEmpty(workHistoryExtends)) {
            redissonClient.getTopic(updateReportAll)
                    .publish(workHistoryExtends.get(0).getBaseId());
        }


        return true;
    }

    @Override
    public boolean delete(Integer id) {
        if (workHistoryMapper.deleteById(id) != 1) {
            throw new CommonException("删除失败 id: " + id);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean delete(List<Integer> ids) {
        if (isNotEmpty(ids)) {
            ids.forEach(this::delete);
        }
        return true;
    }


    @Override
    @Transactional
    public boolean deleteExtend(List<Integer> ExtendIds) {
        if (isNotEmpty(ExtendIds)) {
//            循环删除相扩展表信息
            ExtendIds.forEach(workHistoryExtendMapper::deleteById);
        }
        return true;
    }


    @Override
    @Transactional
    public boolean update(WorkHistoryDTO record) {
        return update(record.getWorkHistories(), record.getWorkHistoryExtends());
    }

    @Override
    @Transactional
    public boolean update(List<WorkHistory> workHistories, List<WorkHistoryExtend> workHistoryExtends) {
        if (isNotEmpty(workHistories)) {
            workHistories.forEach(p -> {
                if (workHistoryMapper.updateById(p) != 1) {
                    throw new CommonException("更新失败 work history : " + p);
                }
            });
        }

//    更新扩展表
        if (isNotEmpty(workHistoryExtends)) {
            workHistoryExtends.forEach(p -> {
                if (workHistoryExtendMapper.updateById(p) != 1) {
                    throw new CommonException("更新扩展表失败: " + p);
                }
            });
        }


        //      尝试更新报告
        if (isNotEmpty(workHistories)) {
            redissonClient.getTopic(updateReportAll)
                    .publish(workHistories.get(0).getBaseId());
        } else if (isNotEmpty(workHistoryExtends)) {
            redissonClient.getTopic(updateReportAll)
                    .publish(workHistoryExtends.get(0).getBaseId());
        }

        return true;
    }

    @Override
    public WorkHistoryDTO get(Integer baseId) {


        List<WorkHistory> workHistories = workHistoryMapper.selectByMap(ImmutableMap.of("base_id", baseId));
        List<WorkHistoryExtend> workHistoryExtends = workHistoryExtendMapper.selectByMap(ImmutableMap.of("base_id", baseId));
        return new WorkHistoryDTO(workHistories, workHistoryExtends);
    }


    @Override
    @Transactional
    public Boolean deleteByBaseId(Integer baseId) {
        workHistoryMapper.deleteByMap(ImmutableMap.of("base_id", baseId));
        workHistoryExtendMapper.deleteByMap(ImmutableMap.of("base_id", baseId));
        return true;
    }


}
