package io.choerodon.infra.aop;

import com.alibaba.fastjson.JSON;
import io.choerodon.infra.exception.AnswerBaseNotFoundException;
import io.choerodon.infra.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/*
 * @description: 对于参数传递以及异常的处理
 * @author: syun
 * @create: 2019-07-18 16:34
 */
@Aspect
@Slf4j
@Scope
@Component
public class LogAspectAOP {


    /**
     * 返回的状态
     */
    public static final class ResponseStatus {
        public static final String SUCCESS = "success";// 成功
        public static final String ERROR = "error";// 错误
    }

    /**
     * 响应状态
     */
    public static final class ResponseString {
        public static final String STATUS = "status";// 状态
        public static final String ERROR_CODE = "error";// 错误代码
        public static final String MESSAGE = "message";// 消息
        public static final String DATA = "data";// 数据
        public static final String DETAIL = "detail";
    }


    @Pointcut("@annotation(io.choerodon.infra.annotation.LogAspect)")
    public void logPointCut() {

    }


    @Around("logPointCut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result;
        long startTime = System.currentTimeMillis();
        log.info("请求入口: {}.{}() 参数 = {}", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
        try {
            result = joinPoint.proceed(joinPoint.getArgs());
        } catch (Exception e) {
//            统一进行异常的处理
            log.error("出现异常，入口: {}.{}(),参数:{}", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
            log.error("异常信息: {}", e.getMessage(), e);
            result = handleException(joinPoint, e);
        }
        log.info("request {}.{}() cost {}ms", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), (System.currentTimeMillis() - startTime));
        return result;
    }

    private ResponseEntity handleException(ProceedingJoinPoint pjp, Throwable e) {
        Map<String, Object> result = new HashMap<>();
        int status = 500;
        result.put(ResponseString.STATUS, status);
        if (e instanceof CommonException) {
            result.put(ResponseString.MESSAGE, e.getMessage());
        } else if (e instanceof AnswerBaseNotFoundException) {
            result.put(ResponseString.MESSAGE, e.getMessage());
        } else if (e instanceof DuplicateKeyException) {
            result.put(ResponseString.MESSAGE, "唯一值重复");
            result.put(ResponseString.DETAIL, e.getCause().getMessage());
        } else if (e instanceof BadCredentialsException) {
            result.put(ResponseString.MESSAGE, "账户或者密码错误");
        } else if (e instanceof AccessDeniedException) {
            result.put(ResponseString.MESSAGE, "无权限");
            status = 403;
        } else if (e instanceof NullPointerException) {
            result.put(ResponseString.MESSAGE, "意外的数据源空值错误");
            status = 500;
        } else if (e instanceof RuntimeException) {
            result.put(ResponseString.MESSAGE, "错误，请查看运行日志，或者联系开发人员");
        }


        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(JSON.toJSONString(result));
    }


}
