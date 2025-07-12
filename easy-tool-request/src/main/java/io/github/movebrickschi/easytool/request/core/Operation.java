package io.github.movebrickschi.easytool.request.core;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import io.github.movebrickschi.easytool.core.constants.LccConstants;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/***
 * 请求方法
 *
 * @author MoveBricks Chi
 * @version 1.0
 */
public interface Operation {

    String RETURN_TYPE_LIST = "list";
    String RETURN_TYPE_SINGLE = "single";
    String CONTENT_TYPE = "Content-Type";

    enum Method {
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
    enum Application {
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

    Function<RequestParams, String> POST = param -> HttpRequest.post(param.getUrl())
            .header(CONTENT_TYPE, param.getApplication().getContent())
            .body(param.getBody())
            .setConnectionTimeout(param.getConnectionTimeout())
            .setReadTimeout(param.getReadTimeout())
            .execute().body();

    Function<RequestParams, String> POST_BODY_HEADERS = param -> HttpRequest.post(param.getUrl())
            .headerMap(param.getHeadersMap(), true)
            .body(param.getBody())
            .setConnectionTimeout(param.getConnectionTimeout())
            .setReadTimeout(param.getReadTimeout())
            .execute().body();

    Function<RequestParams, String> POST_MULTIPLE_HEADERS = param -> HttpRequest.post(param.getUrl())
            .header(param.getHeaders())
            .body(param.getBody())
            .setConnectionTimeout(param.getConnectionTimeout())
            .setReadTimeout(param.getReadTimeout())
            .execute().body();

    Function<RequestParams, String> POST_MULTIPLE_DIFFERENT_HEADERS = param -> HttpRequest.post(param.getUrl())
            .headerMap(param.getHeadersMap(), true)
            .body(param.getBody())
            .setConnectionTimeout(param.getConnectionTimeout())
            .setReadTimeout(param.getReadTimeout())
            .execute().body();

    Function<RequestParams, String> POST_FORM = param -> HttpRequest.post(param.getUrl())
            .form(param.getMapParams())
            .setConnectionTimeout(param.getConnectionTimeout())
            .setReadTimeout(param.getReadTimeout())
            .execute().body();

    Function<RequestParams, String> POST_FORM_WITH_HEADERS = param -> HttpRequest.post(param.getUrl())
            .headerMap(param.getHeadersMap(), true)
            .form(param.getMapParams())
            .setConnectionTimeout(param.getConnectionTimeout())
            .setReadTimeout(param.getReadTimeout())
            .execute().body();

    Function<RequestParams, String> GET = param -> HttpRequest.get(param.getUrl())
            .form(param.getMapParams())
            .setConnectionTimeout(param.getConnectionTimeout())
            .setReadTimeout(param.getReadTimeout())
            .execute().body();
    Function<RequestParams, String> GET_HEADERS = param -> HttpRequest.get(param.getUrl())
            .headerMap(param.getHeadersMap(), true)
            .form(param.getMapParams())
            .setConnectionTimeout(param.getConnectionTimeout())
            .setReadTimeout(param.getReadTimeout())
            .execute().body();

    Function<RequestParams, String> PUT = param -> HttpRequest.put(param.getUrl())
            .header(CONTENT_TYPE, param.getApplication().getContent())
            .body(param.getBody())
            .setConnectionTimeout(param.getConnectionTimeout())
            .setReadTimeout(param.getReadTimeout())
            .execute().body();
    Function<RequestParams, String> PUT_HEADERS = param -> HttpRequest.put(param.getUrl())
            .headerMap(param.getHeadersMap(), true)
            .body(param.getBody())
            .setConnectionTimeout(param.getConnectionTimeout())
            .setReadTimeout(param.getReadTimeout())
            .execute().body();

    Function<RequestParams, String> DELETE = param -> HttpRequest.delete(param.getUrl())
            .header(CONTENT_TYPE, param.getApplication().getContent())
            .body(param.getBody())
            .setConnectionTimeout(param.getConnectionTimeout())
            .setReadTimeout(param.getReadTimeout())
            .execute().body();

    Function<RequestParams, String> DELETE_HEADERS = param -> HttpRequest.delete(param.getUrl())
            .headerMap(param.getHeadersMap(), true)
            .body(param.getBody())
            .setConnectionTimeout(param.getConnectionTimeout())
            .setReadTimeout(param.getReadTimeout())
            .execute().body();
    Function<RequestParams, String> DELETE_NO_PARAM = param -> HttpRequest.delete(param.getUrl())
            .setConnectionTimeout(param.getConnectionTimeout())
            .setReadTimeout(param.getReadTimeout())
            .execute().body();

    Supplier<Map<Method, Function<RequestParams, String>>> ACTION_SUPPLIER = () -> {
        Map<Method, Function<RequestParams, String>> map = Maps.newHashMap();
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
     * 公共处理请求并返回结果
     * @param operationArgs 请求方法参数
     * @param tClass 返回类型
     * @param siblingKes 同级key
     * @param keys 内嵌子集key,由外向内的嵌套key
     * @return CResult
     * @param <T> 请求类型
     */
    static <T> CResult<T> getResult(RequestParams operationArgs, Class<T> tClass, List<String> siblingKes,
                                    String... keys) {
        String resultStr = null;
        try {
            resultStr = ACTION_SUPPLIER.get().get(operationArgs.getMethod()).apply(operationArgs);
        } catch (Exception e) {
            return CResult.success();
        }
        if (CharSequenceUtil.isBlank(resultStr)) {
            return CResult.failed("v1 resultStr is null");
        }
        CResult cResult = JSONUtil.toBean(resultStr, CResult.class);
        if (LccConstants.SuccessEnum.SUCCESS_2_HUNDRED.getCode() != cResult.getCode()) {
            return CResult.failed(cResult.getMessage());
        }
        return cResult;
    }


}
