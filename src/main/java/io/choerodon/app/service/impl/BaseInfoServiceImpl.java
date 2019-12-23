package io.choerodon.app.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import com.google.common.collect.ImmutableMap;
import io.choerodon.api.dto.*;
import io.choerodon.app.service.*;
import io.choerodon.domain.*;
import io.choerodon.infra.exception.CommonException;
import io.choerodon.infra.mapper.AnswerBankBaseMapper;
import io.choerodon.infra.mapper.BaseRelationMapper;
import io.choerodon.infra.mapper.OrderRecordMapper;
import io.choerodon.infra.mapper.SmsRecordMapper;
import io.choerodon.infra.utils.BeanUtil;
import io.choerodon.infra.utils.RandomUtils;
import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.choerodon.infra.utils.BeanUtil.isNotEmpty;
import static io.choerodon.infra.utils.ExcelUtils.workbookWrite;


/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-08-27 10:58
 */
@Service
@Slf4j
public class BaseInfoServiceImpl implements BaseInfoService {

    private ThreadLocal<DateFormat> dateFormatStart = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy年MM月dd日HH:mm"));
    private ThreadLocal<DateFormat> dateFormatEnd = ThreadLocal.withInitial(() -> new SimpleDateFormat("HH:mm"));

    @Autowired
    private BaseRelationMapper baseRelationMapper;

    @Autowired
    private SmsServiceImpl smsService;

    @Autowired
    private SmsRecordMapper smsRecordMapper;

    @Autowired
    private AnswerBankBaseMapper answerBankBaseMapper;

    @Autowired
    private OrderRecordMapper orderRecordMapper;

    @Autowired
    private AnswerBankBaseService bankBaseService;

    @Autowired
    private CharacterPowerService characterPowerService;

    @Autowired
    private CustomerResourcesService customerResourcesService;

    @Autowired
    private JobInformationService jobInformationService;

    @Autowired
    private ProfessionalSkillsService professionalSkillsService;

    @Autowired
    private WorkHistoryService workHistoryService;

    @Autowired
    private AnswerBankBaseService answerBankBaseService;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private MemberService memberService;

    @Autowired
    private CardService cardService;

    @Autowired
    private CustomerResourcesChildService customerResourcesChildService;

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private ExcelService excelService;

    @Value("${redis.topic.personal}")
    String personalTopic;

    @Value("${redis.topic.update-report-all}")
    private String updateReportAll;

    /**
     * 绑定信息
     *
     * @param baseRelationDTO
     * @return
     */
    @Override
    public Boolean bind(BaseRelationDTO baseRelationDTO) {

        BaseRelation baseRelation = BeanUtil.convert(baseRelationDTO, BaseRelation.class);
        baseRelation.setId(null);
        if (baseRelationMapper.insert(baseRelation) != 1) {
            throw new CommonException("绑定用户失败： " + baseRelation);
        }
        return true;
    }

    @Value("${test.code}")
    private String testCode;  // 测试用的验证码

    /**
     * 检测验证码以及创建用户
     *
     * @param baseRelationDTO
     * @param code
     * @return
     */
    @Override
    @Transactional
    public Boolean checkAndBind(BaseRelationDTO baseRelationDTO, String code) {

        // 用以测试
        if ("hand".equals(code)) {
            return bind(baseRelationDTO);
        }

        SmsRecord smsRecord = smsRecordMapper.selectByPhoneAndWechatId(baseRelationDTO.getPhone(), baseRelationDTO.getWechatId());
        if (smsRecord == null) {
            throw new CommonException("验证码无效");
        }

        if (smsRecord.getExpireTime().before(new Date())) {
//            删除已过期验证码
            smsRecordMapper.deleteById(smsRecord.getId());
            throw new CommonException("验证码已过期");
        }

        log.info("stored code :{}, code: {}", smsRecord.getCode(), code);
        if (smsRecord.getCode().equals(code)) {
            bind(baseRelationDTO);
            smsRecordMapper.deleteById(smsRecord.getId());
            return true;
        } else {
            throw new CommonException("验证码错误");
        }

    }

