package io.github.movebrickschi.easytool.request.core;

import cn.hutool.json.JSONObject;
import io.github.movebrickschi.easytool.request.v2.OperationArgsV2;

import java.util.List;
import java.util.Map;

/**
 * 简单HTTP请求工具类,对于复杂的请求，请使用RequestUtil
 * 基于RequestUtil封装，提供简洁的静态方法
 *
 * @author MoveBricks Chi
 */
public final class HttpUtil {

    private HttpUtil() {
    }

    // ================================ POST 请求 ================================

    /**
     * POST请求 - JSON格式
     * @param url 请求地址
     * @param headers 请求头
     * @param body 请求体(JSON字符串)
     * @return 响应内容(JSONObject)
     */
    public static CResult<JSONObject> post(String url, Map<String, String> headers, String body) {
        OperationArgsV2 args = OperationArgsV2.builder()
                .url(url)
                .headersMap(headers)
                .param(body)
                .method(Operation.Method.POST_BODY_HEADERS)
                .build();
        return RequestUtil.parseJsonObject(args);
    }

    /**
     * POST请求 - 返回指定类型
     * @param url 请求地址
     * @param headers 请求头
     * @param body 请求体(JSON字符串)
     * @param tClass 返回类型
     * @return 响应内容
     * @param <T> 返回类型
     */
    public static <T> CResult<T> post(String url, Map<String, String> headers, String body, Class<T> tClass) {
        OperationArgsV2 args = OperationArgsV2.builder()
                .url(url)
                .headersMap(headers)
                .param(body)
                .method(Operation.Method.POST_BODY_HEADERS)
                .build();
        return RequestUtil.parseObj(args, tClass);
    }

    /**
     * POST请求 - 返回List类型
     * @param url 请求地址
     * @param headers 请求头
     * @param body 请求体(JSON字符串)
     * @param tClass 返回类型
     * @return 响应内容
     * @param <T> 返回类型
     */
    public static <T> CResult<List<T>> postForList(String url, Map<String, String> headers, String body,
                                                   Class<T> tClass) {
        OperationArgsV2 args = OperationArgsV2.builder()
                .url(url)
                .headersMap(headers)
                .param(body)
                .method(Operation.Method.POST_BODY_HEADERS)
                .build();
        return RequestUtil.parseArray(args, tClass);
    }

    /**
     * POST请求 - 返回List类型
     * @param url 请求地址
     * @param body 请求体(JSON字符串)
     * @param tClass 返回类型
     * @return 响应内容
     * @param <T> 返回类型
     */
    public static <T> CResult<List<T>> postForList(String url, String body, Class<T> tClass) {
        OperationArgsV2 args = OperationArgsV2.builder()
                .url(url)
                .param(body)
                .method(Operation.Method.POST_BODY_HEADERS)
                .build();
        return RequestUtil.parseArray(args, tClass);
    }

    /**
     * POST请求 - 无请求头
     * @param url 请求地址
     * @param body 请求体(JSON字符串)
     * @return 响应内容(JSONObject)
     */
    public static CResult<JSONObject> post(String url, String body) {
        OperationArgsV2 args = OperationArgsV2.builder()
                .url(url)
                .param(body)
                .method(Operation.Method.POST_BODY)
                .build();
        return RequestUtil.parseJsonObject(args);
    }

    /**
     * POST表单请求
     * @param url 请求地址
     * @param headers 请求头
     * @param formData 表单数据
     * @return 响应内容(JSONObject)
     */
    public static CResult<JSONObject> postForm(String url, Map<String, String> headers, Map<String, Object> formData) {
        OperationArgsV2 args = OperationArgsV2.builder()
                .url(url)
                .headersMap(headers)
                .param(formData)
                .method(Operation.Method.POST_FORM_WITH_HEADERS)
                .build();
        return RequestUtil.parseJsonObject(args);
    }

    // ================================ GET 请求 ================================

    /**
     * GET请求
     * @param url 请求地址
     * @param headers 请求头
     * @return 响应内容(JSONObject)
     */
    public static CResult<JSONObject> get(String url, Map<String, String> headers) {
        OperationArgsV2 args = OperationArgsV2.builder()
                .url(url)
                .headersMap(headers)
                .method(Operation.Method.GET_HEADERS)
                .build();
        return RequestUtil.parseJsonObject(args);
    }

    /**
     * GET请求 - 返回指定类型
     * @param url 请求地址
     * @param headers 请求头
     * @param tClass 返回类型
     * @return 响应内容
     * @param <T> 返回类型
     */
    public static <T> CResult<T> get(String url, Map<String, String> headers, Class<T> tClass) {
        OperationArgsV2 args = OperationArgsV2.builder()
                .url(url)
                .headersMap(headers)
                .method(Operation.Method.GET_HEADERS)
                .build();
        return RequestUtil.parseObj(args, tClass);
    }

    /**
     * GET请求 - 返回指定类型
     * @param url 请求地址
     * @param tClass 返回类型
     * @return 响应内容
     * @param <T> 返回类型
     */
    public static <T> CResult<T> get(String url, Class<T> tClass) {
        OperationArgsV2 args = OperationArgsV2.builder()
                .url(url)
                .method(Operation.Method.GET_HEADERS)
                .build();
        return RequestUtil.parseObj(args, tClass);
    }

