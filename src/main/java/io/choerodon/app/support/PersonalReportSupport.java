package io.choerodon.app.support;


import com.google.common.collect.ImmutableMap;
import io.choerodon.api.dto.*;
import io.choerodon.domain.AnswerBankBase;
import io.choerodon.domain.PersonalReport;
import io.choerodon.infra.exception.CommonException;
import io.choerodon.infra.utils.BeanUtil;
import io.vavr.Tuple2;
import io.vavr.collection.Stream;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

import static io.choerodon.app.service.impl.ComprehensiveReportServiceImpl.DRIVING_MAP;
import static io.choerodon.infra.utils.BeanUtil.isNotEmpty;
import static io.choerodon.infra.utils.BeanUtil.populate;

@Data
@Slf4j
public class PersonalReportSupport {

    private AnswerBankBase bankBase;

    private PersonalReportDTO personalReport;

    private ProfessionalSkillsDTO professionalSkills;

    private CharacterPowerDTO characterPower;

    private JobInformationDTO jobInformation;


    public void handleBase() {
        if (bankBase == null) {
            throw new CommonException("无基础表");
        } else {
//            填充基本的数据
            populate(bankBase, personalReport);
            personalReport.setGender((bankBase.getGender() == 0 ? "男" : "女"));
//            婚姻状态转换
            Optional.ofNullable(bankBase.getMarryStatus())
                    .filter(Objects::nonNull)
                    .map(p -> "0".equals(p) ? "未婚" : "已婚")
                    .ifPresent(personalReport::setMarryStatus);

//            年龄部分计算得出
            if (bankBase.getBirthday() != null) {
                Calendar calendar = new Calendar.Builder().build();
                calendar.setTime(bankBase.getBirthday());
                int birthYear = calendar.get(Calendar.YEAR);
                calendar.setTime(new Date());
                String year = String.valueOf(calendar.get(Calendar.YEAR) - birthYear);
                personalReport.setBirthday(year);
            }

//        毕业学校，
            if (!StringUtils.isEmpty(bankBase.getAcademy())) {
                if (bankBase.getAcademy().lastIndexOf('|') < 0) {
                    personalReport.setAcademy(bankBase.getAcademy());
                } else {
                    String academy = bankBase.getAcademy().substring(bankBase.getAcademy().lastIndexOf('|') + 1);
                    personalReport.setAcademy(academy);
                }
            }
        }

    }


    public void handleProfessionSkills() {
        if (professionalSkills != null) {
            String knowledge = handleProfessionSkills(professionalSkills, professionalSkills.getKnowledge());
            String skill = handleProfessionSkills(professionalSkills, professionalSkills.getSkill());
            personalReport.setKnowledge(knowledge);
            personalReport.setSkill(skill);
        }
    }


    public void handleCharacter() {
        if (characterPower != null) {
            handleCharacter(personalReport, characterPower);
        } else {
            personalReport.setDrivingFactors("");
        }
    }

    public void handleJobInfo() {
        if (jobInformation != null) {
            handleJobInfo(personalReport, jobInformation);
        }
    }


    private static final Map<String, String> SKILLS_MAP = new HashMap<>();

    static {
        SKILLS_MAP.put("basicCompetence", "产品/解决方案知识 ");
        SKILLS_MAP.put("accomplishment", "职业素养");
        SKILLS_MAP.put("salesReturn", "销售回款管理");
        SKILLS_MAP.put("crm", "客户关系管理");
        SKILLS_MAP.put("salesPerformance", "销售业绩管理");
        SKILLS_MAP.put("profession", "行业知识");
        SKILLS_MAP.put("sell", "销售渠道");
        SKILLS_MAP.put("flow", "销售业务流程");
        SKILLS_MAP.put("business", "商务礼仪");
        SKILLS_MAP.put("theory", "销售理论");
        SKILLS_MAP.put("communication", "沟通技能");
        SKILLS_MAP.put("influence", "影响技能");
        SKILLS_MAP.put("manage", "管理技能");
        SKILLS_MAP.put("negotiate", "谈判技能");
        SKILLS_MAP.put("timeManage", "时间管理");
        SKILLS_MAP.put("teleSale", "电话销售技能");
        SKILLS_MAP.put("netSale", "网络销售技能");
        SKILLS_MAP.put("meetingSale", "会议销售技能");
        SKILLS_MAP.put("exhibitionSale", "展会销售技能");
        SKILLS_MAP.put("developCustom", "开发客户技能");
        SKILLS_MAP.put("handleDissent", "处理异议技能");
        SKILLS_MAP.put("dealMaking", "促成交易技能");
        SKILLS_MAP.put("customerMaintenance", "客户维护技能");
        SKILLS_MAP.put("salePlan", "销售计划管理");
        SKILLS_MAP.put("saleContract", "销售合同管理");
    }

