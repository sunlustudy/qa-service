package io.choerodon.app.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.ImmutableMap;
import io.choerodon.api.dto.*;
import io.choerodon.app.service.*;
import io.choerodon.app.support.PersonalReportSupport;
import io.choerodon.domain.*;
import io.choerodon.infra.exception.AnswerBaseNotFoundException;
import io.choerodon.infra.exception.CommonException;
import io.choerodon.infra.mapper.*;
import io.choerodon.infra.utils.BeanUtil;
import io.choerodon.infra.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static io.choerodon.app.support.PersonalReportSupport.*;
import static io.choerodon.infra.utils.BeanUtil.convert;
import static io.choerodon.infra.utils.BeanUtil.isNotEmpty;


/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-09-17 15:57
 */
@Service
@Slf4j
public class PersonalPersonalReportServiceImpl implements PersonalReportService {

    @Autowired
    private WorkHistoryMapper workHistoryMapper;

    @Autowired
    private AnswerBankBaseMapper answerBankBaseMapper;

    @Autowired
    private ProfessionalSkillsService professionalSkillsService;

    @Autowired
    private CharacterPowerService characterPowerService;

    @Autowired
    private PersonalReportMapper personalReportMapper;

    @Autowired
    private JobInformationService jobInformationService;

    @Autowired
    private BaseRelationMapper baseRelationMapper;

    @Autowired
    private DemandMapper demandMapper;

    @Autowired
    private ReportListMapper reportListMapper;

    @Autowired
    private ReportListService reportListService;

    private static final long YEAR_SEC = 31536000000L;  // 一年的秒数

    @Autowired
    private ComprehensiveReportService comprehensiveReportService;


    /**
     * 处理个人报告的基础数据
     *
     * @param baseId
     * @return
     */
    private PersonalReportDTO buildPersonReport(Integer baseId) {

        AnswerBankBase bankBase = answerBankBaseMapper.selectById(baseId);
        if (bankBase == null) {
            log.error("answer base is null baseId:{}", baseId);
            throw new AnswerBaseNotFoundException("不存在基本信息，无法生成报告");
        }

        PersonalReportDTO personalReport = new PersonalReportDTO();
        personalReport.setBaseId(baseId);


        PersonalReportSupport personalReportSupport = new PersonalReportSupport();
        personalReportSupport.setPersonalReport(personalReport);

//        获取基础表信息
        personalReportSupport.setBankBase(bankBase);
        personalReportSupport.handleBase();

//        处理专业技能
        ProfessionalSkillsDTO professionalSkills = professionalSkillsService.get(baseId);
        personalReportSupport.setProfessionalSkills(professionalSkills);
        personalReportSupport.handleProfessionSkills();

//        处理个性
        CharacterPowerDTO characterPower = characterPowerService.get(baseId);
        personalReportSupport.setCharacterPower(characterPower);
        personalReportSupport.handleCharacter();

//        处理求职意向
        JobInformationDTO jobInformation = jobInformationService.get(baseId);
        personalReportSupport.setJobInformation(jobInformation);
        personalReportSupport.handleJobInfo();


//        获取用户手机号
        String wechatId = answerBankBaseMapper.selectById(baseId).getWechatId();
        List<BaseRelation> baseRelations = baseRelationMapper.selectByMap(ImmutableMap.of("wechat_id", wechatId));
        if (!ObjectUtils.isEmpty(baseRelations)) {
            personalReport.setPhone(baseRelations.get(0).getPhone());
        }

//      处理学历映射
        personalReport.setEducation(EDUCATION_MAP.get(personalReport.getEducation()));

        return personalReport;
    }


    @Override
    public Boolean initPersonalReport(Integer baseId) {
        PersonalReport report = convert(buildPersonReport(baseId), PersonalReport.class);
        return personalReportMapper.insert(report) > 0;
    }

    /**
     * 重新填写个人信息时更新个人报告
     *
     * @param baseId
     * @return
     */
    @Override
    public Boolean updateOrInit(Integer baseId) {
        PersonalReport oldReport = personalReportMapper.selectByBaseId(baseId);
        if (oldReport != null) {    // 若是存在则更新
            PersonalReportDTO personalReportDTO = buildPersonReport(baseId);
            PersonalReport report = convert(personalReportDTO, PersonalReport.class);
            report.setId(oldReport.getId());
            return personalReportMapper.updateById(report) > 0;
        } else { // 不存在则生成
            return initPersonalReport(baseId);
        }
    }


