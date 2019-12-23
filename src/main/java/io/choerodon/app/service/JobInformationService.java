package io.choerodon.app.service;

import io.choerodon.api.dto.JobInformationDTO;

/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-08-20 10:57
 */
public interface JobInformationService {


    boolean create(JobInformationDTO record);

    boolean delete(Integer id);

    boolean update(JobInformationDTO record);


    JobInformationDTO get(Integer baseId);
}