    /**
     * 处理专业技能字段
     */
    private String handleProfessionSkills(ProfessionalSkillsDTO professionalSkills, String field) {
        StringBuilder sum = new StringBuilder();
        String[] fieldNames = field.split("\\|");
        try {
            for (String s : fieldNames) {
                String value = (String) PropertyUtils.getProperty(professionalSkills, s);
                String name = SKILLS_MAP.get(s);
                sum.append(name).append(":").append(value).append("|");
            }
        } catch (Exception e) {
            log.error("handle profession skills failure ,{}", e.getMessage(), e);
        }
        return sum.toString();
    }

    private static int average(String... params) {
        double sum = Stream.of(params)
                .map(p -> convertStringToInt(p))
                .sum()
                .doubleValue();
        return (int) Math.ceil(sum / params.length);
    }

    private static int average(int... params) {

        int sum = 0;
        for (int param : params) {
            sum += param;
        }

        return (int) Math.ceil((double) sum / params.length);
    }


    private static int convertStringToInt(String numberStr) {
        int result = 0;
        try {
            result = Integer.parseInt(numberStr);
        } catch (Exception e) {
            log.error("转换错误");
        }
        return result;
    }


    private static void handleCharacter(PersonalReportDTO personalReport, CharacterPowerDTO characterPower) {
        int character1 = average(characterPower.getCharacter1(), characterPower.getCharacter2());
        int character2 = average(characterPower.getCharacter3(), characterPower.getCharacter4());
        int character3 = average(characterPower.getCharacter5(), characterPower.getCharacter6());
        int character4 = average(characterPower.getCharacter7(), characterPower.getCharacter8());
        int character5 = average(characterPower.getCharacter9(), characterPower.getCharacter10());
        int character6 = average(characterPower.getCharacter11(), characterPower.getCharacter11());
        int character7 = average(characterPower.getCharacter13(), characterPower.getCharacter14());

        int character8 = average(characterPower.getCharacter15(), characterPower.getCharacter16(), characterPower.getCharacter17());
        int character9 = average(characterPower.getCharacter18(), characterPower.getCharacter19());
        int character10 = average(characterPower.getCharacter20(), characterPower.getCharacter21());
        int character11 = average(characterPower.getCharacter22(), characterPower.getCharacter23());

        personalReport.setCharacter1(character1);
        personalReport.setCharacter2(character2);
        personalReport.setCharacter3(character3);
        personalReport.setCharacter4(character4);
        personalReport.setCharacter5(character5);
        personalReport.setCharacter6(character6);
        personalReport.setCharacter7(character7);
        personalReport.setCharacter8(character8);
        personalReport.setCharacter9(character9);
        personalReport.setCharacter10(character10);
        personalReport.setCharacter11(character11);
        personalReport.setDrivingFactors(characterPower.getDrivingFactors());

        //        处理驱动因素
        String drivingFactors = "";
        if (!StringUtils.isEmpty(personalReport.getDrivingFactors())) {
            drivingFactors = java.util.stream.Stream.of(personalReport.getDrivingFactors().split("\\|"))
                    .map(p -> DRIVING_MAP.get(p))
                    .reduce((p, q) -> p + "|" + q)
                    .orElse("");
        }
        personalReport.setDrivingFactors(drivingFactors);
    }


