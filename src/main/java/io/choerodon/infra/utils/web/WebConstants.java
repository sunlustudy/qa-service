package io.choerodon.infra.utils.web;

/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-10-11 14:08
 */
public final class WebConstants {


    public static final String ACCESS_TOKEN = "access_token";
    public static final  String REFRESH_TOKEN = "refresh_token";
    public static final String ACCESS_EXPIRE = "access_expire";
    public static final  String REFRESH_EXPIRE = "refresh_expire";

    public static final  long DEFAULT_ACCESS_EXPIRE = 60 * 60 * 1000;
    public static final  long DEFAULT_REFRESH_EXPIRE = 60 * 60 * 24 * 2 * 1000;

    public static final  String INVALID_CODE = "code is invalid";


}
