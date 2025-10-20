package io.github.movebrickschi.easytool.request.core;

import cn.hutool.core.util.ArrayUtil;
import io.github.movebrickschi.easytool.request.v2.OperationArgsV2;

import java.io.Serial;
import java.util.Map;

/**
 * 基于requestUtil
 * 简化操作请求块
 *
 * @author MoveBricks Chi
 */
public class HttpUtil extends RequestUtil {

    @Serial
    private static final long serialVersionUID = -7900462031218152051L;

    private HttpUtil() {

    }

    /**
     * 单例形式,直接返回对应数据类型
     * @param url 请求链接
     * @param body 请求参数，可以是对象或者Map
     * @param tClass 返回类型
     * @return 返回类型
     * @param <T> 参数类型
     */
    public static <T> CResult<T> post(String url, Object body, Class<T> tClass) {
        return parseObj(OperationArgsV2.builder()
                .url(url)
                .param(body)
                .build(), tClass);
    }

    /**
     * 单例形式,直接返回对应数据类型
     * @param url 请求链接
     * @param headers headers
     * @param body 请求参数，可以是对象或者Map
     * @param tClass 返回类型
     * @return 返回类型
     * @param <T> 参数类型
     */
    public static <T> CResult<T> post(String url, Map<String, String> headers, Object body, Class<T> tClass) {
        return parseObj(OperationArgsV2.builder()
                .url(url)
                .param(body)
                .headersMap(headers)
                .build(), tClass);
    }

    /**
     * post请求，返回字符串
     * @param url 请求链接
     * @param body 请求参数，可以是对象或者Map
     * @param headers  headers
     * @return 字符串
     */
    @SafeVarargs
    public static String postToString(String url, Object body, Map<String, String>... headers) {
        OperationArgsV2 args = OperationArgsV2.builder()
                .url(url)
                .param(body)
                .build();
        if (ArrayUtil.isNotEmpty(headers)) {
            args.setHeadersMap(headers[0]);
        }
        return parseObj(args, String.class).getData();
    }

}
