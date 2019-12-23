package io.choerodon.infra.mapper;

import io.choerodon.domain.DomainName;

public interface DomainNameMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DomainName record);

    int insertSelective(DomainName record);

    DomainName selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DomainName record);

    int updateByPrimaryKey(DomainName record);
}