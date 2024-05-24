package io.github.liuchunchiuse.utils.request;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import io.github.liuchunchiuse.constants.LccConstants;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/***
 * 请求方法
 *
 * @author Liu Chunchi
 * @version 1.0
 */
public interface Operation {

    String RETURN_TYPE_LIST = "list";
    String RETURN_TYPE_SINGLE = "single";
    String CONTENT_TYPE = "Content-Type";

    public enum Method {
        POST_BODY,
        POST_BODY_HEADERS,
        POST_FORM,
        POST_FORM_WITH_HEADERS,
        POST_MULTIPLE_HEADERS,
        POST_MULTIPLE_DIFFERENT_HEADERS,
        GET,
        GET_HEADERS,
        PUT_HEADERS,
        PUT,
        DELETE,
        DELETE_HEADERS,
        DELETE_NO_PARAM;
    }

    @Getter
    public enum Application {
        JSON("application/json"),
        MULTIPART_FORM_DATA("multipart/form-data"),
        X_WWW_FORM_URLENCODED("application/x-www-form-urlencoded"),
        ATOM_XML("application/atom+xml"),
        TEXT("text/html");

        private final String content;

        Application(String content) {
            this.content = content;
        }

    }

    Function<OperationArgs, String> POST = param -> HttpRequest.post(param.getUrl())
            .header(CONTENT_TYPE, param.getApplication().getContent())
            .body(StringUtils.isNotBlank(param.getBody()) ? param.getBody() : JSONUtil.toJsonStr(param.getParams()))
            .setConnectionTimeout(param.getConnectionTimeout())
            .setReadTimeout(param.getReadTimeout())
            .execute().body();

    Function<OperationArgs, String> POST_BODY_HEADERS = param -> HttpRequest.post(param.getUrl())
            .headerMap(param.getHeadersMap(), true)
            .body(StringUtils.isNotBlank(param.getBody()) ? param.getBody() : JSONUtil.toJsonStr(param.getParams()))
            .setConnectionTimeout(param.getConnectionTimeout())
            .setReadTimeout(param.getReadTimeout())
            .execute().body();

    Function<OperationArgs, String> POST_MULTIPLE_HEADERS = param -> HttpRequest.post(param.getUrl())
            .header(param.getHeaders())
            .body(StringUtils.isNotBlank(param.getBody()) ? param.getBody() : JSONUtil.toJsonStr(param.getParams()))
            .setConnectionTimeout(param.getConnectionTimeout())
            .setReadTimeout(param.getReadTimeout())
            .execute().body();

    Function<OperationArgs, String> POST_MULTIPLE_DIFFERENT_HEADERS = param -> HttpRequest.post(param.getUrl())
            .headerMap(param.getHeadersMap(), true)
            .body(StringUtils.isNotBlank(param.getBody()) ? param.getBody() : JSONUtil.toJsonStr(param.getParams()))
            .setConnectionTimeout(param.getConnectionTimeout())
            .setReadTimeout(param.getReadTimeout())
            .execute().body();

    Function<OperationArgs, String> POST_FORM = param -> HttpRequest.post(param.getUrl())
            .form(param.getParams())
            .setConnectionTimeout(param.getConnectionTimeout())
            .setReadTimeout(param.getReadTimeout())
            .execute().body();

    Function<OperationArgs, String> POST_FORM_WITH_HEADERS = param -> HttpRequest.post(param.getUrl())
            .headerMap(param.getHeadersMap(), true)
            .form(param.getParams())
            .setConnectionTimeout(param.getConnectionTimeout())
            .setReadTimeout(param.getReadTimeout())
            .execute().body();

    Function<OperationArgs, String> GET = param -> HttpRequest.get(param.getUrl())
            .form(param.getParams())
            .setConnectionTimeout(param.getConnectionTimeout())
            .setReadTimeout(param.getReadTimeout())
            .execute().body();
    Function<OperationArgs, String> GET_HEADERS = param -> HttpRequest.get(param.getUrl())
            .headerMap(param.getHeadersMap(), true)
            .form(param.getParams())
            .setConnectionTimeout(param.getConnectionTimeout())
            .setReadTimeout(param.getReadTimeout())
            .execute().body();

    Function<OperationArgs, String> PUT = param -> HttpRequest.put(param.getUrl())
            .header(CONTENT_TYPE, param.getApplication().getContent())
            .body(StringUtils.isNotBlank(param.getBody()) ? param.getBody() : JSONUtil.toJsonStr(param.getParams()))
            .setConnectionTimeout(param.getConnectionTimeout())
            .setReadTimeout(param.getReadTimeout())
            .execute().body();
    Function<OperationArgs, String> PUT_HEADERS = param -> HttpRequest.put(param.getUrl())
            .headerMap(param.getHeadersMap(), true)
            .body(StringUtils.isNotBlank(param.getBody()) ? param.getBody() : JSONUtil.toJsonStr(param.getParams()))
            .setConnectionTimeout(param.getConnectionTimeout())
            .setReadTimeout(param.getReadTimeout())
            .execute().body();

