package io.github.movebrickschi.easytool.request.core;


import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import io.github.movebrickschi.easytool.core.constants.LccConstants;
import lombok.Builder;
import lombok.SneakyThrows;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;


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

    /**
     * 转成指定类型
     * 适用于将相同的字段转成为不同类型
     * @param typeReference 目标类型
     */
    @SneakyThrows
    public <R> R convert(TypeReference<R> typeReference) {
        return JSONUtil.toBean(JSONUtil.toJsonStr(this), typeReference, false);
    }

    /**
     * 转成指定类型
     * 适用于将相同的字段转成为不同类型
     * @param clazz 目标类型
     */
    @SneakyThrows
    public <R> R convert(Class<R> clazz) {
        return JSONUtil.toBean(JSONUtil.toJsonStr(this), clazz, false);
    }

    /**
     * 判断是否成功
     * @return true-成功，false-失败
     */
    public boolean isSuccess() {
        return LccConstants.SUCCESS.equals(this.code);
    }

    /**
     * 判断是否失败
     * @return true-失败，false-成功
     */
    public boolean isFailed() {
        return !isSuccess();
    }

    /**
     * 转换为 Optional
     * @return Optional
     */
    public Optional<T> toOptional() {
        if (isSuccess() && data != null) {
            return Optional.of(data);
        }
        return Optional.empty();
    }

    /**
     * 获取数据，如果失败则返回默认值
     * @param defaultValue 默认值
     * @return 数据或默认值
     */
    public T orElse(T defaultValue) {
        return isSuccess() && data != null ? data : defaultValue;
    }

    /**
     * 获取数据，如果失败则通过 Supplier 提供默认值
     * @param supplier 默认值提供者
     * @return 数据或默认值
     */
    public T orElseGet(Supplier<? extends T> supplier) {
        return isSuccess() && data != null ? data : supplier.get();
    }

    /**
     * 获取数据，如果失败则抛出异常
     * @param exceptionSupplier 异常提供者
     * @return 数据
     * @param <X> 异常类型
     * @throws X 如果结果失败
     */
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (isSuccess() && data != null) {
            return data;
        }
        throw exceptionSupplier.get();
    }

    /**
     * 映射转换数据
     * @param mapper 转换函数
     * @return 转换后的 CResult
     * @param <R> 目标类型
     */
    public <R> CResult<R> map(Function<? super T, ? extends R> mapper) {
        if (isSuccess() && data != null) {
            return CResult.success(mapper.apply(data));
        }
        return CResult.failed(this.message);
    }

    /**
     * 扁平化映射转换
     * @param mapper 转换函数
     * @return 转换后的 CResult
     * @param <R> 目标类型
     */
    public <R> CResult<R> flatMap(Function<? super T, CResult<R>> mapper) {
        if (isSuccess() && data != null) {
            return mapper.apply(data);
        }
        return CResult.failed(this.message);
    }

    /**
     * 过滤数据
     * @param predicate 过滤条件
     * @return 过滤后的 CResult
     */
    public CResult<T> filter(Predicate<? super T> predicate) {
        if (isSuccess() && data != null && predicate.test(data)) {
            return this;
        }
        return CResult.failed("Filter condition not met");
    }

    /**
     * 如果存在数据则执行
     * @param consumer 消费者
     */
    public void ifPresent(Consumer<? super T> consumer) {
        if (isSuccess() && data != null) {
            consumer.accept(data);
        }
    }

    /**
     * 如果存在数据则执行，否则执行另一个操作
     * @param consumer 成功时的消费者
     * @param emptyAction 失败时的操作
     */
    public void ifPresentOrElse(Consumer<? super T> consumer, Runnable emptyAction) {
        if (isSuccess() && data != null) {
            consumer.accept(data);
        } else {
            emptyAction.run();
        }
    }

    /**
     * 处理错误情况
     * @param errorHandler 错误处理器
     * @return 当前 CResult
     */
    public CResult<T> onError(Consumer<String> errorHandler) {
        if (isFailed()) {
            errorHandler.accept(this.message);
        }
        return this;
    }

    /**
     * 记录日志
     * @param logger 日志记录器
     * @return 当前 CResult
     */
    public CResult<T> peek(Consumer<? super T> logger) {
        if (isSuccess() && data != null) {
            logger.accept(data);
        }
        return this;
    }

}












