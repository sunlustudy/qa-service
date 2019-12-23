package io.choerodon.app.service;

import io.choerodon.api.dto.WorkHistoryDTO;
import io.choerodon.domain.WorkHistory;
import io.choerodon.domain.WorkHistoryExtend;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-08-20 10:57
 */
public interface WorkHistoryService {


    boolean create(WorkHistoryDTO record);

    boolean delete(Integer id);

    boolean delete(List<Integer> ids);

    boolean deleteExtend(List<Integer> ExtendIds);

    boolean update(WorkHistoryDTO record);

    @Transactional
    boolean update(List<WorkHistory> workHistories, List<WorkHistoryExtend> workHistoryExtends);

    WorkHistoryDTO get(Integer baseId);

    Boolean deleteByBaseId(Integer baseId);
}
