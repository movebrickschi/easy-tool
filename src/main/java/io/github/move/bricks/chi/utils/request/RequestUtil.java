package io.github.move.bricks.chi.utils.request;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.github.move.bricks.chi.utils.request_v2.RequestFormatApi;
import io.github.move.bricks.chi.utils.request_v2.impl.RequestFormatListHandler;
import io.github.move.bricks.chi.utils.request_v2.impl.RequestFormatMapHandler;
import io.github.move.bricks.chi.utils.request_v2.impl.RequestFormatNoDataHandler;
import io.github.move.bricks.chi.utils.request_v2.impl.RequestFormatSingleHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 工具类
 *
 * @author MoveBricks Chi 
 * @version 1.0
 * @since 2.1.0
 */
@Slf4j
public final class RequestUtil implements Serializable {
    private static final RequestFormatApi REQUEST_SINGLE;
    private static final RequestFormatApi REQUEST_LIST;
    private static final RequestFormatApi REQUEST_MAP;
    private static final RequestFormatApi REQUEST_NO_DATA;

    static {
        REQUEST_SINGLE = new RequestFormatSingleHandler();
        REQUEST_LIST = new RequestFormatListHandler();
        REQUEST_MAP = new RequestFormatMapHandler();
        REQUEST_NO_DATA = new RequestFormatNoDataHandler();
    }

    /**
     * 单例形式,直接返回对应数据类型
     * @param operationArgs 请求方法参数
     * @param tClass 返回类型
     * @return 返回类型
     * @param <T> 参数类型
     */
    public static <T> CResult<T> parseObj(OperationArgs operationArgs, Class<T> tClass) {
        return REQUEST_SINGLE.toSingle(operationArgs, tClass);
    }

    /**
     * 单例形式,根据内嵌key获取到最底层数据，直接返回对应数据类型
     * @param operationArgs 请求方法参数
     * @param tClass 返回类型
     * @param keys data下内嵌子集key,由外向内的嵌套key
     * @return 返回类型
     * @param <T> 参数类型
     */
    public static <T> CResult<T> parseObj(OperationArgs operationArgs, Class<T> tClass, String... keys) {
        return REQUEST_SINGLE.toSingle(operationArgs, tClass, keys);
    }

    /**
     * 单例形式,根据key取出data中的一个字段，直接返回对应数据类型
     * @param operationArgs 请求方法参数
     * @param tClass 返回类型
     * @param key data里面的一个key
     * @return 返回类型
     * @param <T> 参数类型
     */
    public static <T> CResult<T> parseObj(OperationArgs operationArgs, Class<T> tClass, String key) {
        return REQUEST_SINGLE.toSingle(operationArgs, tClass, key);
    }

    /**
     * 将data转换成list的场景
     * @param operationArgs 请求方法参数
     * @param tClass 返回类型
     * @param keys 内嵌子集key,由外向内的嵌套key
     * @return 返回类型
     * @param <T> 参数类型
     */
    public static <T> CResult<List<T>> parseArray(OperationArgs operationArgs, Class<T> tClass, String... keys) {
        return REQUEST_LIST.toList(operationArgs, tClass, keys);
    }

    /**
     * 转换为map形式
     * @param operationArgs 请求方法参数
     * @param tClass 返回类型
     * @param siblingKes 同级key
     * @param keys 内嵌子集key,由外向内的嵌套key
     * @return 返回结果
     * @param <T> 请求类型
     */
    public static <T> CResult<Map<String, Object>> parseMap(OperationArgs operationArgs, Class<T> tClass,
                                                            List<String> siblingKes,
                                                            String... keys) {
        return REQUEST_MAP.toMap(operationArgs, tClass, siblingKes, keys);
    }

    /**
     * 获取Data下面的数据，如果穿keys则取对应的key和value存入map，否则取全部data放入map
     * @param operationArgs 请求方法参数
     * @param keys data下对应key的字段，例如：data下有a,b,c三个key，则keys为["a","b","c"]
     * @return 返回结果
     */
    public static CResult<Map<String, Object>> parseMap(OperationArgs operationArgs, String... keys) {
        return REQUEST_MAP.toMap(operationArgs, keys);
    }

    /**
     * 返回结果原始结果转为JSONObject
     * @param operationArgs 请求参数
     * @return 原始数据
     */
    public static JSONObject parseJsonObject(OperationArgs operationArgs) {
        return JSONUtil.parseObj(Operation.ACTION_SUPPLIER.get().get(operationArgs.getMethod()).apply(operationArgs));
    }

    /**
     * data为空的场景
     * @param operationArgs 请求方法参数
     * @return CResult
     */
    public static CResult<?> parse(OperationArgs operationArgs) {
        return REQUEST_NO_DATA.noData(operationArgs);
    }

}
