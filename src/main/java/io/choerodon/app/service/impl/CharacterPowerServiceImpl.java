package io.choerodon.app.service.impl;

import com.google.common.collect.ImmutableMap;
import io.choerodon.api.dto.CharacterPowerDTO;
import io.choerodon.app.service.CharacterPowerService;
import io.choerodon.domain.CharacterPower;
import io.choerodon.infra.exception.CommonException;
import io.choerodon.infra.mapper.CharacterPowerMapper;
import io.vavr.control.Option;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static io.choerodon.infra.utils.BeanUtil.convert;

/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-08-20 10:59
 */
@Service
public class CharacterPowerServiceImpl implements CharacterPowerService {


    @Value("${redis.topic.update-report-all}")
    private String updateReportAll;

    @Autowired
    private CharacterPowerMapper characterPowerMapper;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public boolean create(CharacterPowerDTO record) {
        CharacterPower characterPower = convert(record, CharacterPower.class);
        if (characterPowerMapper.insert(characterPower) != 1) {
            throw new CommonException("插入失败 characterPower: " + record);
        }
        //      尝试更新报告
        redissonClient.getTopic(updateReportAll)
                .publish(record.getBaseId());
        return true;
    }

    @Override
    public boolean delete(Integer id) {
        if (characterPowerMapper.deleteById(id) != 1) {
            throw new CommonException("删除失败 id: " + id);
        }
        return true;
    }

    @Override
    public boolean update(CharacterPowerDTO record) {
        CharacterPower characterPower = convert(record, CharacterPower.class);
        if (characterPowerMapper.updateById(characterPower) != 1) {
            throw new CommonException("更新失败 characterPower: " + record);
        }

//        更新报告信息
        redissonClient.getTopic(updateReportAll)
                .publish(record.getBaseId());

        return true;
    }


    @Override
    public CharacterPowerDTO get(Integer baseId) {

        return Option.of(characterPowerMapper.selectByMap(ImmutableMap.of("base_id", baseId)))
                .filter(p -> p != null && p.size() > 0)
                .map(p -> p.get(0))
                .map(p -> convert(p, CharacterPowerDTO.class))
                .getOrElse(() -> null);
    }


}
