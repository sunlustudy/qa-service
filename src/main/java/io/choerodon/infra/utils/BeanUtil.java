package io.choerodon.infra.utils;


import com.github.pagehelper.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.core.ReflectUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 * @description:
 * @author: syun
 * @create: 2019-08-05 10:00
 */
@Slf4j
public class BeanUtil {
    private static final Map<String, BeanCopier> BEAN_COPIER_MAP = new ConcurrentHashMap<>();

    public static <T, R> R convert(T t, Class<R> clzz) {
        if (t == null) { // 若是为空则直接返回
            return null;
        }
        BeanCopier beanCopier = getCopier(t.getClass(), clzz);
        @SuppressWarnings("unchecked")
        R r = (R) ReflectUtils.newInstance(clzz);
        beanCopier.copy(t, r, null);
        return r;
    }

    public static <T, R> void populate(T t, R r) {
        BeanCopier beanCopier = getCopier(t.getClass(), r.getClass());
        beanCopier.copy(t, r, null);
    }

    private static <R> BeanCopier getCopier(Class t, Class<R> clzz) {
        String key = genKey(t, clzz);
        BeanCopier beanCopier;
        if (BEAN_COPIER_MAP.get(key) == null) {
            beanCopier = BeanCopier.create(t, clzz, false);
            BEAN_COPIER_MAP.put(key, beanCopier);
        } else {
            beanCopier = BEAN_COPIER_MAP.get(key);
        }
        return beanCopier;
    }


    public static <T, R> R convert(T t, Class<R> clzz, String... ignore) {
        @SuppressWarnings("unchecked")
        R r = (R) ReflectUtils.newInstance(clzz);
        BeanUtils.copyProperties(t, r, ignore);
        return r;
    }


    public static <R> R convert(Map<String, Object> source, Class<R> clzz) {
        @SuppressWarnings("unchecked")
        R r = (R) ReflectUtils.newInstance(clzz);
        try {
            org.apache.commons.beanutils.BeanUtils.populate(r, source);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return r;
    }

    public static <T> Page<T> convertPage(final Page pageSource, final Class<T> dest) {
        Page<T> pageBack = new Page<>();
        pageBack.setPageNum(pageSource.getPageNum());
        pageBack.setTotal(pageSource.getTotal());
        pageBack.setPages(pageSource.getPages());
        pageBack.setPageSize(pageSource.getPageSize());
        if (pageSource.isEmpty()) {
            return pageBack;
        }
        for (Object temp : pageSource) {
            pageBack.add(convert(temp, dest));
        }

        return pageBack;
    }


    public static <T, R> List<R> convertList(final List<T> source, final Class<R> dest) {
        List<R> result = new ArrayList<>(source.size());
        source.forEach(p -> result.add(convert(p, dest)));
        return result;
    }

    private static <T, R> String genKey(Class t, Class<R> clzz) {
        return t.getName() + clzz.getName();
    }


    public static <T extends Map> boolean isNotEmpty(T t) {
        return t != null && t.size() > 0;
    }

    public static <T extends Collection> boolean isNotEmpty(T t) {
        return t != null && t.size() > 0;
    }

    public static <T extends Collection> boolean isEmpty(T t) {
        return t == null || t.size() == 0;
    }

    public static <T> boolean isNotEmpty(T[] t) {
        return t != null && t.length > 0;
    }

    public static String getProperty(Object src, String fieldName) {
        String value = null;
        try {
            value = (String) PropertyUtils.getProperty(src, fieldName);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.warn("get {} property {} failure ", src.getClass().getName(), fieldName);
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getProperty(Object src, String fieldName, Class<T> tClass) {
        T t = null;
        try {
            t = (T) PropertyUtils.getProperty(src, fieldName);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.warn("get {} property {} failure ", src.getClass().getName(), fieldName);
        }

        return t;
    }

}
