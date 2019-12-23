package io.choerodon.app.service.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AtomicDouble;
import io.choerodon.api.dto.*;
import io.choerodon.app.service.*;
import io.choerodon.domain.ComprehensiveReport;
import io.choerodon.domain.WorkHistoryExtend;
import io.choerodon.infra.exception.CommonException;
import io.choerodon.infra.mapper.ComprehensiveReportMapper;
import io.choerodon.infra.mapper.CustomerResourcesChildMapper;
import io.choerodon.infra.mapper.WorkHistoryExtendMapper;
import io.choerodon.infra.utils.BeanUtil;
import io.vavr.Tuple2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.choerodon.infra.utils.BeanUtil.*;
import static java.lang.Double.parseDouble;


/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-09-23 09:42
 */
@Service
@Slf4j
public class ComprehensiveReportServiceImpl implements ComprehensiveReportService {


    @Autowired
    private ComprehensiveReportMapper comprehensiveReportMapper;

    @Autowired
    private PersonalReportService personalReportService;

    @Autowired
    private CharacterPowerService characterPowerService;

    @Autowired
    private ProfessionalSkillsService professionalSkillsService;

    @Autowired
    private CustomerResourcesService customerResourcesService;

    @Autowired
    private WorkHistoryExtendMapper workHistoryExtendMapper;

    @Autowired
    private CustomerResourcesChildMapper customerResourcesChildMapper;

    @Autowired
    private ReportListService reportListService;

    @Autowired
    private TaskExecutor taskExecutor;


    //    驱动因素
    public static final Map<String, String> DRIVING_MAP = new HashMap<>();

    static {
        DRIVING_MAP.put("1", "取得卓越成就");
        DRIVING_MAP.put("2", "个人成长");
        DRIVING_MAP.put("3", "实现自我价值");
        DRIVING_MAP.put("4", "在竞争取胜");
        DRIVING_MAP.put("5", "与他人建立良好关系");
        DRIVING_MAP.put("6", "为他人所需要");
        DRIVING_MAP.put("7", "权利与地位");
        DRIVING_MAP.put("8", "对他人产生积极影响");
        DRIVING_MAP.put("9", "获得认可");
    }


    private static final String QUALITY = "quality";

