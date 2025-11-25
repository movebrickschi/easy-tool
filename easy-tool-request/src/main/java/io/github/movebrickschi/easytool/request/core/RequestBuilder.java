package io.github.movebrickschi.easytool.request.core;

import cn.hutool.json.JSONObject;
import io.github.movebrickschi.easytool.request.v2.OperationArgsV2;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * 流式 API 构建器，提供更加便捷的链式调用方式
 * 增加重试功能和异步处理功能
 *
 * @author MoveBricks Chi
 * @version 2.5.7
 * @since 2.5.7
 */
@Slf4j
public class RequestBuilder<T> {

    /**
     * 请求参数
     */
    private final OperationArgsV2 operationArgs;
    /**
     * 嵌套键路径
     */
    private String[] keys;
    /**
     * 同级键
     */
    private List<String> siblingKeys;
    /**
     * 默认重试次数
     */
    private int retryCount = 0;
    /**
     * 默认重试延迟（毫秒）
     */
    private long retryDelay = 0;

    /**
     * 构造函数
     * @param operationArgs 请求参数
     */
    public RequestBuilder(OperationArgsV2 operationArgs) {
        this.operationArgs = operationArgs;
    }

    /**
     * 设置嵌套键路径
     * @param keys 嵌套的key，由外向内
     * @return 当前构建器
     */
    public RequestBuilder<T> keys(String... keys) {
        this.keys = keys;
        return this;
    }

    /**
     * 设置同级键
     * @param siblingKeys 同级key列表
     * @return 当前构建器
     */
    public RequestBuilder<T> siblingKeys(List<String> siblingKeys) {
        this.siblingKeys = siblingKeys;
        return this;
    }

    /**
     * 设置重试次数
     * @param retryCount 重试次数
     * @return 当前构建器
     */
    public RequestBuilder<T> retry(int retryCount) {
        this.retryCount = retryCount;
        return this;
    }

    /**
     * 设置重试延迟
     * @param retryDelayMillis 重试延迟（毫秒）
     * @return 当前构建器
     */
    public RequestBuilder<T> retryDelay(long retryDelayMillis) {
        this.retryDelay = retryDelayMillis;
        return this;
    }

    /**
     * 解析为单个对象
     * @param clazz 目标类型
     * @return 结果
     * @param <R> 返回类型
     */
    public <R> CResult<R> asObject(Class<R> clazz) {
        if (retryCount > 0) {
            return executeWithRetry(() -> {
                if (keys != null && keys.length > 0) {
                    return RequestUtil.parseObj(operationArgs, clazz, keys);
                }
                return RequestUtil.parseObj(operationArgs, clazz);
            });
        }
        return RequestUtil.parseObj(operationArgs, clazz, keys);
    }

    /**
     * 解析为列表
     * @param clazz 列表元素类型
     * @return 结果
     * @param <R> 返回类型
     */
    public <R> CResult<List<R>> asList(Class<R> clazz) {
        if (retryCount > 0) {
            return executeWithRetry(() ->
                    RequestUtil.parseArray(operationArgs, clazz, keys)
            );
        }
        return RequestUtil.parseArray(operationArgs, clazz, keys);
    }

    /**
     * 解析为 Map
     * @return 结果
     */
    public CResult<Map<String, Object>> asMap() {
        if (retryCount > 0) {
            return executeWithRetry(() ->
                    RequestUtil.parseMap(operationArgs, keys)
            );
        }
        return RequestUtil.parseMap(operationArgs, keys);
    }

    /**
     * 解析为 JSONObject
     * @return 结果
     */
    public CResult<JSONObject> asJsonObject() {
        if (retryCount > 0) {
            return executeWithRetry(() -> RequestUtil.parseJsonObject(operationArgs));
        }
        return RequestUtil.parseJsonObject(operationArgs);
    }

    /**
     * data为空的场景，只验证请求是否成功
     * 适用于 DELETE、UPDATE 等无返回数据的操作
     * @return 结果
     */
    public CResult<?> execute() {
        if (retryCount > 0) {
            return executeWithRetry(() -> RequestUtil.parse(operationArgs));
        }
        return RequestUtil.parse(operationArgs);
    }

    /**
     * 异步解析为单个对象
     * @param clazz 目标类型
     * @return CompletableFuture
     * @param <R> 返回类型
     */
    public <R> CompletableFuture<CResult<R>> asObjectAsync(Class<R> clazz) {
        return CompletableFuture.supplyAsync(() -> asObject(clazz));
    }

    /**
     * 异步解析为列表
     * @param clazz 列表元素类型
     * @return CompletableFuture
     * @param <R> 返回类型
     */
    public <R> CompletableFuture<CResult<List<R>>> asListAsync(Class<R> clazz) {
        return CompletableFuture.supplyAsync(() -> asList(clazz));
    }

    /**
     * 异步执行 data 为空的场景
     * 适用于 DELETE、UPDATE 等无返回数据的异步操作
     * @return CompletableFuture
     */
    public CompletableFuture<CResult<?>> executeAsync() {
        return CompletableFuture.supplyAsync(this::execute);
    }

    /**
     * 带重试的执行
     * @param supplier 执行逻辑
     * @return 结果
     * @param <R> 返回类型
     */
    private <R> CResult<R> executeWithRetry(Supplier<CResult<R>> supplier) {
        int attempts = 0;
        CResult<R> result = null;

        while (attempts <= retryCount) {
            try {
                result = supplier.get();
                if (result != null && result.isSuccess()) {
                    return result;
                }
            } catch (Exception e) {
                log.warn("Request attempt {} failed: {}", attempts + 1, e.getMessage());
            }

            attempts++;
            if (attempts <= retryCount && retryDelay > 0) {
                try {
                    Thread.sleep(retryDelay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("Retry delay interrupted", e);
                    break;
                }
            }
        }

        return result != null ? result : CResult.failed("Max retries exceeded");
    }
}

