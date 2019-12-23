package io.choerodon.infra.mapper;


import java.util.List;
import java.util.Map;

/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-08-22 09:55
 */
public interface SchoolMapper {

    List<Map<String, Object>> selectProvince();

    List<Map<String, Object>> selectCityByPid(String pid);

    List<Map<String, Object>> selectSchoolByPid(String pid);

    List<String> selectSchoolByKeyword(String keyword);


}