    private static final Map<String, String> ENTERPRISE_QUALITY = new HashMap<>(); // 企业性质应映射

    private static final Map<String, String> ORGANIZATION_SCALE = new HashMap<>(); // 组织规模映射

    private static final Map<String, String> SALES_MAP = new HashMap<>(); // 期望收入映射

    static {
        ENTERPRISE_QUALITY.put("0", "国企");
        ENTERPRISE_QUALITY.put("1", "民企");
        ENTERPRISE_QUALITY.put("2", "外企");
        ENTERPRISE_QUALITY.put("3", "非营利组织");
        ENTERPRISE_QUALITY.put("4", "事业单位");
        ENTERPRISE_QUALITY.put("5", "合资单位");


        ORGANIZATION_SCALE.put("0", "初创企业");
        ORGANIZATION_SCALE.put("1", "中小型企业");
        ORGANIZATION_SCALE.put("2", "大型企业");
        ORGANIZATION_SCALE.put("3", "世界500强企业");


        SALES_MAP.put("（0-20万】", "0-20万");
        SALES_MAP.put("（20-40万】", "20-40万");
        SALES_MAP.put("（40-60万】", "40-60万");
        SALES_MAP.put("（60-80万】", "60-80万");
        SALES_MAP.put("（80-100万】", "80-100万");
        SALES_MAP.put("（100-120万】", "100-120万");
        SALES_MAP.put("（120-140万】", "120-140万");
        SALES_MAP.put("（140-160万】", "140-160万");
        SALES_MAP.put("（160-180万】", "160-180万");
        SALES_MAP.put("（180-200万】", "180-200万");
        SALES_MAP.put("200万以上", "200万以上");

    }

    private void handleJobInfo(PersonalReportDTO personalReport, JobInformationDTO jobInformation) {

//        处理期望岗位
        StringBuilder expectPostSB = new StringBuilder(jobInformation.getExpectPost());
        if (expectPostSB.length() > 0) {
            if ("0".equals(expectPostSB.substring(0, 1))) {
                expectPostSB.replace(0, 1, "销售类岗位");
            } else {
                expectPostSB.replace(0, 1, "非销售类岗位");
            }
        }

//        剪切掉 |
        String expectPost = expectPostSB.toString();
        if (expectPost.endsWith("|")) {
            expectPost = expectPost.substring(0, expectPost.length() - 1);
        }
        personalReport.setExpectPost(expectPost);
        personalReport.setExpectCompany(jobInformation.getEnterpriseQuality() + jobInformation.getOrganizationScale());

//        处理企业性质
        String enterprises = jobInformation.getEnterpriseQuality();
        if (!StringUtils.isEmpty(enterprises)) {
//            拼接企业性质字段
            enterprises = "企业性质:" + Stream.of(enterprises.split("\\|"))
                    .map(p -> ENTERPRISE_QUALITY.get(p) == null ? "其他" : ENTERPRISE_QUALITY.get(p))
                    .reduce((p, q) -> p + "、" + q);
        }
        if (!StringUtils.isEmpty(jobInformation.getOrganizationScale())) {
            String organizations = Stream.of(jobInformation.getOrganizationScale().split("\\|"))
                    .map(p -> ORGANIZATION_SCALE.get(p) == null ? "其他" : ORGANIZATION_SCALE.get(p))
                    .reduce((p, q) -> p + "、" + q);
            personalReport.setExpectCompany(enterprises + "|企业规模:" + organizations);
        }


        personalReport.setExpectSalary(SALES_MAP.get(jobInformation.getExpectSalary()));
        personalReport.setExpectIndustry(jobInformation.getIndustry());  // 不做改动
        personalReport.setSelfEvaluation(jobInformation.getSelfEvaluation());
    }


    private static final String CHARACTER = "character";

    private static final Map<String, Tuple2<Map<String, String>, Map<String, String>>> CHARACTER_DETAILS_MAP
            = new HashMap<>();

    private static final Map<String, String> CHARACTER_MAP = new HashMap<>();

