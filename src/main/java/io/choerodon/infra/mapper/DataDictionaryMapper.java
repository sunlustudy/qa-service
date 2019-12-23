package io.choerodon.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.choerodon.domain.DataDictionary;

import java.util.List;

public interface DataDictionaryMapper extends BaseMapper<DataDictionary> {

    List<String> selectByFirstLevel(String firstLevel);

    List<String> selectFirstLevel();


}