    public boolean create(ReportMidDTO record) {
        PersonalReport personalReport = convert(record, PersonalReport.class);
        if (personalReportMapper.insert(personalReport) != 1) {
            throw new CommonException("插入失败 personalReport: " + record);
        }
        return true;
    }


    @Override
    public boolean delete(Integer id) {
        return personalReportMapper.deleteById(id) > 0;
    }


    /**
     * 管理员手动填写能力素质方面进行更新
     *
     * @param record
     * @return
     */
    @Override
    public boolean update(ReportMidDTO record) {
//        处理两个需要计算字段
        record.setQuality1(record.getQuality2().add(record.getQuality3()).divide(BigDecimal.valueOf(2), 5, RoundingMode.DOWN));
        BigDecimal quality5 = record.getQuality6()
                .add(record.getQuality7())
                .add(record.getQuality8())
                .add(record.getQuality9())
                .add(record.getQuality10())
                .divide(BigDecimal.valueOf(5), RoundingMode.DOWN);
        record.setQuality5(quality5);

//        手动更新
        PersonalReport report = personalReportMapper.selectById(record.getId());
        if (report == null) {
            return false;
        }

        report.setQuality1(record.getQuality1());
        report.setQuality2(record.getQuality2());
        report.setQuality3(record.getQuality3());
        report.setQuality4(record.getQuality4());
        report.setQuality5(record.getQuality5());
        report.setQuality6(record.getQuality6());
        report.setQuality7(record.getQuality7());
        report.setQuality8(record.getQuality8());
        report.setQuality9(record.getQuality9());
        report.setQuality10(record.getQuality10());
        report.setQuality11(record.getQuality11());
        report.setQuality12(record.getQuality12());

//        处理下一步发展推荐
        handleNextDev(report);

//        更新
        if (personalReportMapper.updateById(report) != 1) {
            throw new CommonException("更新失败 personalReport: " + record);
        } else {
            comprehensiveReportService.asyncUpdate(report.getBaseId());  // 更新综合报告
        }
        return true;
    }

    @Override
    public PageInfo<PersonalReportDTO> search(Integer page, Integer size, Map<String, Object> params) {
        Page<PersonalReport> temps = PageHelper.startPage(page, size);
        personalReportMapper.selectByParams(params);
        Page<PersonalReportDTO> result = BeanUtil.convertPage(temps, PersonalReportDTO.class);
        result.getResult()
//                .peek(this::populateWorkHistory)  // 填充工作经历
                .forEach(PersonalPersonalReportServiceImpl::populateProposal);  // 处理能力展示区域
        return new PageInfo<>(result);
    }


    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

    private void populateWorkHistory(PersonalReportDTO p) {
        List<WorkHistory> workHistories = workHistoryMapper.selectByMap(ImmutableMap.of("base_id", p.getBaseId()));
        if (isNotEmpty(workHistories)) {
            AtomicReference<Long> workYears = new AtomicReference<>(0L);
            List<WorkHistoryReport> workHistoryReports = workHistories.stream()
                    .map(workHistory -> convert(workHistory, WorkHistoryReport.class))
                    .peek(workHistory -> {
                        if (workHistory.getSalesIndicators() != null
                                && workHistory.getSalesIndicators().compareTo(BigDecimal.ZERO) > 0
                                && workHistory.getCompletion() != null) {
                            workHistory.setSaleAndCompletion(DECIMAL_FORMAT.format(workHistory.getSalesIndicators()) + "万元/" + DECIMAL_FORMAT.format(workHistory.getCompletion()) + "万元");
                            workHistory.setSalePerCompletion(workHistory.getCompletion().divide(workHistory.getSalesIndicators(), 5, RoundingMode.DOWN));
                        }
//                        计算工作时长
                        if (workHistory.getEndTime() != null) {
                            workYears.updateAndGet(v -> v + (workHistory.getEndTime().getTime() - workHistory.getStartTime().getTime()));
                        } else {
                            workYears.updateAndGet(v -> v + (System.currentTimeMillis() - workHistory.getStartTime().getTime()));
                        }
//                        计算处理字段映射
                        handleWorkHistory(workHistory);
                    }).collect(Collectors.toList());
            p.setWorkYears(String.valueOf((workYears.get() / YEAR_SEC)));
            p.setWorkHistories(workHistoryReports);
        }
    }


