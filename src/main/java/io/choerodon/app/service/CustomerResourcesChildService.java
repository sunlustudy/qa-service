package io.choerodon.app.service;

import io.choerodon.api.dto.CustomerResourcesChildDTO;
import io.choerodon.api.dto.CustomerResourcesDTO;
import io.choerodon.domain.CustomerResourcesChild;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-08-20 10:57
 */
public interface CustomerResourcesChildService {


    boolean create(CustomerResourcesChildDTO record);

    boolean create(List<CustomerResourcesChildDTO> records);

    boolean delete(Integer id);

    boolean update(CustomerResourcesChildDTO record);

    @Transactional
    boolean update(List<CustomerResourcesChildDTO> childDTOS);

    List<CustomerResourcesChildDTO> obtainByCustomer(Integer customerId);

    List<CustomerResourcesChild> listResourcesByBaseId(Integer baseId);
}