    static {

        // 初始化性格文案策略
        CHARACTER_DETAILS_MAP.put("character1", new Tuple2<>(
                ImmutableMap.of("标签", "独立行事", "释义", "在工作中，倾向于独立思考、行动或完成任务"),
                ImmutableMap.of("标签", "广泛沟通", "释义", "在工作中，倾向建立广泛社交并互动，与人合作共同完成任务")));

        CHARACTER_DETAILS_MAP.put("character2", new Tuple2<>(
                ImmutableMap.of("标签", "处事温和", "释义", "在工作中，愿意接受他人的命令和指示，谈判中通情达理态度温和"),
                ImmutableMap.of("标签", "强势主导", "释义", "在工作中，享受支配他人和强势主导的局面，谈判中倾向强势说服他人接受自己的想法和观点")));

        CHARACTER_DETAILS_MAP.put("character3", new Tuple2<>(
                ImmutableMap.of("标签", "低调谨慎", "释义", "在工作中，倾向谨慎对待新结识的人，避免一开始与他们交往过密；不倾向在公共场合直接暴露自己的想法，避免健谈或外露"),
                ImmutableMap.of("标签", "社交达人", "释义", "在工作中，从善如流，愿意适应不同风格的人，与他们建立联系，喜欢正式和公开的场合，享受公开讲话或发表自己的观点")));

        CHARACTER_DETAILS_MAP.put("character4", new Tuple2<>(
                ImmutableMap.of("标签", "独善其身", "释义", "在工作中，避免过度参与或干涉他人的问题或过多顾忌他人顾虑，倾向于让他人自己做决定"),
                ImmutableMap.of("标签", "乐于助人", "释义", "在工作中，乐于体恤他人情绪，愿意帮助和满足他人的诉求，致力于帮助有困难的人，乐于支持、培养、鼓励他人")));

        CHARACTER_DETAILS_MAP.put("character5", new Tuple2<>(
                ImmutableMap.of("标签", "直觉感知", "释义", "在工作中，避免过度依赖数据或分析，倾向以直觉主导思维方式，而非理性分析与决策"),
                ImmutableMap.of("标签", "理性推断", "释义", "希望问题相关的信息都在掌握之中，倾向以逻辑推理，从正反两面，对问题进行分析与推演，从而解决问题或进行决策")));

        CHARACTER_DETAILS_MAP.put("character6", new Tuple2<>(
                ImmutableMap.of("标签", "实战派", "释义", "在工作中，注重战术的落地、实用性和简单高效的解决方案，避免依赖或强调理论或模型"),
                ImmutableMap.of("标签", "理论派", "释义", "在工作中，致力于战略层面的推演，倾向运用模型或相关理论来理解复杂问题的不同层面")));

        CHARACTER_DETAILS_MAP.put("character7", new Tuple2<>(
                ImmutableMap.of("标签", "提纲挈领", "释义", "在工作中，关注事物的全貌，而非沉溺于细节，避免过度筹划"),
                ImmutableMap.of("标签", "条分缕析", "释义", "在工作中，关注各个层面的精准性，倾向在事前制定紧密的计划和安排")));

        CHARACTER_DETAILS_MAP.put("character8", new Tuple2<>(
                ImmutableMap.of("标签", "灵活变通", "释义", "随具体情况变化，而不会刻板地遵守时间期限，对局限自己施展拳脚的规章制度能够做出变通"),
                ImmutableMap.of("标签", "恪守规则", "释义", "在工作中，遵守许下的所有诺言，保证满足所有的承诺，认真执行所有的规章制度")));

        CHARACTER_DETAILS_MAP.put("character9", new Tuple2<>(
                ImmutableMap.of("标签", "浅尝辄止", "释义", "在工作中，理解压力对自己的意义，认为恐惧和焦虑在何时是适当的，会受他人观点的影响避免对未来抱有过于乐观的看法，接受合理批评，会花时间来接受失败的后果"),
                ImmutableMap.of("标签", "坚韧皮实", "释义", "在工作中，面对高度的压力，表现得相当冷静和放松，在引发强烈情绪的情境中依然保持镇定，总能看到问题的积极面，避免过度责备和批评自己，对于失败的结果和挫折有很强的复原力")));


        CHARACTER_DETAILS_MAP.put("character10", new Tuple2<>(
                ImmutableMap.of("标签", "周全决策", "释义", "更喜欢在轻松的节奏下工作，做决策之前，倾向于预演并考量所有可能的后果"),
                ImmutableMap.of("标签", "果断决策", "释义", "具备高度的能量和活力，关注任务的高效完成，即使信息不足，也能快速做出决策")));

        CHARACTER_DETAILS_MAP.put("character11", new Tuple2<>(
                ImmutableMap.of("标签", "沉稳可靠", "释义", "更加关注质量而非结果、数量或目标，避免表现出过于争强好胜"),
                ImmutableMap.of("标签", "雄心壮志", "释义", "在面对挑战较大的目标时，也有达成结果的雄心壮志，在竞争的环境中会激发斗志，渴望在竞争的环境中获胜")));


        CHARACTER_MAP.put("character1", "与人沟通（沟通意愿）");
        CHARACTER_MAP.put("character2", "影响他人（内方外圆）");
        CHARACTER_MAP.put("character3", "社交自信");
        CHARACTER_MAP.put("character4", "支持他人（宜人性");
        CHARACTER_MAP.put("character5", "问题分析（擅于分析）");
        CHARACTER_MAP.put("character6", "归纳思维（擅于总结）");
        CHARACTER_MAP.put("character7", "条理性");
        CHARACTER_MAP.put("character8", "灵活性");
        CHARACTER_MAP.put("character9", "韧性");
        CHARACTER_MAP.put("character10", "决断力（决策性）");
        CHARACTER_MAP.put("character11", "成就导向（竞争性）");

    }

