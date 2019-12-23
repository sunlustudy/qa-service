package io.choerodon.app.service;

import io.choerodon.domain.DataDictionary;

import java.util.List;

/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-08-28 15:41
 */
public interface DictionaryService {


    List<String> getFirstLevelAll();

    List<String> getByFirstLevel(String firstLevel);

    List<String> getBySectionLevel(String sectionLevel);

    boolean create(DataDictionary record);

    boolean delete(Long id);

    boolean update(DataDictionary record);
}
