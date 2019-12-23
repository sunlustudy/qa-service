package io.choerodon.app.service.impl;

import com.google.common.collect.ImmutableMap;
import io.choerodon.api.dto.ProfessionalSkillsDTO;
import io.choerodon.app.service.ProfessionalSkillsService;
import io.choerodon.domain.ProfessionalSkills;
import io.choerodon.infra.exception.CommonException;
import io.choerodon.infra.mapper.ProfessionalSkillsMapper;
import io.vavr.control.Option;
import org.springframework.stereotype.Service;

import static io.choerodon.infra.utils.BeanUtil.convert;


/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-08-20 11:01
 */
@Service
public class ProfessionalSkillsServiceImpl implements ProfessionalSkillsService {


    private final ProfessionalSkillsMapper professionalSkillsMapper;

    public ProfessionalSkillsServiceImpl(ProfessionalSkillsMapper professionalSkillsMapper) {
        this.professionalSkillsMapper = professionalSkillsMapper;
    }

    @Override
    public boolean create(ProfessionalSkillsDTO record) {


        ProfessionalSkills professionalSkills = convert(record, ProfessionalSkills.class);

        if (professionalSkillsMapper.insert(professionalSkills) != 1) {
            throw new CommonException("插入失败 professionalSkills: " + record);
        }
        return true;
    }

    @Override
    public boolean delete(Integer id) {
        if (professionalSkillsMapper.deleteById(id) != 1) {
            throw new CommonException("删除失败 id: " + id);
        }
        return true;
    }

    @Override
    public boolean update(ProfessionalSkillsDTO record) {
        ProfessionalSkills professionalSkills = convert(record, ProfessionalSkills.class);
        if (professionalSkillsMapper.updateById(professionalSkills) != 1) {
            throw new CommonException("更新失败 professionalSkills: " + record);
        }
        return true;
    }


    @Override
    public ProfessionalSkillsDTO get(Integer baseId) {
        return Option.of(professionalSkillsMapper.selectByMap(ImmutableMap.of("base_id", baseId)))
                .filter(p -> p != null && p.size() > 0)
                .map(p -> p.get(0))
                .map(p -> convert(p, ProfessionalSkillsDTO.class))
                .getOrElse(() -> null);
    }


}
