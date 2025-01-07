package io.github.move.bricks.chi.utils.request;


import lombok.Builder;

import java.io.Serializable;

@Builder
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 返回code码，成功--0 ， 失败--1
     */
    private Integer code;

    /**
     * 返回提示信息
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;


    private static <T> Result<T> build(Integer code, String message, T data) {
        return Result.<T>builder()
                .code(code)
                .message(message)
                .data(data)
                .build();
    }

    private static <T> Result<T> build(Integer code, String message) {
        return Result.<T>builder()
                .code(code)
                .message(message)
                .build();
    }

    public static <T> Result<T> success() {
        return build(0, "ok", null);
    }

    public static <T> Result<T> success(T data) {
        return build(0, "ok", data);
    }

    public static <T> Result<T> success(String message, T data) {
        return build(0, message, data);
    }


    public static <T> Result<T> failed() {
        return build(1, "failed", null);
    }

    public static <T> Result<T> failed(String message) {
        return build(1, message, null);
    }

    public static <T> Result<T> failed(String message, T data) {
        return build(1, message, data);
    }

    public static <T> Result<T> failed(Integer code, String message) {
        return build(code, message);
    }


    static <T> Result<T> restResult(T data, int code, String msg) {
        Result<T> apiResult = new Result<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMessage(msg);
        return apiResult;
    }

    public Result() {
    }

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}





