package io.choerodon.app.service;

import io.choerodon.api.dto.ProfessionalSkillsDTO;

/*
 * @program: springboot
 * @author: syun
 * @create: 2019-08-20 10:59
 */
public interface ProfessionalSkillsService {
    boolean create(ProfessionalSkillsDTO record);

    boolean delete(Integer id);

    boolean update(ProfessionalSkillsDTO record);

    ProfessionalSkillsDTO get(Integer baseId);
}