    // 填充性格特质部分
    public static void populateCharacter(PersonalReportDTO personalReport) {
        List<String> characterDetails = new ArrayList<>();

//        若是性格特质为空则直接返回
        if (personalReport.getCharacter1() == null) {
            personalReport.setCharacterDetails(characterDetails);
            personalReport.setCharacterText("");
            return;
        }

        StringBuilder characterDescription = new StringBuilder("您的性格主要有以下几大特点：\n\n");
        // 用以之后拼接性格文本部分
        List<String> relationPart = new ArrayList<>();
        List<String> dealThingPart = new ArrayList<>();

        for (int i = 1; i <= 11; i++) {
            String fieldName = CHARACTER + i;
            Integer character = BeanUtil.getProperty(personalReport, fieldName, Integer.class);
            if (character <= 2) {
                Map<String, String> detail1 = CHARACTER_DETAILS_MAP.get(fieldName)._1;
                String temp = detail1.get("标签") + "：" + detail1.get("释义");
                if (i <= 4) relationPart.add(temp);
                if (i >= 7) dealThingPart.add(temp);
                characterDetails.add(detail1.get("标签"));
            }
            if (character >= 4) {
                Map<String, String> detail2 = CHARACTER_DETAILS_MAP.get(fieldName)._2;
                String temp = detail2.get("标签") + "：" + detail2.get("释义");
                if (i <= 4) relationPart.add(temp);
                if (i >= 7) dealThingPart.add(temp);
                characterDetails.add(detail2.get("标签"));
            }
        }

//        拼接性格描述文案部分
//        一、思维部分
        StringBuilder thinkStr = new StringBuilder();
        Integer character5 = personalReport.getCharacter5();
        Integer character6 = personalReport.getCharacter6();

        String character5Format = "在思维偏好方面，您更加倾向于%s，而非%s，这意味着：%s;\n";
        String character6Format = "您是个%s，而非%s，因为你%s\n";

        String character5Str;
        if (character5 <= 2) {
            character5Str = String.format(character5Format, CHARACTER_DETAILS_MAP.get("character5")._1.get("标签"),
                    CHARACTER_DETAILS_MAP.get("character5")._2.get("标签"),
                    CHARACTER_DETAILS_MAP.get("character5")._1.get("释义"));
        } else {
            character5Str = String.format(character5Format, CHARACTER_DETAILS_MAP.get("character5")._2.get("标签"),
                    CHARACTER_DETAILS_MAP.get("character5")._1.get("标签"),
                    CHARACTER_DETAILS_MAP.get("character5")._2.get("释义"));
        }

        String character6Str;
        if (character6 >= 4) {
            character6Str = String.format(character6Format, CHARACTER_DETAILS_MAP.get("character6")._2.get("标签"),
                    CHARACTER_DETAILS_MAP.get("character6")._1.get("标签"),
                    CHARACTER_DETAILS_MAP.get("character6")._2.get("释义"));
        } else {
            character6Str = String.format(character6Format, CHARACTER_DETAILS_MAP.get("character6")._1.get("标签"),
                    CHARACTER_DETAILS_MAP.get("character6")._2.get("标签"),
                    CHARACTER_DETAILS_MAP.get("character6")._1.get("释义"));
        }

        thinkStr.append("一、").append(character5Str).append(character6Str).append("\n");
        characterDescription.append(thinkStr);

//        人际偏好部分
        StringBuilder relationStr = new StringBuilder("二、在人际偏好方面，您呈现以下特质偏好：\n");
        for (int i = 0; i < relationPart.size(); i++) {
            relationStr.append(i + 1).append(". ").append(relationPart.get(i)).append("\n");
        }
        relationStr.append("\n");

//       三、处事偏好部分
        StringBuilder dealThingStr = new StringBuilder("三、在处事偏好方面，您呈现以下特质偏好：\n");
        for (int i = 0; i < dealThingPart.size(); i++) {
            dealThingStr.append(i + 1).append(". ").append(dealThingPart.get(i)).append("\n");
        }
        dealThingStr.append("\n");

        characterDescription.append(relationStr.toString())
                .append(dealThingStr.toString());


        personalReport.setCharacterDetails(characterDetails);
        personalReport.setCharacterText(characterDescription.toString());
    }