    /**
     * GET请求 - 返回List类型
     * @param url 请求地址
     * @param headers 请求头
     * @param tClass 返回类型
     * @return 响应内容
     * @param <T> 返回类型
     */
    public static <T> CResult<List<T>> getForList(String url, Map<String, String> headers, Class<T> tClass) {
        OperationArgsV2 args = OperationArgsV2.builder()
                .url(url)
                .headersMap(headers)
                .method(Operation.Method.GET_HEADERS)
                .build();
        return RequestUtil.parseArray(args, tClass);
    }

    /**
     * GET请求 - 返回List类型
     * @param url 请求地址
     * @param tClass 返回类型
     * @return 响应内容
     * @param <T> 返回类型
     */
    public static <T> CResult<List<T>> getForList(String url, Class<T> tClass) {
        OperationArgsV2 args = OperationArgsV2.builder()
                .url(url)
                .method(Operation.Method.GET_HEADERS)
                .build();
        return RequestUtil.parseArray(args, tClass);
    }

    /**
     * GET请求 - 无请求头
     * @param url 请求地址
     * @return 响应内容(JSONObject)
     */
    public static CResult<JSONObject> get(String url) {
        OperationArgsV2 args = OperationArgsV2.builder()
                .url(url)
                .method(Operation.Method.GET)
                .build();
        return RequestUtil.parseJsonObject(args);
    }

    /**
     * GET请求 - 带参数
     * @param url 请求地址
     * @param headers 请求头
     * @param params 请求参数
     * @return 响应内容(JSONObject)
     */
    public static CResult<JSONObject> get(String url, Map<String, String> headers, Map<String, Object> params) {
        OperationArgsV2 args = OperationArgsV2.builder()
                .url(url)
                .headersMap(headers)
                .param(params)
                .method(Operation.Method.GET_HEADERS)
                .build();
        return RequestUtil.parseJsonObject(args);
    }

    // ================================ PUT 请求 ================================

    /**
     * PUT请求
     * @param url 请求地址
     * @param headers 请求头
     * @param body 请求体(JSON字符串)
     * @return 响应内容(JSONObject)
     */
    public static CResult<JSONObject> put(String url, Map<String, String> headers, String body) {
        OperationArgsV2 args = OperationArgsV2.builder()
                .url(url)
                .headersMap(headers)
                .param(body)
                .method(Operation.Method.PUT_HEADERS)
                .build();
        return RequestUtil.parseJsonObject(args);
    }

    /**
     * PUT请求 - 返回指定类型
     * @param url 请求地址
     * @param headers 请求头
     * @param body 请求体(JSON字符串)
     * @param tClass 返回类型
     * @return 响应内容
     * @param <T> 返回类型
     */
    public static <T> CResult<T> put(String url, Map<String, String> headers, String body, Class<T> tClass) {
        OperationArgsV2 args = OperationArgsV2.builder()
                .url(url)
                .headersMap(headers)
                .param(body)
                .method(Operation.Method.PUT_HEADERS)
                .build();
        return RequestUtil.parseObj(args, tClass);
    }

    /**
     * PUT请求 - 无请求头
     * @param url 请求地址
     * @param body 请求体(JSON字符串)
     * @return 响应内容(JSONObject)
     */
    public static CResult<JSONObject> put(String url, String body) {
        OperationArgsV2 args = OperationArgsV2.builder()
                .url(url)
                .param(body)
                .method(Operation.Method.PUT)
                .build();
        return RequestUtil.parseJsonObject(args);
    }

    // ================================ DELETE 请求 ================================

    /**
     * DELETE请求
     * @param url 请求地址
     * @param headers 请求头
     * @return 响应内容(JSONObject)
     */
    public static CResult<JSONObject> delete(String url, Map<String, String> headers) {
        OperationArgsV2 args = OperationArgsV2.builder()
                .url(url)
                .headersMap(headers)
                .method(Operation.Method.DELETE_HEADERS)
                .build();
        return RequestUtil.parseJsonObject(args);
    }

    /**
     * DELETE请求 - 返回指定类型
     * @param url 请求地址
     * @param headers 请求头
     * @param tClass 返回类型
     * @return 响应内容
     * @param <T> 返回类型
     */
    public static <T> CResult<T> delete(String url, Map<String, String> headers, Class<T> tClass) {
        OperationArgsV2 args = OperationArgsV2.builder()
                .url(url)
                .headersMap(headers)
                .method(Operation.Method.DELETE_HEADERS)
                .build();
        return RequestUtil.parseObj(args, tClass);
    }

    /**
     * DELETE请求 - 无请求头
     * @param url 请求地址
     * @return 响应内容(JSONObject)
     */
    public static CResult<JSONObject> delete(String url) {
        OperationArgsV2 args = OperationArgsV2.builder()
                .url(url)
                .method(Operation.Method.DELETE_NO_PARAM)
                .build();
        return RequestUtil.parseJsonObject(args);
    }

    /**
     * DELETE请求 - 带请求体
     * @param url 请求地址
     * @param headers 请求头
     * @param body 请求体(JSON字符串)
     * @return 响应内容(JSONObject)
     */
    public static CResult<JSONObject> delete(String url, Map<String, String> headers, String body) {
        OperationArgsV2 args = OperationArgsV2.builder()
                .url(url)
                .headersMap(headers)
                .param(body)
                .method(Operation.Method.DELETE_HEADERS)
                .build();
        return RequestUtil.parseJsonObject(args);
    }

}
