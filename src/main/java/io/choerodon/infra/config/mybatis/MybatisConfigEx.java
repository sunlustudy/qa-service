package io.choerodon.infra.config.mybatis;

import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-08-27 10:50
 */
@Configuration
public class MybatisConfigEx {


    /**
     * 乐观锁拦截器
     *
     * @return
     */
    @Bean
    public OptimisticLockerInterceptor optimisticLockerInterceptor() {
        return new OptimisticLockerInterceptor();
    }


}
