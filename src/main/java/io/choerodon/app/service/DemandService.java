package io.choerodon.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.api.dto.DemandDTO;

import java.util.Map;

/*
 * @program: springboot
 * @author: syun
 * @create: 2019-09-17 17:15
 */
public interface DemandService {

    boolean create(DemandDTO record);

    boolean delete(Integer id);

    boolean update(DemandDTO record);

    boolean updateStatus(Integer id, Boolean isDel);

    PageInfo<DemandDTO> search(Integer page, Integer size, Map<String, Object> params);

    PageInfo<DemandDTO> obtainRecommended(Integer page, Integer size, Integer baseId);
}
