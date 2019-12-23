package io.choerodon.api.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@ApiModel(value = "综合报告")
public class ComprehensiveReportDTO {
    private Integer id;

    private Integer baseId;

    @Excel(name = "姓名", width = 15)
    @ApiModelProperty(value = "姓名")
    private String name;

    @Excel(name = "性别", width = 15)
    @ApiModelProperty(value = "性别")
    private String gender;

    @Excel(name = "年龄", width = 15)
    @ApiModelProperty(value = "出生年月 存年龄，动态计算年龄")
    private String birthday;

    @Excel(name = "教育背景", width = 15)
    @ApiModelProperty(value = "教育背景 最高学历|毕业院校|专业名称")
    private String educational;

    @Excel(name = "任职公司", width = 15)
    @ApiModelProperty(value = "任职公司 A公司|B公司|。。。")
    private String companys;

    @Excel(name = "知识技能", width = 15)
    @ApiModelProperty(value = "知识技能")
    private String skills;

//    @ApiModelProperty(value = "性格")
//    private String personality;

    @ApiModelProperty(value = "性格特质")
    private Map<String, Integer> personalities;

    @Excel(name = "能力优势", width = 15)
    @ApiModelProperty(value = "能力优势")
    private String advantageAbilitys;

    @Excel(name = "能力劣势", width = 15)
    @ApiModelProperty(value = "能力劣势")
    private String inferiorityAbilitys;

    @Excel(name = "工作动力", width = 15)
    @ApiModelProperty(value = "工作动力")
    private String drivingFactors;

    @Excel(name = "推荐综合指数", width = 15)
    @ApiModelProperty(value = "推荐综合指数")
    private BigDecimal recommendedCompositeIndex;

    @Excel(name = "指数说明", width = 15)
    @ApiModelProperty(value = "指数说明")
    private String indexLevel;

    @Excel(name = "推荐说明文字", width = 15)
    @ApiModelProperty(value = "推荐说明文字")
    private String indexExplain;

    @Excel(name = "销售能力指数", width = 15)
    @ApiModelProperty(value = "销售能力指数")
    private BigDecimal salesCapabilityIndex;

    @Excel(name = "计划和准备", width = 15)
    @ApiModelProperty(value = "计划和准备")
    private BigDecimal salesCapabilityIndex1;

    @Excel(name = "搜集信息", width = 15)
    @ApiModelProperty(value = "搜集信息")
    private BigDecimal salesCapabilityIndex2;

    @Excel(name = "建立联系", width = 15)
    @ApiModelProperty(value = "建立联系")
    private BigDecimal salesCapabilityIndex3;

    @Excel(name = "需求洞察", width = 15)
    @ApiModelProperty(value = "需求洞察")
    private BigDecimal salesCapabilityIndex4;

    @Excel(name = "提出解决方案", width = 15)
    @ApiModelProperty(value = "提出解决方案")
    private BigDecimal salesCapabilityIndex5;

    @Excel(name = "应对客户质疑", width = 15)
    @ApiModelProperty(value = "应对客户质疑")
    private BigDecimal salesCapabilityIndex6;

    @Excel(name = "获得销售", width = 15)
    @ApiModelProperty(value = "获得销售")
    private BigDecimal salesCapabilityIndex7;

    @Excel(name = "业绩能力指数", width = 15)
    @ApiModelProperty(value = "业绩能力指数")
    private String performanceCapabilityIndex;

    @ApiModelProperty(value = "历史业绩情况 年份：金额|年份：金额")
    private String historicalPerformance;

    @ApiModelProperty(value = "历史业绩情况 excel 中形式 ")
    private List<HistoricalPerformance> historicalPerformances;

    @Excel(name = "重点客户成单指数", width = 15)
    @ApiModelProperty(value = "重点客户成单指数")
    private BigDecimal customerIndex;

    @Excel(name = "客户资源指数", width = 15)
    @ApiModelProperty(value = "客户资源指数")
    private String customerResourceIndex;

    @Excel(name = "客户关系置信度", width = 15)
    @ApiModelProperty(value = "客户关系置信度")
    private BigDecimal customerRelationshipConfidence;

    @Excel(name = "资源能级指数", width = 15)
    @ApiModelProperty(value = "资源能级指数")
    private String resourceEnergyLevelIndex;

    @Excel(name = "关系能量指数", width = 15)
    @ApiModelProperty(value = "关系能量指数")
    private BigDecimal relationalEnergyIndex;

    @Excel(name = "工作稳定性", width = 15)
    @ApiModelProperty(value = "工作稳定性")
    private String workingStability;

    @ApiModelProperty(value = "岗位契合度")
    private String fitDegree;

    @ApiModelProperty(value = "投递时间")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date deliveryTime;

    @ApiModelProperty(value = "推荐表 id，用以更新推荐表简历的状态")
    private Integer postId;

    @ApiModelProperty(value = "手机号")
    private String phone;

}