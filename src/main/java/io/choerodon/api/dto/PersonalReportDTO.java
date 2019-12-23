package io.choerodon.api.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Data
@ApiModel(value = "个人报告")
public class PersonalReportDTO {

    private Integer id;

    private Integer baseId;

    @ApiModelProperty(value = "姓名")
    @Excel(name = "姓名", width = 15)
    private String name;

    @Excel(name = "性别", width = 15)
    @ApiModelProperty(value = "性别")
    private String gender;

    @ApiModelProperty(value = "自我评价")
    @Excel(name = "自我评价", width = 20)
    private String selfEvaluation;

    @Excel(name = "年龄", width = 15)
    @ApiModelProperty(value = "出生年月 存年龄，动态计算年龄")
    private String birthday;

    @Excel(name = "婚姻状况", width = 15)
    @ApiModelProperty(value = "婚姻状况")
    private String marryStatus;

    @ApiModelProperty(value = "工作年限")
    private String workYears;

    @Excel(name = "居住城市", width = 15)
    @ApiModelProperty(value = "居住城市")
    private String habitation;

    @Excel(name = "应聘职位", width = 15)
    @ApiModelProperty(value = "期望岗位，应聘职位")
    private String expectPost;

    @Excel(name = "期望工作地点", width = 15)
    @ApiModelProperty(value = "期望工作地点")
    private String workPlace;

    @Excel(name = "最高学历", width = 15)
    @ApiModelProperty(value = "最高学历")
    private String education;

    @Excel(name = "毕业院校", width = 15)
    @ApiModelProperty(value = "毕业院校")
    private String academy;

    @Excel(name = "专业学科", width = 15)
    @ApiModelProperty(value = "专业学科")
    private String subject;

    @Excel(name = "专业名称", width = 15)
    @ApiModelProperty(value = "专业名称")
    private String professionalName;

    @Excel(name = "专业知识", width = 15)
    @ApiModelProperty(value = "专业知识 数据格式：标签：掌握级别|标签：掌握级别")
    private String knowledge;

    @Excel(name = "专业技能", width = 15)
    @ApiModelProperty(value = "专业技能 数据格式：标签：掌握级别|标签：掌握级别")
    private String skill;

    //    自我管理
    @Excel(name = "自我管理", width = 15)
    @ApiModelProperty(value = "自我管理")
    private BigDecimal quality1;
    //    理解业务  手输
    @Excel(name = "理解业务", width = 15)
    @ApiModelProperty(value = "理解业务")
    private BigDecimal quality2;
    //    不骄不躁 手输
    @Excel(name = "不骄不躁", width = 15)
    @ApiModelProperty(value = "不骄不躁")
    private BigDecimal quality3;
    //    自信 手输
    @Excel(name = "自信", width = 15)
    @ApiModelProperty(value = "自信")
    private BigDecimal quality4;
    //    业务实现
    @Excel(name = "业务实现", width = 15)
    @ApiModelProperty(value = "业务实现")
    private BigDecimal quality5;
    //    使命必达 手输
    @Excel(name = "使命必达", width = 15)
    @ApiModelProperty(value = "使命必达")
    private BigDecimal quality6;
    //  老谋深算 手输
    @Excel(name = "老谋深算", width = 15)
    @ApiModelProperty(value = "老谋深算")
    private BigDecimal quality7;
    //融化客户 手输
    @Excel(name = "融化客户", width = 15)
    @ApiModelProperty(value = "融化客户")
    private BigDecimal quality8;
    //利益结盟 手输
    @Excel(name = "利益结盟", width = 15)
    @ApiModelProperty(value = "利益结盟")
    private BigDecimal quality9;
    //经营关系 手输
    @Excel(name = "经营关系", width = 15)
    @ApiModelProperty(value = "经营关系")
    private BigDecimal quality10;
    //团队管理 手输
    @Excel(name = "团队管理", width = 15)
    @ApiModelProperty(value = "团队管理")
    private BigDecimal quality11;
    //带兵打仗 手输
    @Excel(name = "带兵打仗", width = 15)
    @ApiModelProperty(value = "带兵打仗")
    private BigDecimal quality12;

