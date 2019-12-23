package io.choerodon.api.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

@Data
public class CharacterPowerDTO {
    private Integer id;

    private Integer baseId;

    @Excel(name = "广泛社交", width = 10, needMerge = true)
    private String character1;

    @Excel(name = "团队协作", width = 10, needMerge = true)
    private String character2;

    @Excel(name = "强势支配", width = 10, needMerge = true)
    private String character3;

    @Excel(name = "销售谈判", width = 10, needMerge = true)
    private String character4;

    @Excel(name = "主动结识", width = 10, needMerge = true)
    private String character5;

    @Excel(name = "公开活动", width = 10, needMerge = true)
    private String character6;

    @Excel(name = "服务意识", width = 10, needMerge = true)
    private String character7;

    @Excel(name = "与人为善", width = 10, needMerge = true)
    private String character8;

    @Excel(name = "分析思维", width = 10, needMerge = true)
    private String character9;

    @Excel(name = "批判思维", width = 10, needMerge = true)
    private String character10;

    @Excel(name = "概念思维", width = 10, needMerge = true)
    private String character11;

    @Excel(name = "辩证思维", width = 10, needMerge = true)
    private String character12;

    @Excel(name = "关注细节", width = 10, needMerge = true)
    private String character13;

    @Excel(name = "组织计划", width = 10, needMerge = true)
    private String character14;

    @Excel(name = "风险偏好", width = 10, needMerge = true)
    private String character15;

    @Excel(name = "遵守承诺", width = 10, needMerge = true)
    private String character16;

    @Excel(name = "遵守规则", width = 10, needMerge = true)
    private String character17;

    @Excel(name = "抗压性", width = 10, needMerge = true)
    private String character18;

    @Excel(name = "恢复力", width = 10, needMerge = true)
    private String character19;

    @Excel(name = "精神能量", width = 10, needMerge = true)
    private String character20;

    @Excel(name = "果断决策", width = 10, needMerge = true)
    private String character21;

    @Excel(name = "目标导向", width = 10, needMerge = true)
    private String character22;

    @Excel(name = "竞争意识", width = 10, needMerge = true)
    private String character23;

    @Excel(name = "驱动因素", width = 10, needMerge = true)
    private String drivingFactors;


}