    /**
     * 获取填写状态
     *
     * @param wechatId
     * @return
     */
    public Map<String, Object> getStatus(String wechatId) {
        Map<String, Object> info = new HashMap<>();

        AnswerBankBaseDTO bankBaseDTO = bankBaseService.getByWechatId(wechatId);

        info.put("base", bankBaseDTO != null);
        if (bankBaseDTO != null) {
            info.put("characterPower", characterPowerService.get(bankBaseDTO.getId()) != null);
            info.put("customerResources", (int) customerResourcesService.obtainByBaseId(bankBaseDTO.getId()).get("count") > 0);
            info.put("jobInformation", jobInformationService.get(bankBaseDTO.getId()) != null);
            info.put("professionalSkills", professionalSkillsService.get(bankBaseDTO.getId()) != null);
            WorkHistoryDTO workHistoryDTO = workHistoryService.get(bankBaseDTO.getId());
            info.put("workHistory", isNotEmpty(workHistoryDTO.getWorkHistories())
                    || isNotEmpty(workHistoryDTO.getWorkHistoryExtends()));

//            添加基础表的必填字段状态
            if (bankBaseDTO.getEducation() != null && bankBaseDTO.getAcademy() != null) {
                info.put("isBaseComplete", true);
            } else {
                info.put("isBaseComplete", false);
            }

        } else {
            info.put("characterPower", false);
            info.put("customerResources", false);
            info.put("jobInformation", false);
            info.put("professionalSkills", false);
            info.put("workHistory", false);
            info.put("isBaseComplete", false);
        }
        return info;
    }


    /**
     * 判断是否已经提交
     *
     * @param baseId
     * @return
     */
    @Override
    public boolean isSubmit(Integer baseId) {
//        只要存在 false 即返回 false，后续不会运行
        return characterPowerService.get(baseId) != null
                && jobInformationService.get(baseId) != null
                && professionalSkillsService.get(baseId) != null
                && isNotEmpty(workHistoryService.get(baseId).getWorkHistories())
                && (int) customerResourcesService.obtainByBaseId(baseId).get("count") > 0;
    }

    /**
     * 通过微信 id 获取关联手机号
     *
     * @param wechatId
     * @return
     */
    @Override
    public Map<String, Object> obtainByWechatId(String wechatId) {
        List<BaseRelation> list = baseRelationMapper.selectByMap(ImmutableMap.of("wechat_id", wechatId));
        Map<String, Object> result = new HashMap<>();
        if (isNotEmpty(list)) {
            result.put("relation", list.get(0));
            result.put("status", getStatus(list.get(0).getWechatId()));
        }
        return result;
    }

    /**
     * 解绑操作
     *
     * @param baseRelationDTO
     * @return
     */
    @Override
    public Boolean unBind(BaseRelationDTO baseRelationDTO) {

        if (baseRelationDTO.getId() != null) {
            if (baseRelationMapper.deleteById(baseRelationDTO.getId()) != 1) {
                throw new CommonException("解绑失败，或无此绑定信息 id: " + baseRelationDTO.getId());
            }

        } else {
            Map<String, Object> params = ImmutableMap.of(
                    "wechatId", baseRelationDTO.getWechatId(),
                    "phone", baseRelationDTO.getPhone());

            if (baseRelationMapper.deleteByParams(params) < 1) {
                throw new CommonException("解绑失败，解绑参数有误 params:" + params);
            }
        }
        return true;
    }


    /**
     * 发送绑定的二维码
     *
     * @param phone
     * @param wechatId
     * @return
     */

    @Override
    @Transactional
    public boolean sendCode(String phone, String wechatId) {

        // 生成验证码
        String code = RandomUtils.generateCodeNum();
        Date createTime = new Date();
        Date expire = new Date(createTime.getTime() + 1000 * 60 * 10);
        // 删除已发验证码记录
        smsRecordMapper.deleteByPhoneAndWechatId(phone, wechatId);

//        先插入验证码记录
        if (smsRecordMapper.insert(new SmsRecord(null, wechatId, phone, code, createTime, expire)) > 0) {
//            再发送短信，若是出错则回滚。
            return smsService.sendCode(code, phone);
        }

        return false;
    }


