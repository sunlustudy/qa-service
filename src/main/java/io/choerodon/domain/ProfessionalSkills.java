package io.choerodon.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class ProfessionalSkills {


    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer baseId;

    private String knowledge;

    private String skill;

    private String basicCompetence;

    private String accomplishment;

    private String salesReturn;

    private String crm;

    private String salesPerformance;

    private String languageOne;

    private String languageTwo;

    private String languageThree;

    private String languageFour;

    private String languageFive;

    private String profession;

    private String sell;

    private String flow;

    private String business;

    private String theory;

    private String knowOthers;

    private String communication;

    private String influence;

    private String manage;

    private String negotiate;

    private String timeManage;

    private String teleSale;

    private String netSale;

    private String meetingSale;

    private String exhibitionSale;

    private String developCustom;

    private String handleDissent;

    private String dealMaking;

    private String customerMaintenance;

    private String salePlan;

    private String saleContract;

    private String skillOthers;

}