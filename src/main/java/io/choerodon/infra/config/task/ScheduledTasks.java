package io.choerodon.infra.config.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * @description: 定时任务
 * @author: syun
 * @create: 2019-08-08 15:29
 */
@Component
@Slf4j
public class ScheduledTasks {

    @Autowired
    private  AsyncTasks asyncTasks;



    /**
     * 每 12 小时分钟处理一次过期的验证码
     */
    @Scheduled(initialDelay = 1000 * 60 * 60 * 12, fixedRate = 1000 * 60 * 60 * 12)
    public void reportCurrentTime() {
        asyncTasks.deleteOvertime();
    }

    /**
     * 每天1点触发一次
     */
    @Scheduled(cron = "0 0 1 * * ? ")
    public void checkResources() {

    }


}
