package io.choerodon.infra.config.task;

import io.choerodon.app.service.ComprehensiveReportService;
import io.choerodon.app.service.PersonalReportService;
import io.choerodon.infra.mapper.PersonalReportMapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-09-29 17:06
 */
@Slf4j
@Component
public class RedisReceiver implements CommandLineRunner {


    @Autowired
    private PersonalReportService personalReportService;

    @Autowired
    private PersonalReportMapper reportMapper;

    @Autowired
    private ComprehensiveReportService comprehensiveReportService;

    @Autowired
    private RedissonClient redissonClient;

    @Value("${redis.topic.update-report-all}")
    private String updateReportAll;


    /**
     * @param message
     * @deprecated (基本不单独使用)
     */
    @Deprecated
    public void receiveMessage(String message) {
        log.info("准备生成个人简历 baseId={}", message);
        Integer baseId = Integer.parseInt(message);
        if (reportMapper.selectByBaseId(baseId) == null && personalReportService.initPersonalReport(baseId)) {
            log.info("生成个人简历成功，baseId={}", message);
        } else {
            log.info("个人简历生成失败，baseId={}", message);
        }
    }


    @Override
    public void run(String... args) {
        log.info("register redis listener ");
        redissonClient.getTopic(updateReportAll).addListener(Integer.class, (channel, msg) -> {
            log.info("start update reports baseId:{}", msg);
            if (Boolean.TRUE.equals(personalReportService.updateOrInit(msg))) {
                log.info("init or update personal report success baseId:{}", msg);
                log.info("update comprehensive report {}", (comprehensiveReportService.updateOrSave(msg) ? "success" : "failure"));
            } else {
                log.warn("init or update personal report baseId:{}", msg);
            }
        });
    }
}
