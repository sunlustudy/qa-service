package io.choerodon.app.service;

/*
 * @program: springboot
 * @author: syun
 * @create: 2019-09-04 14:00
 */
public interface CertificateService {
    boolean deleteById(Integer id);

    Boolean deleteByChildId(Integer childId);
}