    /**
     * 生成综合报告
     *
     * @param baseId
     * @return
     */
    private ComprehensiveReportDTO initComprehensive(Integer baseId) {

        ComprehensiveReportDTO comprehensiveReportDTO = new ComprehensiveReportDTO();

//        处理基础字段
        PersonalReportDTO personalReport = personalReportService.obtainPersonalByBaseId(baseId);
        if (personalReport == null) {
            return null;
        }

//        是否手填部分字段为空
        for (int i = 1; i < 13; i++) {
            String s = QUALITY + i;
            try {
                if (PropertyUtils.getProperty(personalReport, s) == null) {
                    throw new CommonException("创建失败，能力素质部分存在空值，请编辑个人报告");
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                log.error("get PersonalReportDTO property {} error ,message:{}", s, e.getMessage(), e);
            }
        }

        comprehensiveReportDTO.setName(personalReport.getName());
        comprehensiveReportDTO.setBaseId(personalReport.getBaseId());
        comprehensiveReportDTO.setBirthday(personalReport.getBirthday());
        comprehensiveReportDTO.setGender(personalReport.getGender());
        comprehensiveReportDTO.setEducational(personalReport.getEducation() + "|" + personalReport.getAcademy() + "|" + personalReport.getProfessionalName());
        comprehensiveReportDTO.setSkills(personalReport.getSkill());
        comprehensiveReportDTO.setDrivingFactors(personalReport.getDrivingFactors());

        //        处理任职公司字段
        if (isNotEmpty(personalReport.getWorkHistories())) {
            String companies = personalReport.getWorkHistories().stream()
                    .map(WorkHistoryReport::getMoodyhas)
                    .filter(Objects::nonNull)
                    .reduce((p, q) -> p + "|" + q)
                    .orElse("");
            comprehensiveReportDTO.setCompanys(companies);
        }


//        处理优势劣势字段
        handleQuality(personalReport, comprehensiveReportDTO);

//        处理销售能力
        CharacterPowerDTO characterPowerDTO = characterPowerService.get(baseId);
        ProfessionalSkillsDTO professionalSkillsDTO = professionalSkillsService.get(baseId);
        if (characterPowerDTO != null && professionalSkillsDTO != null) {
            try {
                handleSaleCapability(professionalSkillsDTO, comprehensiveReportDTO, personalReport, characterPowerDTO);
            } catch (NullPointerException e) {
                log.warn("销售能力依赖字段存在空值 ");
                comprehensiveReportDTO.setSalesCapabilityIndex(new BigDecimal("0.00"));
            }
        }

//        处理客户资源
        @SuppressWarnings("unchecked")
        List<CustomerResourcesDTO> customerResources = (List<CustomerResourcesDTO>) customerResourcesService.obtainByBaseId(baseId).get("customerResources");
        List<Integer> confidences = customerResourcesChildMapper.selectCountGroup(baseId);
        handleResources(comprehensiveReportDTO, customerResources, confidences);

        try {
//        处理业绩能力
            handlePerformance(workHistoryExtendMapper.selectByBaseIdLimit(baseId), comprehensiveReportDTO);
        } catch (NullPointerException e) {
            log.warn("处理业绩能力依赖字段存在空值");
            comprehensiveReportDTO.setPerformanceCapabilityIndex("0.00");
        }

//        处理工作稳定性

        try {
            handleWKSta(personalReport, characterPowerDTO, comprehensiveReportDTO);
        } catch (NullPointerException e) {
            log.warn("工作稳定性依赖字段存在空值 ");
            comprehensiveReportDTO.setWorkingStability("");
        }

//         处理人才综合推荐
        try {
            handleRecommendIndex(comprehensiveReportDTO);
        } catch (NullPointerException e) {
            log.warn("存在必须字段为空，人才综合推荐计算失败！");
            comprehensiveReportDTO.setIndexExplain("");
        }

        return comprehensiveReportDTO;
    }


    /**
     * 处理历史业绩
     * fix 使用工作拓展表
     *
     * @param workHistories
     * @param comprehensiveReport
     */
    private static void handleHistoricalPerformance(List<WorkHistoryExtend> workHistories, ComprehensiveReportDTO comprehensiveReport) {
        List<HistoricalPerformance> list =
                workHistories.stream().map(workHistory -> {
                    HistoricalPerformance historicalPerformance = new HistoricalPerformance();
                    populate(workHistory, historicalPerformance);
//                    使用工作扩展表，无此字段
                    if (workHistory.getCompletion() != null) {
                        historicalPerformance.setPercentComplete(workHistory.getCompletion().divide(workHistory.getSalesIndicators(), RoundingMode.DOWN));
                    }


                    historicalPerformance.setIndustry(workHistory.getIndustry());
                    historicalPerformance.setProductsServices(workHistory.getProductsServices());
                    historicalPerformance.setYear(Integer.parseInt(workHistory.getYear()));

//                    处理负责区域的字段转换(使用工作扩展表，无此字段)
//                    if (workHistory.getResponsibleArea() != null) {
//                        String responsibleArea = io.vavr.collection.Stream.of(workHistory.getResponsibleArea().split("\\|"))
//                                .map(p -> p.split("-"))
//                                .map(p -> {
//                                    if (p.length > 1) {
//                                        return RESPONSIBLE_AREA_MAP.get(p[0]) + (p[1].equals("null") ? "" : "-" + p[1]);
//                                    } else if (p.length == 1) {
//                                        return RESPONSIBLE_AREA_MAP.get(p[0]);
//                                    } else {
//                                        return "";
//                                    }
//
//                                })
//                                .reduce((p, q) -> p + "|" + q);
//                        historicalPerformance.setResponsibleArea(responsibleArea);
//                    }
                    return historicalPerformance;
                }).collect(Collectors.toList());
        comprehensiveReport.setHistoricalPerformances(list);
    }


    private static BigDecimal sumPerformanceOrSalesIndicators(Stream<WorkHistoryExtend> stream, Function<WorkHistoryExtend, BigDecimal> opr) {
        return stream.map(opr).filter(Objects::nonNull).reduce(BigDecimal::add).
                orElse(new BigDecimal(0));
    }

    //        处理资源
    private static void handlePerformance(List<WorkHistoryExtend> workHistoryExtends, ComprehensiveReportDTO comprehensiveReport) {
        if (isNotEmpty(workHistoryExtends)) {
            BigDecimal performance = sumPerformanceOrSalesIndicators(workHistoryExtends.stream(), p -> p.getCompletion())
                    .divide(BigDecimal.valueOf(workHistoryExtends.size()), RoundingMode.DOWN);
            BigDecimal salesIndicators = sumPerformanceOrSalesIndicators(workHistoryExtends.stream(), p -> p.getSalesIndicators())
                    .divide(BigDecimal.valueOf(workHistoryExtends.size()), RoundingMode.DOWN);
            if (salesIndicators.doubleValue() > 0) {  // 避免计算0异常
                comprehensiveReport.setPerformanceCapabilityIndex(performance.divide(salesIndicators, RoundingMode.DOWN).toString());
            }
        }
    }


    private static final String LEVEL1 = "经过综合评估，该候选人的综合能力非常强，是不可多得的销售将才，属于顶级销售人才";

    private static final String LEVEL2 = "经过综合评估，该候选人的综合能力较强，属于中上游的销售人才";

//    采用字符串拼接的方式
//    private final static String LEVEL3 = "经过综合评估，该候选人的综合能力一般，他的优势在于（在销售能力、业绩能力、客户资源三个维度当中显示最高的得分）他的不足在于（在销售能力、业绩能力、客户资源三个维度当中显示最低的得分）";

    private static final String LEVEL4 = "经过综合评估，该候选人的综合能力水平较低，不管销售能力、业绩能力和客户资源各方面，都有较大的提升空间";

    private static final String LEVEL5 = "经过综合评估，该候选人的综合能力水平极低，在进行相关人才决策时建议谨慎考虑";


    //    处理人才综合推荐部分
    private static void handleRecommendIndex(ComprehensiveReportDTO comprehensiveReport) {

        double recommendedCompositeIndex = (comprehensiveReport.getSalesCapabilityIndex().doubleValue()
                + parseDouble(comprehensiveReport.getPerformanceCapabilityIndex())
                + parseDouble(comprehensiveReport.getCustomerResourceIndex())) / 15;

        comprehensiveReport.setRecommendedCompositeIndex(BigDecimal.valueOf(recommendedCompositeIndex));

        if (recommendedCompositeIndex > 0.85) {
            comprehensiveReport.setIndexLevel("极高");
        }
        if (recommendedCompositeIndex >= 0.70 && recommendedCompositeIndex < 0.85) {
            comprehensiveReport.setIndexLevel("较高");
        }
        if (recommendedCompositeIndex >= 0.60 && recommendedCompositeIndex < 0.70) {
            comprehensiveReport.setIndexLevel("一般");
        }
        if (recommendedCompositeIndex >= 0.45 && recommendedCompositeIndex < 0.60) {
            comprehensiveReport.setIndexLevel("较低");
        }
        if (recommendedCompositeIndex < 0.45) {
            comprehensiveReport.setIndexLevel("极低");
        }

        String level = comprehensiveReport.getIndexLevel();
        String levelExplain = "";
        if ("极高".equals(level)) {
            levelExplain = LEVEL1;
        }
        if ("较高".equals(level)) {
            levelExplain = LEVEL2;
        }
        if ("一般".equals(level)) {
            String levelParamMax = handleMaxOrMin(comprehensiveReport, true);
            String levelParamMin = handleMaxOrMin(comprehensiveReport, false);
            levelExplain = String.format("经过综合评估，该候选人的综合能力一般，他的优势在于 %s 他的不足在于 %s ", levelParamMax, levelParamMin);
        }
        if ("较低".equals(level)) {
            levelExplain = LEVEL4;
        }
        if ("极低".equals(level)) {
            levelExplain = LEVEL5;
        }
        comprehensiveReport.setIndexExplain(levelExplain);
    }


    private static String handleMaxOrMin(ComprehensiveReportDTO comprehensiveReport, boolean isMax) {
        Double salesCapabilityIndex = comprehensiveReport.getSalesCapabilityIndex().doubleValue();
        Double customerIndex = comprehensiveReport.getCustomerIndex().doubleValue();
        Double performanceCapabilityIndex = Double.valueOf(comprehensiveReport.getPerformanceCapabilityIndex());

        if (isMax) {
            if (salesCapabilityIndex >= customerIndex && salesCapabilityIndex >= performanceCapabilityIndex) {
                return "销售能力";
            }
            if (customerIndex >= salesCapabilityIndex && customerIndex >= performanceCapabilityIndex) {
                return "客户资源";
            }
            if (performanceCapabilityIndex >= salesCapabilityIndex && performanceCapabilityIndex >= customerIndex) {
                return "业绩能力";
            }
        } else {
            if (salesCapabilityIndex <= customerIndex && salesCapabilityIndex <= performanceCapabilityIndex) {
                return "销售能力";
            }
            if (customerIndex <= salesCapabilityIndex && customerIndex <= performanceCapabilityIndex) {
                return "客户资源";
            }
            if (performanceCapabilityIndex <= salesCapabilityIndex && performanceCapabilityIndex <= customerIndex) {
                return "业绩能力";
            }
        }

        return "";
    }


    //    处理工作稳定性
    private static void handleWKSta(PersonalReportDTO personalReport, CharacterPowerDTO characterPower,
                                    ComprehensiveReportDTO comprehensiveReport) {

        double sum = 5;
//        平均在职时间
        if (isNotEmpty(personalReport.getWorkHistories())) {
            double year = parseDouble(personalReport.getWorkYears()) / personalReport.getWorkHistories().size();
            if (year > 5) {
                sum++;
            } else if (year >= 3 && year <= 5) {
                sum += 0.5;
            } else if (year < 1) {
                sum--;
            } else if (year < 2) {
                sum = sum - 0.5;
            }
        }

        int age = Integer.parseInt(personalReport.getBirthday());
        if (age >= 40 && age <= 50) {
            sum = sum + 0.5;
        }
        if (age >= 20 && age <= 35) {
            sum = sum - 0.5;
        }

        if ("已婚".equals(personalReport.getMarryStatus())) {
            sum = sum + 0.5;
        } else {
            sum = sum - 0.5;
        }

        int character15 = Integer.parseInt(characterPower.getCharacter15());  // 风险偏好
        if (character15 >= 4 && character15 <= 5) {
            sum = sum - 0.5;
        }
        if (character15 >= 1 && character15 <= 2) {
            sum = sum + 0.5;
        }

        int character18 = Integer.parseInt(characterPower.getCharacter18()); // 抗压性
        int character19 = Integer.parseInt(characterPower.getCharacter19()); // 恢复力

        if (character18 >= 4 && character18 <= 5) {
            sum = sum - 0.25;
        }
        if (character18 >= 1 && character18 <= 2) {
            sum = sum + 0.25;
        }

        if (character19 >= 4 && character19 <= 5) {
            sum = sum - 0.25;
        }
        if (character19 >= 1 && character19 <= 2) {
            sum = sum + 0.25;
        }
        comprehensiveReport.setWorkingStability(String.valueOf(sum));

    }


    private static final Map<String, Integer> AMOUNT_MAP = new HashMap<>();  //签单金额映射
    private static final Map<String, Integer> DUTY_MAP = new HashMap<>();  // 客户职务映射
    //    tuple2< 国企/外企，民企 >
    private static Map<Integer, Tuple2<Double, Double>> weight = new HashMap<>();

    //      签单金额转换映射
    private static Integer[][] ARR = new Integer[][]{
            {4, 3, 2, 1},
            {8, 6, 4, 2},
            {12, 9, 6, 3},
            {16, 12, 8, 4}
    };

    static {
        AMOUNT_MAP.put("100万以内", 1);
        AMOUNT_MAP.put("100-500万", 2);
        AMOUNT_MAP.put("500-1000万", 3);
        AMOUNT_MAP.put("1000万以上", 4);
//        客户职位映射
        DUTY_MAP.put("CEO", 1);
        DUTY_MAP.put("VP/CXO", 2);
        DUTY_MAP.put("总监", 3);
        DUTY_MAP.put("经理", 4);
        DUTY_MAP.put("专员", 5);
//      不同企业性质的权重  tuple2< 国企/外企，民企 >
        weight.put(1, new Tuple2<>(0.1, 0.35));
        weight.put(2, new Tuple2<>(0.2, 0.2));
        weight.put(3, new Tuple2<>(0.4, 0.25));
        weight.put(4, new Tuple2<>(0.25, 0.15));
        weight.put(5, new Tuple2<>(0.5, 0.05));
    }

    /**
     * 处理客户资源部分
     *
     * @param comprehensiveReport
     * @param customerResources
     * @param confidences
     */
    private static void handleResources(ComprehensiveReportDTO comprehensiveReport,
                                        List<CustomerResourcesDTO> customerResources,
                                        List<Integer> confidences) {

//        客户关系置信度
        AtomicDouble confidence = new AtomicDouble(confidences.size());
        AtomicDouble certificateScore = new AtomicDouble(0);
        if (isNotEmpty(customerResources)) {
            confidence.set(confidence.get() / customerResources.size() / 18);
            customerResources.stream().flatMap(p -> p.getCustomerResourcesChildDTOS().stream())
                    .forEach(p -> {
                        if (isNotEmpty(p.getCertificateIds())) {  // 若是上传凭证则 +1
                            certificateScore.addAndGet(1d);
                        }
                    });
            certificateScore.set(certificateScore.get() / customerResources.size());
        }
        comprehensiveReport.setCustomerRelationshipConfidence(BigDecimal.valueOf(confidence.get() + certificateScore.get()));


        if (!ObjectUtils.isEmpty(customerResources)) {
            //        资源能级指数
            Double incomeScale = customerResources.stream().map(p -> {
                if (p.getIncomeScale() != null) {
                    try {
                        return parseDouble(p.getIncomeScale()) * 10000;  // 小程序单位为亿，这里为 百万美金
                    } catch (Exception e) {
                        log.error("收入规模转换错误:{}，返回 0", p.getIncomeScale());
                        return 0d;
                    }
                } else {
                    return 0d;
                }
            })
                    .reduce(Double::sum)
                    .orElse(0d) / customerResources.size();
            Integer V = handleV(incomeScale);   // 计算出 V
            Integer staffSize = customerResources.stream().map(p -> {
                if (p.getStaffSize() != null) {
                    try {
                        return Integer.valueOf(p.getStaffSize());
                    } catch (NumberFormatException e) {
                        log.warn("人员规模转换错误: {}，返回 0", p.getStaffSize());
                        return 0;
                    }
                } else {
                    return 0;
                }

            })
                    .reduce(Integer::sum)
                    .orElse(0) / customerResources.size();

            Integer P = handleP(staffSize); // 计算出 P
            if (Math.abs(P - V) < 2) {
                comprehensiveReport.setResourceEnergyLevelIndex(String.valueOf(handlePAndV(V)));
            } else {
                comprehensiveReport.setResourceEnergyLevelIndex(String.valueOf(handlePAndV(Math.round(((float) (P + V)) / 2))));
            }
//        客户资源指数
            //        处理重点客户成单指数
            double result = 0.0d;
            for (CustomerResourcesDTO p : customerResources) {
                List<SigningRecordDTO> signingRecords = p.getSigningRecordDTOS();
                if (isNotEmpty(signingRecords)) {
                    Integer sum = 0;
//        排序
                    for (int i = 0; i < signingRecords.size(); i++) {
                        String amount = signingRecords.get(i).getAmount();
                        Integer index = AMOUNT_MAP.get(amount);
                        if (index != null && i < 4) {
                            sum += ARR[i][index - 1];
                        }
                    }
                    if (signingRecords.size() <= 4 && signingRecords.size() > 0) {
                        result += (double) sum / ((double) signingRecords.size() * 10);
                    } else {
                        result += (double) sum / 40;
                    }
                }
            }
            result = result / customerResources.size();
            comprehensiveReport.setCustomerIndex(BigDecimal.valueOf(result));

//        处理关系能量指数   relational_energy_index
            try {
                AtomicReference<Double> relationEnergyIndex = new AtomicReference<>(0.0d);
                customerResources.forEach(p -> {
                    AtomicReference<Double> tempIndex = new AtomicReference<>(0.0);  //临时指数
                    AtomicReference<Integer> count12 = new AtomicReference<>(0);
                    AtomicReference<Integer> count34 = new AtomicReference<>(0);
                    Map<String, Set<Integer>> levelFlag = new HashMap<>();
                    AtomicReference<Boolean> isOverThreeLevel = new AtomicReference<>(false);
                    p.getCustomerResourcesChildDTOS()
                            .forEach(child -> {
                                Integer dutyIndex = DUTY_MAP.get(child.getDuty());
                                Integer relationShip = Integer.parseInt(child.getRelationship());
                                if ("国企".equals(p.getEnterpriseQuality()) ||
                                        "外企".equals(p.getEnterpriseQuality())) {
                                    tempIndex.updateAndGet(v -> v + weight.get(dutyIndex)._1 * relationShip);
                                } else {
                                    tempIndex.updateAndGet(v -> v + weight.get(dutyIndex)._2 * relationShip);
                                }
//                        用以标记 if2
                                if ((dutyIndex == 1 || dutyIndex == 2) &&
                                        relationShip >= 4) {
                                    count12.getAndSet(count12.get() + 1);
                                }
                                if ((dutyIndex == 3 || dutyIndex == 4) &&
                                        relationShip >= 4) {
                                    count34.getAndSet(count34.get() + 1);
                                }

//                        用以处理同一个部门覆盖三个以上的层级，标记 f3
                                Set<Integer> set = levelFlag.get(child.getDepartment());
                                if (set == null) {
                                    levelFlag.put(child.getDepartment(), Sets.newHashSet(dutyIndex));
                                } else {
                                    set.add(dutyIndex);
                                    if (set.size() >= 3) {
                                        isOverThreeLevel.set(true);
                                    }
                                }

                            });

//            if2 1和2 3和4 中至少有一个大于等于4 则乘以 1.2 的系数
                    if (count12.get() > 0 && count34.get() > 0) {
                        tempIndex.updateAndGet(v -> v * 1.2);
                    }

//                若是跨域三个层级则 * 1.2
                    if (Boolean.TRUE.equals(isOverThreeLevel.get())) {
                        tempIndex.updateAndGet(v -> v * 1.2);
                    }
                    relationEnergyIndex.updateAndGet(v -> v + tempIndex.get());
                });

                if (customerResources.size() > 0) {
                    relationEnergyIndex.set(relationEnergyIndex.get() / customerResources.size());
                }
                comprehensiveReport.setRelationalEnergyIndex(BigDecimal.valueOf(relationEnergyIndex.get()));

            } catch (NullPointerException e) {
                log.warn("存在必须字段为空，关系资源指数计算失败，设置为默认值");
                comprehensiveReport.setRelationalEnergyIndex(new BigDecimal("0.00"));
            }
        }


//        处理客户资源指数
        try {
            int resourceEnergyLevelIndex = Integer.parseInt(comprehensiveReport.getResourceEnergyLevelIndex());
            double customerResourcesIndex = (comprehensiveReport.getRelationalEnergyIndex().doubleValue() + resourceEnergyLevelIndex) / 2 *
                    comprehensiveReport.getCustomerRelationshipConfidence().doubleValue();
            comprehensiveReport.setCustomerResourceIndex(String.valueOf(customerResourcesIndex));

        } catch (NullPointerException e) {
            log.warn("存在必须字段为空，关系能量指数计算失败！");
            comprehensiveReport.setCustomerResourceIndex("");
        }

    }


    private static int handlePAndV(Integer status) {
        int index = 1;
        if (status >= 20 && status <= 22) {
            index = 2;
        }
        if (status >= 23 && status <= 25) {
            index = 3;
        }
        if (status >= 26 && status <= 28) {
            index = 4;
        }
        if (status >= 29 && status <= 30) {
            index = 5;
        }
        return index;
    }


    private static int handleV(Double incomeScale) {
        int V1 = 17;

        if (incomeScale >= 0 && incomeScale < 12) {
            return V1;
        }
        if (incomeScale >= 12 && incomeScale < 25) {
            V1 = 18;
        }
        if (incomeScale >= 25 && incomeScale < 50) {
            V1 = 19;
        }
        if (incomeScale >= 50 && incomeScale < 75) {
            V1 = 20;
        }
        if (incomeScale >= 75 && incomeScale < 150) {
            V1 = 21;
        }
        if (incomeScale >= 150 && incomeScale < 325) {
            V1 = 22;
        }
        if (incomeScale >= 325 && incomeScale < 750) {
            V1 = 23;
        }
        if (incomeScale >= 750 && incomeScale < 1500) {
            V1 = 24;
        }
        if (incomeScale >= 1500 && incomeScale < 3500) {
            V1 = 25;
        }
        if (incomeScale >= 3500 && incomeScale < 5000) {
            V1 = 26;
        }
        if (incomeScale >= 5000 && incomeScale < 7500) {
            V1 = 27;
        }
        if (incomeScale >= 7500 && incomeScale < 30000) {
            V1 = 28;
        }
        if (incomeScale >= 30000 && incomeScale < 75000) {
            V1 = 29;
        }
        if (incomeScale >= 75000) {
            V1 = 30;
        }
        return V1;
    }

    private static int handleP(Integer staffSize) {
        int P = 17;
        if (staffSize >= 0 && staffSize < 12) {
            return P;
        }
        if (staffSize >= 12 && staffSize < 25) {
            P = 18;
        }
        if (staffSize >= 25 && staffSize < 50) {
            P = 19;
        }
        if (staffSize >= 50 && staffSize < 90) {
            P = 20;
        }
        if (staffSize >= 90 && staffSize < 240) {
            P = 21;
        }
        if (staffSize >= 240 && staffSize < 430) {
            P = 22;
        }
        if (staffSize >= 430 && staffSize < 1110) {
            P = 23;
        }
        if (staffSize >= 1110 && staffSize < 2850) {
            P = 24;
        }
        if (staffSize >= 2850 && staffSize < 7350) {
            P = 25;
        }
        if (staffSize >= 7350 && staffSize < 10600) {
            P = 26;
        }
        if (staffSize >= 10600 && staffSize < 19050) {
            P = 27;
        }
        if (staffSize >= 19050 && staffSize < 51250) {
            P = 28;
        }
        if (staffSize >= 51250 && staffSize < 137500) {
            P = 29;
        }
        if (staffSize >= 137500) {
            P = 30;
        }
        return P;
    }


    private static Map<String, Map<String, Double>> SALE_WEIGHT_MAP = new HashMap<>();  //销售能力计算权重配置

    static {
//        权重设置  |revers2e 后缀表示权重反转；|3 后缀表示下标
//          计划准备
        Map<String, Double> params1 = new HashMap<>();
        params1.put("quality7", 0.3);
        params1.put("character13", 0.15);
        params1.put("character14", 0.15);
        params1.put("character17|revers2e", 0.15);
        params1.put("flow", 0.05);
        params1.put("accomplishment", 0.05);
        params1.put("theory", 0.05);
        params1.put("timeManage", 0.05);
        params1.put("salePlan", 0.05);
        SALE_WEIGHT_MAP.put("salesCapabilityIndex1", params1);
//        搜集信息
        Map<String, Double> params2 = new HashMap<>();
        params2.put("quality2", 0.3);
        params2.put("character9", 0.1);
        params2.put("character10", 0.1);
        params2.put("character11", 0.1);
        params2.put("character12", 0.1);
        params2.put("character13", 0.1);
        params2.put("character17|revers2e", 0.1);
        params2.put("profession", 0.05);
        params2.put("sell", 0.05);
        SALE_WEIGHT_MAP.put("salesCapabilityIndex2", params2);

//     建立联系
        Map<String, Double> params3 = new HashMap<>();
        params3.put("quality12", 0.3);
        params3.put("character1", 0.1);
        params3.put("character5", 0.1);
        params3.put("character6", 0.1);
        params3.put("character8", 0.1);
        params3.put("sell", 0.05);
        params3.put("business", 0.05);
        params3.put("accomplishment", 0.05);
        params3.put("developCustom", 0.05);
        params3.put("driving|5", 0.05);
        SALE_WEIGHT_MAP.put("salesCapabilityIndex3", params3);
//          需求洞察
        Map<String, Double> params4 = new HashMap<>();
        params4.put("quality2", 0.2);
        params4.put("quality8", 0.2);
        params4.put("character2", 0.1);
        params4.put("character3", 0.1);
        params4.put("character7", 0.1);
        params4.put("character13", 0.1);
        params4.put("communication", 0.05);
        params4.put("influence", 0.05);
        params4.put("driving|3", 0.05);
        params4.put("driving|6", 0.05);
        SALE_WEIGHT_MAP.put("salesCapabilityIndex4", params4);

//          提出解决方案
        Map<String, Double> param5 = new HashMap<>();
        param5.put("quality2", 0.15);
        param5.put("quality8", 0.2);
        param5.put("character9", 0.1);
        param5.put("character10", 0.05);
        param5.put("character11", 0.1);
        param5.put("character12", 0.05);
        param5.put("driving|3", 0.05);
        param5.put("driving|6", 0.05);
        param5.put("basicCompetence", 0.05);
        param5.put("profession", 0.05);
        param5.put("communication", 0.05);
        param5.put("influence", 0.05);
        SALE_WEIGHT_MAP.put("salesCapabilityIndex5", param5);

//      应对客户质疑
        Map<String, Double> param6 = new HashMap<>();
        param6.put("quality3", 0.2);
        param6.put("quality4", 0.1);
        param6.put("quality9", 0.15);
        param6.put("character3|revers2e", 0.1);
        param6.put("character18", 0.1);
        param6.put("character19", 0.1);
        param6.put("character20", 0.1);
        param6.put("communication", 0.05);
        param6.put("influence", 0.05);
        param6.put("handleDissent", 0.05);
        SALE_WEIGHT_MAP.put("salesCapabilityIndex6", param6);

//        获取销售
        Map<String, Double> param7 = new HashMap<>();
        param7.put("quality6", 0.23);
        param7.put("quality9", 0.12);
        param7.put("character3", 0.05);
        param7.put("character4", 0.05);
        param7.put("character21", 0.05);
        param7.put("character22", 0.1);
        param7.put("character23", 0.1);
        param7.put("driving|1", 0.05);
        param7.put("driving|2", 0.05);
        param7.put("driving|3", 0.05);
        param7.put("theory", 0.02);
        param7.put("communication", 0.02);
        param7.put("influence", 0.02);
        param7.put("negotiate", 0.02);
        param7.put("dealMaking", 0.02);
        param7.put("salesPerformance", 0.05);
        SALE_WEIGHT_MAP.put("salesCapabilityIndex7", param7);
    }

    /**
     * 处理销售能力
     */
    private static void handleSaleCapability(ProfessionalSkillsDTO professionalSkills,
                                             ComprehensiveReportDTO comprehensiveReportDTO,
                                             PersonalReportDTO personalReportDTO,
                                             CharacterPowerDTO characterPower) {
        AtomicReference<Double> salesCapabilities = new AtomicReference<>((double) 0);  // 销售指数和
        AtomicReference<BigDecimal> salesCapability = new AtomicReference<>(BigDecimal.valueOf(0.0)); // 销售指数
        AtomicReference<BigDecimal> tempCharacter = new AtomicReference<>(); //性格部分计算临时变量


//        获取驱动因素
        String drivingFactors = characterPower.getDrivingFactors();
        String[] drivings; // 驱动因素分解
        if (drivingFactors != null) {
            drivings = characterPower.getDrivingFactors().split("\\|");
        } else {
            drivings = new String[]{};
        }

        SALE_WEIGHT_MAP.forEach((fieldName, map) -> {
            map.forEach((key, value) -> {
                if (key.startsWith(QUALITY)) {  // 能力素质部分
                    tempCharacter.set(getProperty(personalReportDTO, key, BigDecimal.class));
                    tempCharacter.updateAndGet(v -> v.multiply(BigDecimal.valueOf(value)));
                    salesCapability.updateAndGet(v -> v.add(tempCharacter.get()));
                } else if (key.startsWith("character")) { // 性格特质部分
                    if (key.endsWith("revers2e")) {  // 权重反转
                        double re = 6 - parseDouble(getProperty(characterPower, key.substring(0, key.indexOf('|'))));
                        salesCapability.updateAndGet(v ->
                                v.add(BigDecimal.valueOf(re * value)));
                    } else {
                        salesCapability.updateAndGet(v ->
                                v.add(BigDecimal.valueOf(parseDouble(getProperty(characterPower, key)) * value)));
                    }
                } else if (key.startsWith("driving")) {  // 驱动因素部分
                    String index = key.substring(key.indexOf('|') + 1);
                    for (String driving : drivings) {
                        if (index.equals(driving)) {
                            salesCapability.updateAndGet(v -> v.add(BigDecimal.valueOf(5 * value)));
                        }
                    }
                } else {
                    salesCapability.updateAndGet(v -> v.add(BigDecimal.valueOf(strToInt(getProperty(professionalSkills, key)) * value)));
                }
            });


            try {
                PropertyUtils.setProperty(comprehensiveReportDTO, fieldName, salesCapability.get());
                salesCapabilities.updateAndGet(v -> v + salesCapability.get().doubleValue());
                salesCapability.set(BigDecimal.valueOf(0.0)); // 清零
            } catch (Exception e) {
                log.error("字段值设置错误：{}", fieldName);
            }

        });

        comprehensiveReportDTO.setSalesCapabilityIndex(BigDecimal.valueOf((salesCapabilities.get() / 7)));
    }


    private static int strToInt(String numberStr) {
        int result = 0;
        if (numberStr != null) {
            try {
                result = Integer.parseInt(numberStr);
            } catch (NumberFormatException e) {
                log.warn("转换错误，以返回0处理");
                result = 0;
            }
        }
        return result;
    }


    private static final BigDecimal A = BigDecimal.valueOf(2.5);
    private static final BigDecimal B = BigDecimal.valueOf(1.5);
    private static final Map<String, String> QUALITIES = new HashMap<>();

    static {
        QUALITIES.put("quality1", "自我管理");
        QUALITIES.put("quality2", "理解业务");
        QUALITIES.put("quality3", "不骄不躁");
        QUALITIES.put("quality4", "自信");
        QUALITIES.put("quality5", "业务实现");
        QUALITIES.put("quality6", "使命必达");
        QUALITIES.put("quality7", "老谋深算");
        QUALITIES.put("quality8", "融化客户");
        QUALITIES.put("quality9", "利益结盟");
        QUALITIES.put("quality10", "经营关系");
        QUALITIES.put("quality11", "团队管理");
        QUALITIES.put("quality12", "带兵打仗");
    }


    /**
     * 处理优势与劣势项
     *
     * @param personalReportDTO
     * @param comprehensiveReportDTO
     */
    private static void handleQuality(PersonalReportDTO personalReportDTO, ComprehensiveReportDTO comprehensiveReportDTO) {
        List<String> advantages = new ArrayList<>();
        List<String> disadvantages = new ArrayList<>();

        for (int p = 1; p < 13; p++) {
            String key = QUALITY + p;
            BigDecimal quality = BeanUtil.getProperty(personalReportDTO, key, BigDecimal.class);
            if (quality != null) {
                if (quality.compareTo(A) > 0) {
                    advantages.add(QUALITIES.get(key));
                }
                if (quality.compareTo(B) < 0) {
                    disadvantages.add(QUALITIES.get(key));
                }
            }
        }

        String advantage = listReduce(advantages.stream());
        String disadvantage = listReduce(disadvantages.stream());
        comprehensiveReportDTO.setAdvantageAbilitys(advantage);
        comprehensiveReportDTO.setInferiorityAbilitys(disadvantage);
    }

    private static String listReduce(Stream<String> strings) {
        return strings.reduce((p, q) -> p + "|" + q).orElse("");
    }


    /**
     * 生成综合报告
     */
    @Override
    public Boolean create(Integer baseId) {
        ComprehensiveReportDTO comprehensiveReportDTO = initComprehensive(baseId);
        return comprehensiveReportDTO != null && create(comprehensiveReportDTO);
    }

    /**
     * 具体进行综合报告的插入操作
     *
     * @param record
     * @return
     */
    public boolean create(ComprehensiveReportDTO record) {
        ComprehensiveReport comprehensiveReport = convert(record, ComprehensiveReport.class);
        try {
            if (comprehensiveReportMapper.insert(comprehensiveReport) != 1) {
                log.error("插入失败 comprehensiveReport: {}", record);
                return false;
            }
        } catch (DuplicateKeyException e) {
            throw new CommonException("已存在相关综合报告");
        }
        return true;
    }


    @Override
    public boolean delete(Integer baseId) {
        if (comprehensiveReportMapper.deleteByMap(ImmutableMap.of("base_id", baseId)) != 1) {
            throw new CommonException("删除失败 可能不存在此综合报告,baseId: " + baseId);
        }
        return true;
    }


    /**
     * 重新生成综合报告
     *
     * @param baseId
     * @return
     */
    @Override
    @Transactional
    public boolean reInitReport(Integer baseId) {
        comprehensiveReportMapper.deleteByMap(ImmutableMap.of("base_id", baseId));
        return create(baseId);
    }


    @Override
    public Boolean updateOrSave(Integer baseId) {
        List<ComprehensiveReport> comprehensiveReports = comprehensiveReportMapper.selectByMap(ImmutableMap.of("base_id", baseId));
        ComprehensiveReportDTO comprehensiveReportDTO = initComprehensive(baseId);
        if (comprehensiveReportDTO == null) {
            return false;
        } else {
            if (ObjectUtils.isEmpty(comprehensiveReports)) {  // 若是不存在则进行创建操作
                return create(comprehensiveReportDTO);
            } else {
                comprehensiveReportDTO.setId(comprehensiveReports.get(0).getId());
                return comprehensiveReportMapper.updateById(convert(comprehensiveReportDTO, ComprehensiveReport.class)) > 0;
            }
        }
    }

    @Override
    public void asyncUpdate(Integer baseId) {
        taskExecutor.execute(() -> {
            if (Boolean.TRUE.equals(updateOrSave(baseId))) {
                log.info("update comprehensive report success baseId :{}", baseId);
            } else {
                log.info("update comprehensive report failure baseId :{}", baseId);
            }
        });
    }


    /**
     * 获取综合报告，填充额外数据
     *
     * @param baseId
     * @return
     */
    @Override
    public ComprehensiveReportDTO obtainByBaseId(Integer baseId) {
//       若是不存在此综合报告则触发创建操作
        List<ComprehensiveReport> comprehensiveReports = comprehensiveReportMapper.selectByMap(ImmutableMap.of("base_id", baseId));
        ComprehensiveReportDTO comprehensiveReportDTO;
        if (ObjectUtils.isEmpty(comprehensiveReports)) {
            comprehensiveReportDTO = initComprehensive(baseId);
            if (comprehensiveReportDTO != null && create(comprehensiveReportDTO)) {
                log.info("create comprehensive report success baseId :{}", baseId);
            } else {
                return null;
            }
        } else {
            comprehensiveReportDTO = convert(comprehensiveReports.get(0), ComprehensiveReportDTO.class);
        }

        // 处理性格字段
        PersonalReportDTO personalReport = personalReportService.obtainPersonalByBaseId(baseId);
        if (personalReport != null) {
            Map<String, Integer> personalities = new HashMap<>();
            personalities.put("沟通意愿", personalReport.getCharacter1());
            personalities.put("内方外圆", personalReport.getCharacter2());
            personalities.put("社交自信", personalReport.getCharacter3());
            personalities.put("宜人性", personalReport.getCharacter4());
            personalities.put("擅于分析", personalReport.getCharacter5());
            personalities.put("擅于总结", personalReport.getCharacter6());
            personalities.put("条理性", personalReport.getCharacter7());
            personalities.put("灵活性", personalReport.getCharacter8());
            personalities.put("韧性", personalReport.getCharacter9());
            personalities.put("决策性", personalReport.getCharacter10());
            personalities.put("竞争性", personalReport.getCharacter11());
            comprehensiveReportDTO.setPersonalities(personalities);
        }

        // 历史业绩情况
        handleHistoricalPerformance(workHistoryExtendMapper.selectByBaseIdLimit(baseId), comprehensiveReportDTO);
        return comprehensiveReportDTO;
    }


    @Override
    public ComprehensiveReportDTO obtainByBaseId(Integer baseId, Integer demandId) {
//        获取推荐表数据
        ReportListDTO reportList = reportListService.obtainByBaseIdAndDemandId(baseId, demandId);
        ComprehensiveReportDTO comprehensiveReportDTO = obtainByBaseId(baseId);
        if (reportList != null && comprehensiveReportDTO != null) {
            comprehensiveReportDTO.setDeliveryTime(reportList.getDeliveryTime());
            comprehensiveReportDTO.setFitDegree(reportList.getFitDegree());
            comprehensiveReportDTO.setPostId(reportList.getId());
            comprehensiveReportDTO.setPhone(reportList.getPhone());
        }
        return comprehensiveReportDTO;
    }

    /**
     * 获取所有综合报告
     *
     * @return
     */
    public List<ComprehensiveReportDTO> listComprehensiveReportDTO() {
        List<ComprehensiveReport> comprehensiveReports = comprehensiveReportMapper.selectByMap(new HashMap<>());
        return ObjectUtils.isEmpty(comprehensiveReports) ? Collections.emptyList()
                : convertList(comprehensiveReports, ComprehensiveReportDTO.class);
    }

}
