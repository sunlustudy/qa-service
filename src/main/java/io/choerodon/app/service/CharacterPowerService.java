package io.choerodon.app.service;

import io.choerodon.api.dto.CharacterPowerDTO;

/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-08-20 10:57
 */
public interface CharacterPowerService {


    boolean create(CharacterPowerDTO record);

    boolean delete(Integer id);

    boolean update(CharacterPowerDTO record);

    CharacterPowerDTO get(Integer baseId);
}
