package io.choerodon.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ComprehensiveReport {
    
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer baseId;

    private String name;

    private String gender;

    private String birthday;

    private String educational;

    private String companys;

    private String skills;

    private String personality;

    private String advantageAbilitys;

    private String inferiorityAbilitys;

    private String drivingFactors;

    private BigDecimal recommendedCompositeIndex;

    private String indexLevel;

    private String indexExplain;

    private BigDecimal salesCapabilityIndex;

    private BigDecimal salesCapabilityIndex1;

    private BigDecimal salesCapabilityIndex2;

    private BigDecimal salesCapabilityIndex3;

    private BigDecimal salesCapabilityIndex4;

    private BigDecimal salesCapabilityIndex5;

    private BigDecimal salesCapabilityIndex6;

    private BigDecimal salesCapabilityIndex7;

    private String performanceCapabilityIndex;

    private String historicalPerformance;

    private BigDecimal customerIndex;

    private String customerResourceIndex;

    private BigDecimal customerRelationshipConfidence;

    private String resourceEnergyLevelIndex;

    private BigDecimal relationalEnergyIndex;

    private String workingStability;


}