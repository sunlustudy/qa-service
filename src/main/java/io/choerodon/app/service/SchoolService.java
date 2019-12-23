package io.choerodon.app.service;

import java.util.List;
import java.util.Map;

/*
 * @program: springboot
 * @author: syun
 * @create: 2019-08-22 10:02
 */
public interface SchoolService {

    /**
     * 直接获取省份数据
     * @return
     */
    List<Map<String,Object>> getProvince();

    /**
     * 通过省级 id 获取市级数据
     * @param pid
     * @return
     */
    List<Map<String, Object>> getCityByProvince(String pid);

    /**
     * 通过市级 id 获取学校数据
     * @param pid
     * @return
     */
    List<Map<String, Object>> getSchoolByCity(String pid);

    List<String> searchSchool(String keyword);
}