    @Override
    @Transactional
    public Boolean sendOrderInfo(String wechatId, Date date, String address) {

//        通过微信号获取基础表
        AnswerBankBase answerBankBase = Option.of(answerBankBaseMapper.selectByWechatId(wechatId))
                .filter(BeanUtil::isNotEmpty)
                .map(p -> p.get(0))
                .getOrElseThrow(() -> new CommonException("无此微信号的基础调查问卷信息，wechatId = " + wechatId));
//        通过微信号获取关联手机号
        BaseRelation baseRelation = Option.of(baseRelationMapper.selectByMap(ImmutableMap.of("wechat_id", wechatId)))
                .filter(p -> p != null && p.size() > 0)
                .map(p -> p.get(0))
                .getOrElseThrow(() -> new CommonException("无绑定的手机号"));
//         格式化预约时间
        String start = dateFormatStart.get().format(date);
        date = new Date(date.getTime() + 1000 * 60 * 90);
        String end = dateFormatEnd.get().format(date);
//        发送数据
        if (orderRecordMapper.insert(new OrderRecord(wechatId, baseRelation.getPhone(), date, address)) > 0) {
            return smsService.sendOrderInfo(start, end, address, baseRelation.getPhone(), answerBankBase.getName());
        } else {
            throw new CommonException("预约信息发送失败");
        }
    }

    @Override
    public void exportExcel(Map<String, Object> params, HttpServletResponse response) {
        List<AnswerBankBase> answerBankBases = answerBankBaseMapper.selectByMap(params);
        if (isNotEmpty(answerBankBases)) {
            long start = System.currentTimeMillis();
            log.info("start fetch data:  {}ms", System.currentTimeMillis());
            List<ReportExcelEntity> reportExcelEntities = answerBankBases.stream()
                    .parallel()
                    .map(p -> populateExcelEntity(p.getId()))
                    .collect(Collectors.toList());
            long cost = System.currentTimeMillis() - start;
            log.info("total cost {}ms", cost);
            writeWorkbook(reportExcelEntities, response);
        }
    }


    @Override
    public void exportExcel(Integer baseId, HttpServletResponse response) {
        List<ReportExcelEntity> list = new ArrayList<>();
//        客户数据导出测试代码
        Integer[] lists = new Integer[]{9, 15, 17};
        Stream.of(lists).forEach(p -> list.add(populateExcelEntity(p)));
        writeWorkbook(list, response);
    }