    private static final String PROPOSAL4 = "风险领域 以下能力可能较难发展，需要为自己制定措施，提醒自己尽量规避可能的风险：";
    private static final String PROPOSAL5 = "待发展领域 建议花一些精力发展以下能力：";
    private static final String PROPOSAL6 = "可快速提升领域 建议以第一优先度发展以下能力：";


    /**
     * 下一步发展建议
     *
     * @param reportDTO
     */
    public static void handleNextDev(PersonalReport reportDTO) {
        List<String> proposal1List = new ArrayList<>();
        List<String> proposal2List = new ArrayList<>();
        List<String> proposal3List = new ArrayList<>();
        List<String> proposal4List = new ArrayList<>();
        List<String> proposal5List = new ArrayList<>();
        List<String> proposal6List = new ArrayList<>();
        Map<String, List<String>> listMap = new HashMap<>();
//        待发展领域
        listMap.put("00", proposal4List);
        listMap.put("10", proposal5List);
        listMap.put("20", proposal6List);
//        优势
        listMap.put("01", proposal1List);
        listMap.put("11", proposal2List);
        listMap.put("21", proposal3List);

        try {
            //        业务思维
            String key;
            Integer y = checkQuality(reportDTO.getQuality2());
            Integer x = checkCharacter(average(reportDTO.getCharacter5(), reportDTO.getCharacter6()));
            key = x.toString() + y.toString();
            listMap.get(key).add("业务思维");

//        情绪管理
            y = checkQuality(reportDTO.getQuality3());
            x = checkCharacter(average(reportDTO.getCharacter8(), reportDTO.getCharacter9()));
            key = x.toString() + y.toString();
            listMap.get(key).add("情绪管理");


//        自信
            y = checkQuality(reportDTO.getQuality4());
            x = checkCharacter(average(reportDTO.getCharacter3(), reportDTO.getCharacter9()));
            key = x.toString() + y.toString();
            listMap.get(key).add("自信");


//        结果导向
            y = checkQuality(reportDTO.getQuality6());
            x = checkCharacter(average(reportDTO.getCharacter8(), reportDTO.getCharacter10(), reportDTO.getCharacter11()));
            key = x.toString() + y.toString();
            listMap.get(key).add("结果导向");


//        积极主动
            y = checkQuality(reportDTO.getQuality7());
            x = checkCharacter(reportDTO.getCharacter7());
            key = x.toString() + y.toString();
            listMap.get(key).add("积极主动");


//        客户导向
            y = checkQuality(reportDTO.getQuality8());
            x = checkCharacter(average(reportDTO.getCharacter1(), reportDTO.getCharacter4(), reportDTO.getCharacter5()));
            key = x.toString() + y.toString();
            listMap.get(key).add("客户导向");


//        影响他人
            y = checkQuality(reportDTO.getQuality9());
            x = checkCharacter(reportDTO.getCharacter2());
            key = x.toString() + y.toString();
            listMap.get(key).add("影响他人");


//        建立关系
            y = checkQuality(reportDTO.getQuality10());
            x = checkCharacter(reportDTO.getCharacter3());
            key = x.toString() + y.toString();
            listMap.get(key).add("建立关系");

//      领导团队
            y = checkQuality(reportDTO.getQuality12());
            x = checkCharacter(reportDTO.getCharacter2());
            key = x.toString() + y.toString();
            listMap.get(key).add("领导团队");

        } catch (NullPointerException e) {
            log.warn("性格字段存在空值");
        }

        String proposal1 = proposal1List.stream()
                .reduce((p, q) -> p + "," + q)
                .orElse("");
        String proposal2 = proposal2List.stream()
                .reduce((p, q) -> p + "," + q)
                .orElse("");
        String proposal3 = proposal3List.stream()
                .reduce((p, q) -> p + "," + q)
                .orElse("");

        String proposal4 = (isNotEmpty(proposal4List) ? PROPOSAL4 : "") + proposal4List.stream()
                .reduce((p, q) -> p + "," + q)
                .orElse("");
        String proposal5 = (isNotEmpty(proposal5List) ? PROPOSAL5 : "") + proposal5List.stream()
                .reduce((p, q) -> p + "," + q)
                .orElse("");
        String proposal6 = (isNotEmpty(proposal6List) ? PROPOSAL6 : "") + proposal6List.stream()
                .reduce((p, q) -> p + "," + q)
                .orElse("");

        reportDTO.setProposal1(proposal1);
        reportDTO.setProposal2(proposal2);
        reportDTO.setProposal3(proposal3);
        reportDTO.setProposal4(proposal4);
        reportDTO.setProposal5(proposal5);
        reportDTO.setProposal6(proposal6);
    }


