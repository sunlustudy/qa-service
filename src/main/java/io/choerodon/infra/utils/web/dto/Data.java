package io.choerodon.infra.utils.web.dto;

/**
 *
 * 响应数据实体
 *
 * @param <T>
 */
public class Data<T> {

    private  T data;

    private  String message;

    public Data(T data,String message){

        this.data = data;
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }



}