    private static final Map<String, String> EDUCATION_MAP = new HashMap<>();

    static {

        EDUCATION_MAP.put("0", "硕士研究生及以上");
        EDUCATION_MAP.put("1", "大学本科");
        EDUCATION_MAP.put("2", "大学专科");
        EDUCATION_MAP.put("3", "高中/中专/技校");
        EDUCATION_MAP.put("4", "初中");
        EDUCATION_MAP.put("5", "小学及以下");
    }


    @Override
    public PersonalReportDTO obtainPersonalByBaseId(Integer baseId) {

//        从数据库获取个人报告基本数据
        PersonalReportDTO personalReportDTO = obtainPersonal(baseId);
        if (personalReportDTO != null) {
//        填充工作历史
            populateWorkHistory(personalReportDTO);
//        填充能力素质部分
            populateProposal(personalReportDTO);
//        填充性格特质部分
            populateCharacter(personalReportDTO);
        }

        return personalReportDTO;
    }


    /**
     * 获取个人报告
     *
     * @param baseId
     * @return
     */
    private PersonalReportDTO obtainPersonal(Integer baseId) {
        PersonalReport personalReport = personalReportMapper.selectByBaseId(baseId);
        if (personalReport == null
                && Boolean.TRUE.equals(initPersonalReport(baseId))) {
            return convert(personalReportMapper.selectByBaseId(baseId), PersonalReportDTO.class);
        }

        return convert(personalReport, PersonalReportDTO.class);
    }


    private static void populateProposal(PersonalReportDTO reportDTO) {
        Map<String, String> map = new HashMap<>();
        map.put("显著优势", reportDTO.getProposal3());
        map.put("可快速提升区域", reportDTO.getProposal6());
        map.put("自然优势", reportDTO.getProposal2());
        map.put("待发展优势", reportDTO.getProposal5());
        map.put("习得优势", reportDTO.getProposal1());
        map.put("风险领域", reportDTO.getProposal4());
        reportDTO.setProposal(map);
    }


