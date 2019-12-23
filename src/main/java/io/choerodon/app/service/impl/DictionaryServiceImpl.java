package io.choerodon.app.service.impl;

import com.google.common.collect.ImmutableMap;
import io.choerodon.app.service.DictionaryService;
import io.choerodon.domain.DataDictionary;
import io.choerodon.infra.exception.CommonException;
import io.choerodon.infra.mapper.DataDictionaryMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-08-28 15:42
 */
@Service
public class DictionaryServiceImpl  implements DictionaryService {

    private final DataDictionaryMapper dataDictionaryMapper;

    public DictionaryServiceImpl(DataDictionaryMapper dataDictionaryMapper) {
        this.dataDictionaryMapper = dataDictionaryMapper;
    }



    @Override
    public List<String> getFirstLevelAll(){
        return dataDictionaryMapper.selectFirstLevel();
    }


    @Override
    public List<String> getByFirstLevel(String firstLevel) {
        return dataDictionaryMapper.selectByFirstLevel(firstLevel);
    }


    @Override
    public List<String> getBySectionLevel(String sectionLevel) {
        return dataDictionaryMapper.selectByMap(ImmutableMap.of("section_level", sectionLevel))
                .stream()
                .map(DataDictionary::getThreeLevel)
                .collect(Collectors.toList());
    }



    @Override
    public boolean create(DataDictionary record){
        if(dataDictionaryMapper.insert(record)!=1){
            throw new CommonException("插入失败 dataDictionaryMapper: "+record);
        }
        return true;
    }

    @Override
    public boolean delete(Long id){
        if(dataDictionaryMapper.deleteById(id)!=1){
            throw new CommonException("删除失败 id: "+id);
        }
        return true;
    }


    @Override
    public boolean update(DataDictionary record){

        if(dataDictionaryMapper.updateById(record)!=1){
            throw new CommonException("更新失败 dataDictionaryMapper: "+record);
        }
        return true;
    }




}