    @Excel(name = "沟通意愿", width = 15)
    @ApiModelProperty(value = "沟通意愿")
    private Integer character1;

    @Excel(name = "内方外圆", width = 15)
    @ApiModelProperty(value = "内方外圆")
    private Integer character2;

    @Excel(name = "社交自信", width = 15)
    @ApiModelProperty(value = "社交自信")
    private Integer character3;

    @Excel(name = "宜人性", width = 15)
    @ApiModelProperty(value = "宜人性")
    private Integer character4;

    @Excel(name = "擅于分析", width = 15)
    @ApiModelProperty(value = "擅于分析")
    private Integer character5;

    @Excel(name = "擅于总结", width = 15)
    @ApiModelProperty(value = "擅于总结")
    private Integer character6;

    @Excel(name = "条理性", width = 15)
    @ApiModelProperty(value = "条理性")
    private Integer character7;

    @Excel(name = "灵活性", width = 15)
    @ApiModelProperty(value = "灵活性")
    private Integer character8;

    @Excel(name = "韧性", width = 15)
    @ApiModelProperty(value = "韧性")
    private Integer character9;

    @Excel(name = "决策性", width = 15)
    @ApiModelProperty(value = "决策性")
    private Integer character10;

    @Excel(name = "竞争性", width = 15)
    @ApiModelProperty(value = "竞争性")
    private Integer character11;

    @Excel(name = "性格特质", width = 15)
    @ApiModelProperty(value = "性格特质")
    private List<String> characterDetails;

    @Excel(name = "性格描述文案", width = 15)
    @ApiModelProperty(value = "性格描述文案")
    private String characterText;

    @Excel(name = "具体驱动因素项", width = 15)
    @ApiModelProperty(value = "具体驱动因素项")
    private String drivingFactors;

    @Excel(name = "期望行业", width = 15)
    @ApiModelProperty(value = "期望行业")
    private String expectIndustry;

    @Excel(name = "期望公司", width = 15)
    @ApiModelProperty(value = "期望公司 企业性质（enterprise_quality）+组织规模（organization_scale）")
    private String expectCompany;

    @Excel(name = "期望薪资", width = 15)
    @ApiModelProperty(value = "期望薪资")
    private String expectSalary;

    @Excel(name = "习得优势", width = 15)
    @ApiModelProperty(value = "习得优势")
    private String proposal1;

    @Excel(name = "自然优势", width = 15)
    @ApiModelProperty(value = "自然优势")
    private String proposal2;

    @Excel(name = "显著优势", width = 15)
    @ApiModelProperty(value = "显著优势")
    private String proposal3;

    @Excel(name = "风险领域", width = 15)
    @ApiModelProperty(value = "风险领域")
    private String proposal4;

    @Excel(name = "待发展领域", width = 15)
    @ApiModelProperty(value = "待发展领域")
    private String proposal5;

    @Excel(name = "可快速提升领域", width = 15)
    @ApiModelProperty(value = "可快速提升领域")
    private String proposal6;

    @ApiModelProperty(value = "综合")
    private Map<String, String> proposal;

    @Excel(name = "推送状态", width = 15)
    @ApiModelProperty(value = "状态 0-未推送，1-已推送，")
    private String status;

    @Excel(name = "更新时间", width = 15, format = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date updateTime;

    @Excel(name = "手机号", width = 15)
    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "工作经历")
    private List<WorkHistoryReport> workHistories;

    @Excel(name = "简历已推荐次数", width = 15)
    @ApiModelProperty(value = "简历已推荐次数")
    private Integer recommendCount;

    @ApiModelProperty(value = "岗位契合度")
    private String fitDegree;

    @ApiModelProperty(value = "投递时间")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date deliveryTime;

    @Excel(name = "", width = 15)
    @ApiModelProperty(value = "推荐表 id，用以更新推荐表简历的状态")
    private Integer postId;

}