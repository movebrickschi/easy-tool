package io.github.move.bricks.chi.utils.request;


import io.github.move.bricks.chi.constants.LccConstants;
import lombok.Builder;

import java.io.Serializable;


@Builder
public class CResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 返回code码，成功--LccConstants.SUCCESS ， 失败--LccConstants.FAIL
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


    private static <T> CResult<T> build(Integer code, String message, T data) {
        return CResult.<T>builder()
                .code(code)
                .message(message)
                .data(data)
                .build();
    }

    private static <T> CResult<T> build(Integer code, String message) {
        return CResult.<T>builder()
                .code(code)
                .message(message)
                .build();
    }

    public static <T> CResult<T> success() {
        return build(LccConstants.SUCCESS, "ok", null);
    }

    public static <T> CResult<T> success(T data) {
        return build(LccConstants.SUCCESS, "ok", data);
    }

    public static <T> CResult<T> success(String message, T data) {
        return build(LccConstants.SUCCESS, message, data);
    }


    public static <T> CResult<T> failed() {
        return build(LccConstants.FAIL, "failed", null);
    }

    public static <T> CResult<T> failed(String message) {
        return build(LccConstants.FAIL, message, null);
    }

    public static <T> CResult<T> failed(String message, T data) {
        return build(LccConstants.FAIL, message, data);
    }

    public static <T> CResult<T> failed(Integer code, String message) {
        return build(code, message);
    }


    static <T> CResult<T> restResult(T data, int code, String msg) {
        CResult<T> apiCResult = new CResult<>();
        apiCResult.setCode(code);
        apiCResult.setData(data);
        apiCResult.setMessage(msg);
        return apiCResult;
    }

    public CResult() {
    }

    public CResult(Integer code, String message, T data) {
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





