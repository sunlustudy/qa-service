package io.choerodon.api.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

@Data
public class ProfessionalSkillsDTO {
    private Integer id;

    private Integer baseId;

    @Excel(name = "知识类", width = 15, needMerge = true)
    private String knowledge;

    @Excel(name = "技能类", width = 15, needMerge = true)
    private String skill;

    @Excel(name = "产品/解决方案知识", width = 15, needMerge = true)
    private String basicCompetence;

    @Excel(name = "职业素养", width = 15, needMerge = true)
    private String accomplishment;

    @Excel(name = "销售回款管理", width = 15, needMerge = true)
    private String salesReturn;

    @Excel(name = "客户关系管理", width = 15, needMerge = true)
    private String crm;

    @Excel(name = "销售业绩管理", width = 15, needMerge = true)
    private String salesPerformance;

    @Excel(name = "语言一", width = 15, needMerge = true)
    private String languageOne;

    @Excel(name = "语言二", width = 15, needMerge = true)
    private String languageTwo;

    @Excel(name = "语言三", width = 15, needMerge = true)
    private String languageThree;

    @Excel(name = "语言四", width = 15, needMerge = true)
    private String languageFour;

    @Excel(name = "语言五", width = 15, needMerge = true)
    private String languageFive;

    @Excel(name = "行业知识", width = 15, needMerge = true)
    private String profession;

    @Excel(name = "销售渠道", width = 15, needMerge = true)
    private String sell;

    @Excel(name = "销售业务流程", width = 15, needMerge = true)
    private String flow;

    @Excel(name = "商务礼仪", width = 15, needMerge = true)
    private String business;

    @Excel(name = "销售理论", width = 15, needMerge = true)
    private String theory;

    @Excel(name = "其他知识类型", width = 15, needMerge = true)
    private String knowOthers;

    @Excel(name = "沟通技能", width = 15, needMerge = true)
    private String communication;

    @Excel(name = "影响技能", width = 15, needMerge = true)
    private String influence;

    @Excel(name = "管理技能", width = 15, needMerge = true)
    private String manage;

    @Excel(name = "谈判技能", width = 15, needMerge = true)
    private String negotiate;

    @Excel(name = "时间管理", width = 15, needMerge = true)
    private String timeManage;

    @Excel(name = "电话销售技能", width = 15, needMerge = true)
    private String teleSale;

    @Excel(name = "网络销售技能", width = 15, needMerge = true)
    private String netSale;

    @Excel(name = "会议销售技能", width = 15, needMerge = true)
    private String meetingSale;

    @Excel(name = "展会销售技能", width = 15, needMerge = true)
    private String exhibitionSale;

    @Excel(name = "开发客户技能", width = 15, needMerge = true)
    private String developCustom;

    @Excel(name = "处理异议技能", width = 15, needMerge = true)
    private String handleDissent;

    @Excel(name = "促成交易技能", width = 15, needMerge = true)
    private String dealMaking;

    @Excel(name = "客户维护技能", width = 15, needMerge = true)
    private String customerMaintenance;

    @Excel(name = "销售计划管理", width = 15, needMerge = true)
    private String salePlan;

    @Excel(name = "销售合同管理", width = 15, needMerge = true)
    private String saleContract;

    @Excel(name = "其他专业技能", width = 15, needMerge = true)
    private String skillOthers;
}