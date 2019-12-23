package io.choerodon.app.service.impl;

import com.google.common.collect.ImmutableMap;
import io.choerodon.api.dto.JobInformationDTO;
import io.choerodon.app.service.BaseInfoService;
import io.choerodon.app.service.JobInformationService;
import io.choerodon.domain.JobInformation;
import io.choerodon.infra.exception.CommonException;
import io.choerodon.infra.mapper.JobInformationMapper;
import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import static io.choerodon.infra.utils.BeanUtil.convert;


/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-08-20 11:00
 */
@Service
@Slf4j
public class JobInformationServiceImpl implements JobInformationService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private JobInformationMapper jobInformationMapper;


    @Value("${redis.topic.personal}")
    String personalTopic;

    @Value("${redis.topic.update-report-all}")
    private String updateReportAll;


    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private TaskExecutor taskExecutor;


    @Autowired
    private BaseInfoService baseInfoService;

    @Override
    public boolean create(JobInformationDTO record) {
        JobInformation jobInformation = convert(record, JobInformation.class);
        if (jobInformationMapper.insert(jobInformation) != 1) {
            throw new CommonException("插入失败 jobInformation: " + record);
        }

//      尝试更新报告
        redissonClient.getTopic(updateReportAll)
                .publish(record.getBaseId());
//      尝试获取商机卡和会员身份
        taskExecutor.execute(() -> baseInfoService.handleCardGet(record.getBaseId()));

        return true;
    }

    @Override
    public boolean delete(Integer id) {
        if (jobInformationMapper.deleteById(id) != 1) {
            throw new CommonException("删除失败 id: " + id);
        }
        return true;
    }

    @Override
    public boolean update(JobInformationDTO record) {
        JobInformation jobInformation = convert(record, JobInformation.class);
        if (jobInformationMapper.updateById(jobInformation) != 1) {
            throw new CommonException("更新失败 jobInformation: " + record);
        }

//      尝试更新报告
        redissonClient.getTopic(updateReportAll)
                .publish(record.getBaseId());
        return true;
    }

    @Override
    public JobInformationDTO get(Integer baseId) {
        return Option.of(jobInformationMapper.selectByMap(ImmutableMap.of("base_id", baseId)))
                .filter(p -> p != null && p.size() > 0)
                .map(p -> p.get(0))
                .map(p -> convert(p, JobInformationDTO.class))
                .getOrElse(() -> null);
    }


}
