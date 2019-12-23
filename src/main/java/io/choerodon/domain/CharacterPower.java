package io.choerodon.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class CharacterPower {


    @TableId(type = IdType.INPUT)
    private Integer id;

    private Integer baseId;

    private String character1;

    private String character2;

    private String character3;

    private String character4;

    private String character5;

    private String character6;

    private String character7;

    private String character8;

    private String character9;

    private String character10;

    private String character11;

    private String character12;

    private String character13;

    private String character14;

    private String character15;

    private String character16;

    private String character17;

    private String character18;

    private String character19;

    private String character20;

    private String character21;

    private String character22;

    private String character23;

    private String drivingFactors;


}