    Function<OperationArgs, String> DELETE = param -> HttpRequest.delete(param.getUrl())
            .header(CONTENT_TYPE, param.getApplication().getContent())
            .body(StringUtils.isNotBlank(param.getBody()) ? param.getBody() : JSONUtil.toJsonStr(param.getParams()))
            .setConnectionTimeout(param.getConnectionTimeout())
            .setReadTimeout(param.getReadTimeout())
            .execute().body();

    Function<OperationArgs, String> DELETE_HEADERS = param -> HttpRequest.delete(param.getUrl())
            .headerMap(param.getHeadersMap(), true)
            .body(StringUtils.isNotBlank(param.getBody()) ? param.getBody() : JSONUtil.toJsonStr(param.getParams()))
            .setConnectionTimeout(param.getConnectionTimeout())
            .setReadTimeout(param.getReadTimeout())
            .execute().body();
    Function<OperationArgs, String> DELETE_NO_PARAM = param -> HttpRequest.delete(param.getUrl())
            .setConnectionTimeout(param.getConnectionTimeout())
            .setReadTimeout(param.getReadTimeout())
            .execute().body();


    Supplier<Map<Method, Function<OperationArgs, String>>> ACTION_SUPPLIER = () -> {
        Map<Method, Function<OperationArgs, String>> map = Maps.newHashMap();
        map.put(Method.POST_BODY, POST);
        map.put(Method.POST_BODY_HEADERS, POST_BODY_HEADERS);
        map.put(Method.POST_FORM, POST_FORM);
        map.put(Method.POST_FORM_WITH_HEADERS, POST_FORM_WITH_HEADERS);
        map.put(Method.POST_MULTIPLE_HEADERS, POST_MULTIPLE_HEADERS);
        map.put(Method.POST_MULTIPLE_DIFFERENT_HEADERS, POST_MULTIPLE_DIFFERENT_HEADERS);
        map.put(Method.GET, GET);
        map.put(Method.GET_HEADERS, GET_HEADERS);
        map.put(Method.PUT, PUT);
        map.put(Method.PUT_HEADERS, PUT_HEADERS);
        map.put(Method.DELETE, DELETE);
        map.put(Method.DELETE_HEADERS, DELETE_HEADERS);
        map.put(Method.DELETE_NO_PARAM, DELETE_NO_PARAM);
        return map;
    };

    /**
     * 单例形式
     * @param result 请求结果
     * @param operationArgs 请求方法参数
     * @param tClass 返回类型
     * @param keys 内嵌子集key
     * @return 返回类型
     * @param <T> 参数类型
     */
    default <T> Result<T> toSingle(Result<Object> result, OperationArgs operationArgs, Class<T> tClass, String... keys) {
        return null;
    }

    /**
     * data为空
     * @param result 请求结果
     * @param operationArgs 请求方法参数
     * @return Result
     */
    default Result noData(Result<Object> result, OperationArgs operationArgs) {
        return Result.success();
    }

    /**
     * 集合形式
     * @param result 请求结果
     * @param operationArgs 请求方法参数
     * @param tClass 返回类型
     * @param keys 内嵌子集key
     * @return 返回类型
     * @param <T> 参数类型
     */
    default <T> Result<List<T>> toList(Result<Object> result, OperationArgs operationArgs, Class<T> tClass, String... keys) {
        return Result.success(Collections.emptyList());
    }

    /**
     * map形式
     * @param result 请求结果
     * @param operationArgs 请求方法参数
     * @param tClass 返回类型
     * @param siblingKes 同级key
     * @param keys 内嵌子集key
     * @return 返回结果
     * @param <T> 请求类型
     */
    default <T> Result<Map<String, Object>> toMap(Result<Object> result, OperationArgs operationArgs, Class<T> tClass, List<String> siblingKes,
                                                  String... keys) {
        return Result.success(Collections.emptyMap());
    }


    /**
     * 公共处理请求并返回结果
     * @param operationArgs 请求方法参数
     * @param tClass 返回类型
     * @param siblingKes 同级key
     * @param keys 内嵌子集key
     * @return Result
     * @param <T> 请求类型
     */
    static <T> Result<T> getResult(OperationArgs operationArgs, Class<T> tClass, List<String> siblingKes, String... keys) {
        String resultStr = null;
        try {
            resultStr = ACTION_SUPPLIER.get().get(operationArgs.getMethod()).apply(operationArgs);
        } catch (Exception e) {
            return Result.success();
        }
        if (CharSequenceUtil.isBlank(resultStr)) {
            return Result.failed("request resultStr is null");
        }
        Result result = JSONUtil.toBean(resultStr, Result.class);
        if (LccConstants.SuccessEnum.SUCCESS_2_HUNDRED.getCode() != result.getCode()) {
            return Result.failed(result.getMessage());
        }
        return result;
    }


}
