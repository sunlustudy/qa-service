package io.choerodon.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResourcesCertificateRelation {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer resourcesChildId;

    private Integer certificateId;


}