package com.daxiang.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * Created by jiangyitao.
 */
@Data
public class Response<T> {

    private static final Integer SUCCESS = 1;
    private static final Integer FAIL = 0;
    private static final Integer ERROR = -1;

    private Integer status;
    private String msg;
    private T data;

    private static <T> Response<T> createResponse(Integer status, String msg, T data) {
        Response<T> response = new Response<>();
        response.setStatus(status);
        response.setMsg(msg);
        response.setData(data);
        return response;
    }

    @JsonIgnore
    public boolean isSuccess() {
        return SUCCESS.equals(status);
    }

    public static Response success() {
        return createResponse(SUCCESS, "success", null);
    }

    public static <T> Response<T> success(T data) {
        return createResponse(SUCCESS, "success", data);
    }

    public static Response success(String msg) {
        return createResponse(SUCCESS, msg, null);
    }

    public static <T> Response<T> success(String msg, T data) {
        return createResponse(SUCCESS, msg, data);
    }

    public static Response fail(String msg) {
        return createResponse(FAIL, msg, null);
    }

    public static <T> Response<T> fail(String msg, T data) {
        return createResponse(FAIL, msg, data);
    }

    public static Response error(String msg) {
        return createResponse(ERROR, msg, null);
    }

}
