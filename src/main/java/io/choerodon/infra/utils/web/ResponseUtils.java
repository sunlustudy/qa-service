package io.choerodon.infra.utils.web;


import io.choerodon.infra.utils.web.dto.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

/**
 *
 * 响应数据包装工具类
 *
 */
public class ResponseUtils {


    public static  <T> ResponseEntity<Data<T>> res(T data){

        return res(data, HttpStatus.OK);
    }

    public static  <T> ResponseEntity<Data<T>> res(T data, HttpStatus status){

        return res(data,"",status);
    }


    public static  <T> ResponseEntity<Data<T>> res(T data, String message){

        return res(data,message, HttpStatus.OK);
    }


    public static  <T> ResponseEntity<Data<T>> res(T data, String message, HttpStatus status){

        return res(data,message,null,status);
    }


    public static  <T> ResponseEntity<Data<T>> res(T data, String message, MultiValueMap<String, String> headers, HttpStatus status){

        return new ResponseEntity(new Data(data,message),headers,status);
    }











}