    @Override
    public Boolean createReportList(ReportListDTO reportListDTO) {
        ReportList reportList = convert(reportListDTO, ReportList.class);
        reportList.setDeliveryTime(new Date());

        PersonalReport personalReport = personalReportMapper.selectByBaseId(reportList.getBaseId());
//        判断手动填写部分是否已填写
        try {
            for (int i = 1; i < 13; i++) {
                if (PropertyUtils.getProperty(personalReport, "quality" + i) == null) {
                    throw new CommonException("推送失败，请先填写个人报告素质部分！ 候选人名：" + reportList.getName());
                }
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error("obtain bean property failure :{}", e.getMessage(), e);
        }

//        更新个人报告推荐次数
        personalReport.setRecommendCount(personalReport.getRecommendCount() + 1);
        personalReport.setStatus("1");  // 设置为已推送
        if (personalReportMapper.updateById(personalReport) != 1) {
            throw new CommonException("个人报告更新已推荐次数失败！");
        }

        return reportListMapper.insert(reportList) > 0;
    }


    @Override
    @Transactional
    public Boolean createReportList(List<ReportListDTO> reportListDTOS) {
        if (!ObjectUtils.isEmpty(reportListDTOS)) {
            reportListDTOS.forEach(this::createReportList);
        }
        return true;
    }


    @Override
    public PageInfo<ReportListDTO> obtainReportList(Integer page, Integer size, Map<String, Object> params) {
//        获取当前用户创建的需求
        List<Demand> demands = demandMapper
                .selectByMap(ImmutableMap.of("created_by", SecurityUtils.getCurrentUser().getId()));
//        若是无创建的需求则直接返回空数据
        if (ObjectUtils.isEmpty(demands)) {
            return new PageInfo<>();
        }
        List<Integer> demandIds = demands.stream().map(Demand::getId).collect(Collectors.toList());
        params.put("demandIds", demandIds);
        Page<ReportList> temps = PageHelper.startPage(page, size);
        reportListMapper.selectByParams(params);
        Page<ReportListDTO> result = BeanUtil.convertPage(temps, ReportListDTO.class);
        return new PageInfo<>(result);
    }

    @Override
    public PageInfo<ReportListDTO> obtainReportListByStatus(Integer page, Integer size, Integer status
            , String name, String phone) {
        Map<String, Object> params = new HashMap<>();
        params.put("status", status);
        params.put("phone", phone);
        params.put("name", name);
        Page<ReportList> temps = PageHelper.startPage(page, size);
        reportListMapper.selectByParamsAdminAll(params);
        Page<ReportListDTO> result = BeanUtil.convertPage(temps, ReportListDTO.class);
        return new PageInfo<>(result);
    }


    @Override
    public PageInfo<ReportListDTO> obtainByParams(String name, String phone, Integer demandId,
                                                  Integer page, Integer size) {

        Map<String, Object> params = new HashMap<>();
        params.put("demandId", demandId);
        params.put("name", name);
        params.put("phone", phone);

        Page<ReportListDTO> result = PageHelper.startPage(page, size);
        reportListMapper.selectByDemand(params);
        return new PageInfo<>(result);
    }

    @Override
    public List<PersonalReportDTO> listPersonalReport() {
        List<PersonalReport> personalReports = personalReportMapper.selectByMap(new HashMap<>());
        return ObjectUtils.isEmpty(personalReports) ? Collections.emptyList()
                : personalReports.stream()
                .map(p -> convert(p, PersonalReportDTO.class))
                .peek(p -> populateCharacter(p))// 填充性格特质部分文案
                .collect(Collectors.toList());
    }


    @Override
    public boolean updateReportList(ReportListDTO reportListDTO) {
        ReportList reportList = new ReportList();
        reportList.setId(reportListDTO.getId());
        reportList.setStatus(reportListDTO.getStatus());
        return reportListMapper.updateById(reportList) > 0;
    }


    @Override
    public PersonalReportDTO obtainByBase(Integer baseId, Integer demandId) {

//      获取基本数据
        PersonalReportDTO personalReportDTO = obtainPersonalByBaseId(baseId);
//      获取推荐表数据，填充数据
        ReportListDTO reportList = reportListService.obtainByBaseIdAndDemandId(baseId, demandId);
        if (reportList != null) {
            personalReportDTO.setDeliveryTime(reportList.getDeliveryTime());
            personalReportDTO.setFitDegree(reportList.getFitDegree());
            personalReportDTO.setPostId(reportList.getId());
        }
        return personalReportDTO;
    }

    @Override
    public boolean initPersonalReportAll() {
        List<AnswerBankBase> answerBankBases = answerBankBaseMapper.selectByMap(new HashMap<>());
        if (!ObjectUtils.isEmpty(answerBankBases)) {
            answerBankBases.stream().map(AnswerBankBase::getId).forEach(p -> {
                try {
                    log.info("init personal report {}", (initPersonalReport(p) ? "success" : "failure"));
                } catch (Exception e) {
                    log.warn("init personal report failure baseId {}", p);
                }
            });
        }
        return true;
    }


    /**
     * 获取手机端的个人报告数据
     *
     * @param baseId
     * @return
     */
    @Override
    public PersonalReportMobileDTO obtainMobile(Integer baseId) {
        PersonalReportDTO personalReportDTO = obtainPersonal(baseId);
        if (personalReportDTO != null) {
//        填充能力素质部分，发展建议
            populateProposal(personalReportDTO);
//        填充性格特质部分
            populateCharacter(personalReportDTO);

            PersonalReportMobileDTO mobileDTO = convert(personalReportDTO, PersonalReportMobileDTO.class);
//        判断是否能力部分字段存在
            mobileDTO.setIsQualityExist(isQualityExist(personalReportDTO));
//        性格测试部分字段是否存在
            mobileDTO.setIsCharacterExist(isCharacterExist(personalReportDTO));
//        当性格特质与能力手输入部分同时存在才会生成发展建议
            mobileDTO.setIsProposalExist(isProposalExist(personalReportDTO));

            return mobileDTO;
        }

        return null;
    }


}