    //    判断能力位置
    private static int checkQuality(BigDecimal quality) {
        if (quality.doubleValue() >= 3 && quality.doubleValue() <= 5) {
            return 1;  // 中
        } else {
            return 0;  // 低
        }
    }

    //      判断性格位置
    private static int checkCharacter(int character) {
        int status = -1;
        if (character >= 4 && character <= 5) {
            status = 2; // 高
        } else if (character == 3) {
            status = 1; // 中
        } else if (character >= 1 && character <= 2) {
            status = 0; // 低
        }
        return status;
    }


    static final Map<String, String> RESPONSIBLE_AREA_MAP = new HashMap<>(); // 负责区域映射
    private static final Map<String, String> MAIN_BUSSINESS_MAP = new HashMap<>(); // 主营业务映射
    private static final Map<String, String> KEY_EXPERIENCES_MAP = new HashMap<>(); // 关键经历映射
    private static final Map<String, String> DIMISSION_CAUSE_MAP = new HashMap<>(); // 离职原因映射


    static {
        RESPONSIBLE_AREA_MAP.put("0", "华东");
        RESPONSIBLE_AREA_MAP.put("1", "华中");
        RESPONSIBLE_AREA_MAP.put("2", "华南");
        RESPONSIBLE_AREA_MAP.put("3", "华北");
        RESPONSIBLE_AREA_MAP.put("4", "华西");

        MAIN_BUSSINESS_MAP.put("0", "OP产品");
        MAIN_BUSSINESS_MAP.put("1", "SAAS产品");
        MAIN_BUSSINESS_MAP.put("2", "物联网");
        MAIN_BUSSINESS_MAP.put("3", "人工智能");
        MAIN_BUSSINESS_MAP.put("4", "解决方案");
        MAIN_BUSSINESS_MAP.put("5", "其他");

        KEY_EXPERIENCES_MAP.put("0", "关键赢单");
        KEY_EXPERIENCES_MAP.put("1", "市场开拓");
        KEY_EXPERIENCES_MAP.put("2", "组建团队");
        KEY_EXPERIENCES_MAP.put("3", "扭转士气");
        KEY_EXPERIENCES_MAP.put("4", "其他");

        DIMISSION_CAUSE_MAP.put("0", "个人成长");
        DIMISSION_CAUSE_MAP.put("1", "职业发展");
        DIMISSION_CAUSE_MAP.put("2", "薪酬待遇");
        DIMISSION_CAUSE_MAP.put("3", "家庭原因");
        DIMISSION_CAUSE_MAP.put("4", "公司及团队原因");
        DIMISSION_CAUSE_MAP.put("5", "工作内容原因");
        DIMISSION_CAUSE_MAP.put("6", "身体原因");
        DIMISSION_CAUSE_MAP.put("7", "其他");
    }

