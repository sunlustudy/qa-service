package io.choerodon.infra.config.task;

/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-08-29 10:38
 */

import io.choerodon.infra.mapper.SmsRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class AsyncTasks {

    @Autowired
    private  SmsRecordMapper smsRecordMapper;



    @Async("taskExecutor")
    public void deleteOvertime() {
        log.info("delete timeout code");
        smsRecordMapper.deleteByOvertime(new Date());
    }


}