    private void writeWorkbook(List<ReportExcelEntity> reportExcelEntities, HttpServletResponse response) {

        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams("数据综合", "base"),
                ReportExcelEntity.class, reportExcelEntities);
        workbookWrite(workbook, "summary", response);
    }


    private ReportExcelEntity populateExcelEntity(Integer baseId) {

        ReportExcelEntity reportExcelEntity = new ReportExcelEntity();
        AnswerBankBaseDTO answerBankBaseDTO = answerBankBaseService.getById(baseId);
        reportExcelEntity.setAnswerBankBaseDTO(answerBankBaseDTO);

        List<BaseRelation> relations = baseRelationMapper.selectByMap(ImmutableMap.of("wechat_id", answerBankBaseDTO.getWechatId()));
        if (isNotEmpty(relations)) {
            reportExcelEntity.setPhone(relations.get(0).getPhone());
        }

        Map<String, Object> result = customerResourcesService.obtainByBaseId(answerBankBaseDTO.getId());
        if (result != null) {
            @SuppressWarnings("unchecked")
            List<CustomerResourcesDTO> customerResourcesDTOS = (List<CustomerResourcesDTO>) result.get("customerResources");
            reportExcelEntity.setCustomerResources(customerResourcesDTOS);
        }

        WorkHistoryDTO workHistoryDTO = workHistoryService.get(answerBankBaseDTO.getId());
        reportExcelEntity.setWorkHistories(workHistoryDTO.getWorkHistories());
        reportExcelEntity.setWorkHistoryExtends(workHistoryDTO.getWorkHistoryExtends());

        ProfessionalSkillsDTO professionalSkillsDTO = professionalSkillsService.get(answerBankBaseDTO.getId());
        reportExcelEntity.setProfessionalSkillsDTO(professionalSkillsDTO);

        JobInformationDTO jobInformationDTO = jobInformationService.get(answerBankBaseDTO.getId());
        reportExcelEntity.setJobInformationDTO(jobInformationDTO);

        CharacterPowerDTO characterPowerDTO = characterPowerService.get(answerBankBaseDTO.getId());
        reportExcelEntity.setCharacterPowerDTO(characterPowerDTO);

        return reportExcelEntity;
    }


    @Override
    public BaseInfoDTO getBaseInfoByBaseId(Integer baseId) throws InterruptedException {

        CountDownLatch countDownLatch = new CountDownLatch(3);
        AtomicReference<AnswerBankBaseDTO> answerBankBaseDTO = new AtomicReference<>();
        AtomicReference<JobInformationDTO> jobInformationDTO = new AtomicReference<>();
//        AtomicReference<ProfessionalSkillsDTO> professionalSkillsDTO = new AtomicReference<>();
        AtomicReference<WorkHistoryDTO> workHistoryDTO = new AtomicReference<>();

        taskExecutor.execute(() -> {
            answerBankBaseDTO.set(answerBankBaseService.getById(baseId));
            countDownLatch.countDown();
            log.info("obtain answerBankBase success");
        });
        taskExecutor.execute(() -> {
            jobInformationDTO.set(jobInformationService.get(baseId));
            countDownLatch.countDown();
            log.info("obtain job information success");
        });
//        防止之后又需要加入，注释处理，其他相关同此 2019/11/7
//        taskExecutor.execute(() -> {
//            professionalSkillsDTO.set(professionalSkillsService.get(baseId));
//            countDownLatch.countDown();
//            log.info("obtain professional skill success");
//        });
        taskExecutor.execute(() -> {
            workHistoryDTO.set(workHistoryService.get(baseId));
            countDownLatch.countDown();
            log.info("obtain work history success");
        });

        log.info((countDownLatch.await(5, TimeUnit.SECONDS) ? "all get success" : "no all get success"));
        BaseInfoDTO baseInfoDTO = new BaseInfoDTO();
        baseInfoDTO.setAnswerBankBaseDTO(answerBankBaseDTO.get());
        baseInfoDTO.setJobInformationDTO(jobInformationDTO.get());
        baseInfoDTO.setWorkHistoryDTO(workHistoryDTO.get());
//        baseInfoDTO.setProfessionalSkillsDTO(professionalSkillsDTO.get());

        return baseInfoDTO;
    }


    /**
     * 创建且更新
     *
     * @param baseInfoDTO
     * @return
     * @throws InterruptedException
     */
    @Deprecated
    @Override
    public Boolean saveAndUpdate(BaseInfoDTO baseInfoDTO) throws InterruptedException {
        AnswerBankBaseDTO answerBankBaseDTO = baseInfoDTO.getAnswerBankBaseDTO();
        Integer baseId = answerBankBaseDTO.getId();
        if (baseId == null) {
            throw new CommonException("base id is null");
        }

        CountDownLatch countDownLatch = new CountDownLatch(3);
        taskExecutor.execute(() -> {
            if (answerBankBaseService.getById(baseId) != null) {
                answerBankBaseService.update(answerBankBaseDTO);
            } else {
                answerBankBaseService.create(answerBankBaseDTO);
            }
            countDownLatch.countDown();
        });

        JobInformationDTO jobInformationDTO = baseInfoDTO.getJobInformationDTO();
        if (jobInformationDTO != null) {
            taskExecutor.execute(() -> {
                if (jobInformationService.get(baseId) != null) {
                    jobInformationService.update(jobInformationDTO);
                } else {
                    jobInformationService.create(jobInformationDTO);
                }
                countDownLatch.countDown();
            });
        } else {
            countDownLatch.countDown();
        }

//        ProfessionalSkillsDTO professionalSkillsDTO = baseInfoDTO.getProfessionalSkillsDTO();
//        if (professionalSkillsDTO != null) {
//            taskExecutor.execute(() -> {
//                if (professionalSkillsService.get(baseId) != null) {
//                    professionalSkillsService.update(professionalSkillsDTO);
//                } else {
//                    professionalSkillsService.create(professionalSkillsDTO);
//                }
//                countDownLatch.countDown();
//            });
//        } else {
//            countDownLatch.countDown();
//        }

        WorkHistoryDTO workHistoryDTO = baseInfoDTO.getWorkHistoryDTO();
        if (workHistoryDTO != null) {
            taskExecutor.execute(() -> {
                workHistoryService.deleteByBaseId(baseId);  //删除
                workHistoryService.create(workHistoryDTO);  //创建
                countDownLatch.countDown();
            });
        } else {
            countDownLatch.countDown();
        }

        if (countDownLatch.await(5, TimeUnit.SECONDS)) {
            log.info("base info get all success");
        } else {
            log.info("base info timeout");
        }

//          生成个人报告
        redissonClient.getTopic(updateReportAll).publish(baseId);

        return true;
    }


    @Override
    public void handleCardGet(Integer baseId) {
        BaseInfoDTO baseInfoDTO = null;
        try {
            baseInfoDTO = getBaseInfoByBaseId(baseId);
        } catch (InterruptedException e) {
            log.error("getBaseInfoByBaseId failure :{}", e.getMessage(), e);
            Thread.currentThread().interrupt();
        }

        // 只有基本信息全部填写完
        if (baseInfoDTO != null && calBaseInfoCompetePer(baseInfoDTO) == 1) {
            // 尝试获取商机卡
            if (cardService.saveIfCompleteBaseInfo(baseInfoDTO.getAnswerBankBaseDTO().getId())) {
                log.info("create three business card success");
            }

            // 尝试获取会员身份
            Integer inviterId = invitationService.getInviterId(baseId);
            if (inviterId != null && memberService.saveIfInvitedReach(inviterId)) {
                log.info("create or update member success inviterId = {}", inviterId);
            }
        }

    }


    /**
     * 获取用户会员，卡包，积分（未定）信息
     *
     * @param baseId
     * @return
     */
    @Override
    public ResourceDTO getResource(Integer baseId) throws InterruptedException {
        ResourceDTO resourceDTO = new ResourceDTO();
        resourceDTO.setBaseId(baseId);

        CountDownLatch countDownLatch = new CountDownLatch(6);

        taskExecutor.execute(() -> {
            //        填充会员信息
            resourceDTO.setMemberDTO(memberService.getMember(baseId));
            countDownLatch.countDown();
        });

        taskExecutor.execute(() -> {
//        填充卡包数量
            resourceDTO.setCardSize(cardService.countCard(baseId));
            countDownLatch.countDown();
        });

        taskExecutor.execute(() -> {
//        是否有商机卡
            resourceDTO.setHasBusinessCard(cardService.hasBusinessCardByBaseInfo(baseId));
            countDownLatch.countDown();
        });
        taskExecutor.execute(() -> {
//        是否有任务优先卡
            resourceDTO.setHasMissionCard(cardService.hasMissionCardByResources(baseId));
            countDownLatch.countDown();
        });
        taskExecutor.execute(() -> {
//       获取客户资源完成率
            resourceDTO.setResourceCompletePer(calResourcesCompletePer(baseId));
            countDownLatch.countDown();
        });

        taskExecutor.execute(() -> {
//       获取性格填写完成率
            resourceDTO.setCharacterPer(calCharacterPer(baseId));
            countDownLatch.countDown();
        });

        // 获取个人信息完善率
        resourceDTO.setBaseCompletePer(calBaseInfoCompetePer(baseId));
        if (countDownLatch.await(2, TimeUnit.SECONDS)) {
            log.info("all call success");
        } else {
            log.warn("something timeout");
        }
        return resourceDTO;
    }


    private Double calBaseInfoCompetePer(BaseInfoDTO baseInfoDTO) {
        if (baseInfoDTO != null) {
            double count = 0;
            if (baseInfoDTO.getAnswerBankBaseDTO() != null) {
                count++;
            }
            if (baseInfoDTO.getJobInformationDTO() != null) {
                count++;
            }
            if (isNotEmpty(baseInfoDTO.getWorkHistoryDTO().getWorkHistories()) ||
                    isNotEmpty(baseInfoDTO.getWorkHistoryDTO().getWorkHistoryExtends())) {
                count++;
            }
            return count / 3;
        } else {
            return 0.0d;
        }

    }


    @Override
    public Double calBaseInfoCompetePer(Integer baseId) {
        BaseInfoDTO baseInfoDTO = null;
        try {
            baseInfoDTO = getBaseInfoByBaseId(baseId);
        } catch (InterruptedException e) {
            log.error("getBaseInfoByBaseId failure :{}", e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return calBaseInfoCompetePer(baseInfoDTO);
    }

    /**
     * 基础综合信息是否填写完成
     *
     * @param baseId
     * @return
     */
    @Override
    public Boolean isBaseInfoCompete(Integer baseId) {
        return calBaseInfoCompetePer(baseId) == 1;
    }


    private static final List<String> RESOURCE_FIELD_NAMES = new ArrayList<>(); // 客户资源主表必填字段

    private static final List<String> RESOURCE_CHILD_FIELD_NAMES = new ArrayList<>(); // 客户字段子表必填

    private static final List<String> ANSWER_BASE_FIELD_NAMES = new ArrayList<>();   // 基本表必填字段

    private static final List<String> WORK_HISTORY_FIELD_NAMES = new ArrayList<>();  // 工作经历必填字段

    private static final List<String> JOB_INFO_FIELD_NAMES = new ArrayList<>();  // 求职信息必填字段


    static {
        Stream.of(CustomerResources.class.getDeclaredFields())
                .forEach(p -> RESOURCE_FIELD_NAMES.add(p.getName()));
        RESOURCE_FIELD_NAMES.remove("id");
        RESOURCE_FIELD_NAMES.remove("baseId");
        RESOURCE_FIELD_NAMES.remove("sort");


        Stream.of(CustomerResourcesChild.class.getDeclaredFields())
                .forEach(p -> RESOURCE_CHILD_FIELD_NAMES.add(p.getName()));
        RESOURCE_CHILD_FIELD_NAMES.remove("id");
        RESOURCE_CHILD_FIELD_NAMES.remove("customerId");
        RESOURCE_CHILD_FIELD_NAMES.remove("sort");

        Stream.of(AnswerBankBase.class.getDeclaredFields())
                .forEach(p -> ANSWER_BASE_FIELD_NAMES.add(p.getName()));
        ANSWER_BASE_FIELD_NAMES.remove("marryStatus");
        ANSWER_BASE_FIELD_NAMES.remove("habitation");
        ANSWER_BASE_FIELD_NAMES.remove("professionalName");
        ANSWER_BASE_FIELD_NAMES.remove("subject");


    }


    /**
     * 计算资源的完成率
     *
     * @return
     */
    @Override
    public Double calResourcesCompletePer(Integer baseId) {
        int fieldCount = 0;
        AtomicInteger hasValueCount = new AtomicInteger(0);
        List<CustomerResources> customerResources = customerResourcesService.listResourceByBaseId(baseId);
        List<CustomerResourcesChild> customerResourcesChildren = customerResourcesChildService.listResourcesByBaseId(baseId);

        if (isNotEmpty(customerResources)) {
            fieldCount += RESOURCE_FIELD_NAMES.size() * customerResources.size();
            for (CustomerResources customerResource : customerResources) {
                RESOURCE_FIELD_NAMES.forEach(p -> {
                    try {
//                        当字段不为空时 +1
                        if (PropertyUtils.getProperty(customerResource, p) != null) {
                            hasValueCount.getAndIncrement();
                        }
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        log.warn("get {} property  failure message:{} ", customerResource, e.getMessage(), e);
                    }
                });
            }
        }

        if (isNotEmpty(customerResourcesChildren)) {
            fieldCount += RESOURCE_CHILD_FIELD_NAMES.size() * customerResourcesChildren.size();
            for (CustomerResourcesChild customerResourcesChild : customerResourcesChildren) {
                RESOURCE_CHILD_FIELD_NAMES.forEach(p -> {
                    try {
//                        当字段不为空时 +1
                        if (PropertyUtils.getProperty(customerResourcesChild, p) != null) {
                            hasValueCount.getAndIncrement();
                        }
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) { // 触发可能性非常小
                        log.warn("get {} property  failure message:{} ", customerResourcesChild, e.getMessage(), e);
                    }
                });
            }
        }

        return hasValueCount.doubleValue() / (fieldCount == 0 ? 1 : fieldCount);
    }


    /**
     * 获取全部的填写状态
     *
     * @param baseId
     * @return
     * @link ExcelService.exportInvitationLink()
     */
    @Override
    public int getCompletePer(Integer baseId) {
        List<CustomerResources> customerResources = customerResourcesService.listResourceByBaseId(baseId);
        List<CustomerResourcesChild> customerResourcesChildren = customerResourcesChildService.listResourcesByBaseId(baseId);

        int customerPoint = 0;
        if (!ObjectUtils.isEmpty(customerResources) || !ObjectUtils.isEmpty(customerResourcesChildren)) {
            customerPoint++;
        }

        return (int) ((calBaseInfoCompetePer(baseId) + customerPoint) / 2);
    }


    /**
     * 计算性格特质填写完成率
     *
     * @param baseId
     * @return
     */
    private Double calCharacterPer(Integer baseId) {
        CharacterPowerDTO characterPowerDTO = characterPowerService.get(baseId);
        if (characterPowerDTO != null) {
            if (characterPowerDTO.getDrivingFactors() == null) {
                return 0.958d;
            } else {
                return 1.0d;
            }
        } else {
            return 0.0d;
        }

    }

    @Override
    public void checkResources() {
        answerBankBaseService.listBaseIds().forEach(p -> {
            if (calResourcesCompletePer(p) > 0) {
                cardService.saveIfCompleteResource(p);
            }
            if (Boolean.TRUE.equals(isBaseInfoCompete(p))) {
                cardService.saveIfCompleteBaseInfo(p);
            }
        });
    }


    @Override
    public void getInvitationExcel(HttpServletResponse response) {

        excelService.exportInvitationLink(response);


//        List<InvitationExcelEntity> invitationExcelEntities = new LinkedList<>();
//        List<AnswerBankBase> answerBankBases = answerBankBaseService.listAll();   // 获取所有的候选人
//        for (AnswerBankBase answerBankBase : answerBankBases) {
//            List<AnswerBankBaseDTO> answerBankBaseList =
//                    answerBankBaseService.listAnswerBaseByInviter(answerBankBase.getId());  // 获取候选人邀请的人员
//            if (isNotEmpty(answerBankBaseList)) {
//                InvitationExcelEntity invitationExcelEntity = new InvitationExcelEntity();
//                invitationExcelEntity.setName(answerBankBase.getName());
//                BaseRelation relation = getBaseRelation(answerBankBase.getWechatId());
//                if (relation != null) {
//                    invitationExcelEntity.setPhone(relation.getPhone());  // 添加邀请人的手机号
//                }
//                invitationExcelEntity.setAnswerBankBaseDTOS(answerBankBaseList);  // 添加候选人邀请的人员
//                invitationExcelEntities.add(invitationExcelEntity);
//            }
//        }
//        writeWorkbookInvitation(invitationExcelEntities, response);  // 写入
    }


    private void writeWorkbookInvitation(List<InvitationExcelEntity> invitationExcelEntities, HttpServletResponse response) {

        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams("数据综合", "base"),
                InvitationExcelEntity.class, invitationExcelEntities);
        workbookWrite(workbook, "summary", response);
    }


    private BaseRelation getBaseRelation(String wechatId) {
        List<BaseRelation> relations = baseRelationMapper.selectByMap(ImmutableMap.of("wechat_id", wechatId));
        return isNotEmpty(relations) ? relations.get(0) : null;
    }


}
