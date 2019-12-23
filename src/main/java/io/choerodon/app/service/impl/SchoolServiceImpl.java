package io.choerodon.app.service.impl;

import io.choerodon.app.service.SchoolService;
import io.choerodon.infra.mapper.SchoolMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-08-22 10:02
 */
@Service
public class SchoolServiceImpl implements SchoolService {

    private final SchoolMapper schoolMapper;


    public SchoolServiceImpl(SchoolMapper schoolMapper) {
        this.schoolMapper = schoolMapper;
    }


    @Override
    public List<Map<String, Object>> getProvince() {
        return schoolMapper.selectProvince();
    }


    @Override
    public List<Map<String, Object>> getCityByProvince(String pid) {
        return schoolMapper.selectCityByPid(pid);
    }


    @Override
    public List<Map<String, Object>> getSchoolByCity(String pid) {
        return schoolMapper.selectSchoolByPid(pid);
    }

    @Override
    public List<String> searchSchool(String keyword) {
        return schoolMapper.selectSchoolByKeyword(keyword);
    }


}
