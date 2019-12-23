package io.choerodon.infra.config.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import io.choerodon.infra.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-08-20 17:07
 */
@Slf4j
@Component
public class FieldFillHandler implements MetaObjectHandler {


    @Override
    public void insertFill(MetaObject metaObject) {
//        同一处理需要填充给的字段
        this.setFieldValByName("createdTime", new Date(), metaObject);
        this.setFieldValByName("createDate", new Date(), metaObject);
        this.setFieldValByName("updateDate", new Date(), metaObject);

        this.setFieldValByName("createTime", new Date(), metaObject);
        this.setFieldValByName("updateTime", new Date(), metaObject);
        if (SecurityUtils.getCurrentUser() != null) {
            this.setFieldValByName("createdBy", SecurityUtils.getCurrentUser().getId(), metaObject);
        }
    }


    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateDate", new Date(), metaObject);
        this.setFieldValByName("updateTime", new Date(), metaObject);
    }
}