    private static String reduceString(String[] src, Map<String, String> map) {
        if (src.length > 1) {
            return map.get(src[0]) + (src[1] == null || "null".equals(src[1]) ? "" : "-" + src[1]);
        } else if (src.length == 1) {
            return map.get(src[0]);
        } else {
            return "";
        }
    }

    public static void handleWorkHistory(WorkHistoryReport workHistoryReport) {


        if (workHistoryReport.getMainBusiness() != null) {
            String mainBusiness = Stream.of(workHistoryReport.getMainBusiness().split("\\|"))
                    .map(p -> MAIN_BUSSINESS_MAP.get(p))
                    .reduce((p, q) -> p + "|" + q);
            workHistoryReport.setMainBusiness(mainBusiness);

        }

        if (workHistoryReport.getResponsibleArea() != null) {
            String responsibleArea = Stream.of(workHistoryReport.getResponsibleArea().split("\\|"))
                    .map(p -> p.split("-"))
                    .map(p -> reduceString(p, RESPONSIBLE_AREA_MAP))
                    .reduce((p, q) -> p + "|" + q);
            workHistoryReport.setResponsibleArea(responsibleArea);
        }

        if (workHistoryReport.getKeyExperiences() != null) {
            String keyExperiences = Stream.of(workHistoryReport.getKeyExperiences().split("\\|"))
                    .map(p -> p.split("-"))
                    .map(p -> reduceString(p, KEY_EXPERIENCES_MAP))
                    .reduce((p, q) -> p + "|" + q);
            workHistoryReport.setKeyExperiences(keyExperiences);
        }


        if (workHistoryReport.getDimissionCause() != null) {
            String dimissionCause = Stream.of(workHistoryReport.getDimissionCause().split("\\|"))
                    .map(p -> p.split("-"))
                    .map(p -> reduceString(p, DIMISSION_CAUSE_MAP))
                    .reduce((p, q) -> p + "|" + q);
            workHistoryReport.setDimissionCause(dimissionCause);
        }
    }


    /**
     * 性格字段是否存在
     *
     * @param personalReportDTO
     * @return
     */
    public static boolean isCharacterExist(PersonalReportDTO personalReportDTO) {
        return personalReportDTO.getCharacter1() != null;
    }

    /**
     * 能力部分字段是否存在(web 页手动输入)
     *
     * @return
     */
    public static boolean isQualityExist(PersonalReportDTO personalReportDTO) {
        return personalReportDTO.getQuality1() != null;
    }

    /**
     * 发展建议是否存在
     *
     * @param personalReportDTO
     * @return
     */
    public static boolean isProposalExist(PersonalReportDTO personalReportDTO) {
        return isCharacterExist(personalReportDTO) && isQualityExist(personalReportDTO);
    }


}

