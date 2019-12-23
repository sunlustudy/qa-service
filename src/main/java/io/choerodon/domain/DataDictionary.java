package io.choerodon.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DataDictionary {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String firstLevel;

    private String sectionLevel;

    private String threeLevel;

}