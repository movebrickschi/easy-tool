package io.github.move.bricks.chi.utils.request;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import io.github.move.bricks.chi.utils.request_v2.*;
import io.github.move.bricks.chi.utils.request_v2.impl.RequestFormatListHandler;
import io.github.move.bricks.chi.utils.request_v2.impl.RequestFormatMapHandler;
import io.github.move.bricks.chi.utils.request_v2.impl.RequestFormatNoDataHandler;
import io.github.move.bricks.chi.utils.request_v2.impl.RequestFormatSingleHandler;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
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
        return REQUEST_SINGLE.toSingle(format(operationArgs, tClass), tClass);
    }

    private static OperationArgsV2 format(OperationArgs operationArgs, Class<?> tClass) {
        OperationArgsV2 operationArgsV2 = format(operationArgs);
        operationArgsV2.getReadConvertConfig().setTClass(tClass);
        return operationArgsV2;
    }

    private static OperationArgsV2 format(OperationArgs operationArgs) {
        OperationArgsV2 operationArgsV2 = BeanUtil.copyProperties(operationArgs, OperationArgsV2.class);
        List<Object> nonNull = getNonNull(operationArgs.getParam(), operationArgs.getBody(), operationArgs.getParams());
        if (nonNull.size() > 1) {
            throw new IllegalArgumentException("参数只能有一个");
        }
        if(nonNull.size() == 1){
            operationArgsV2.setParam(nonNull.get(0));
        }
        ReturnConfig returnConfig = new ReturnConfig(operationArgs.getReturnCodeField(),
                operationArgs.getReturnDataField(),
                operationArgs.getReturnMessageField(), operationArgs.getReturnSuccessCode(),
                operationArgs.getBizReturnSuccessCode());
        operationArgsV2.setReturnConfig(returnConfig);
        ObjectConvertConfig writeConvertConfig = ObjectConvertConfig.builder()
                .ignoreFields(operationArgs.getIgnoreFields())
                .namingStrategy(operationArgs.getWritePropertyNamingStrategy())
                .build();
        ObjectConvertConfig readConvertConfig = ObjectConvertConfig.builder()
                .namingStrategy(operationArgs.getReadPropertyNamingStrategy())
                .build();

        operationArgsV2.setWriteConvertConfig(writeConvertConfig);
        operationArgsV2.setReadConvertConfig(readConvertConfig);

        LogConfig logConfig = LogConfig.builder()
                .isPrintArgsLog(operationArgs.getIsPrintArgsLog())
                .isPrintResultLog(operationArgs.getIsPrintResultLog())
                .printLength(operationArgs.getPrintLength())
                .build();
        operationArgsV2.setLogConfig(logConfig);

        return operationArgsV2;
    }


    private static List<Object> getNonNull(Object param, String body, Map<String, Object> params) {
        ArrayList<@Nullable Object> objects = Lists.newArrayList();
        if (ObjectUtil.isNotNull(param)) objects.add(param);
        if (CharSequenceUtil.isNotBlank(body)) objects.add(body);
        if (CollUtil.isNotEmpty(params)) objects.add(params);
        return objects;
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
        return REQUEST_SINGLE.toSingle(format(operationArgs, tClass), tClass, keys);
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
        return REQUEST_SINGLE.toSingle(format(operationArgs, tClass), tClass, key);
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
        return REQUEST_LIST.toList(format(operationArgs, tClass), tClass, keys);
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
        return REQUEST_MAP.toMap(format(operationArgs, tClass), tClass, siblingKes, keys);
    }

    /**
     * 获取Data下面的数据，如果传keys则取对应的key和value存入map，否则取全部data放入map
     * @param operationArgs 请求方法参数
     * @param keys data下对应key的字段，例如：data下有a,b,c三个key，则keys为["a","b","c"]
     * @return 返回结果
     */
    public static CResult<Map<String, Object>> parseMap(OperationArgs operationArgs, String... keys) {
        return REQUEST_MAP.toMap(format(operationArgs), keys);
    }

    /**
     * 返回结果原始结果转为JSONObject
     * @param operationArgs 请求参数
     * @return 原始数据
     */
    public static JSONObject parseJsonObject(OperationArgs operationArgs) {
        return JSONUtil.parseObj(Operation.ACTION_SUPPLIER.get()
                .get(operationArgs.getMethod())
                .apply(BeanUtil.copyProperties(operationArgs, RequestParams.class)));
    }

    /**
     * data为空的场景
     * @param operationArgs 请求方法参数
     * @return CResult
     */
    public static CResult<?> parse(OperationArgs operationArgs) {
        return REQUEST_NO_DATA.noData(format(operationArgs));
    }

    /*=========================================================V2==========================================*/

    /**
     * 单例形式,直接返回对应数据类型
     * @param operationArgs 请求方法参数
     * @param tClass 返回类型
     * @return 返回类型
     * @param <T> 参数类型
     */
    public static <T> CResult<T> parseObj(OperationArgsV2 operationArgs, Class<T> tClass) {
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
    public static <T> CResult<T> parseObj(OperationArgsV2 operationArgs, Class<T> tClass, String... keys) {
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
    public static <T> CResult<T> parseObj(OperationArgsV2 operationArgs, Class<T> tClass, String key) {
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
    public static <T> CResult<List<T>> parseArray(OperationArgsV2 operationArgs, Class<T> tClass, String... keys) {
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
    public static <T> CResult<Map<String, Object>> parseMap(OperationArgsV2 operationArgs, Class<T> tClass,
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
    public static CResult<Map<String, Object>> parseMap(OperationArgsV2 operationArgs, String... keys) {
        return REQUEST_MAP.toMap(operationArgs, keys);
    }

    /**
     * 返回结果原始结果转为JSONObject
     * @param operationArgs 请求参数
     * @return 原始数据
     */
    public static JSONObject parseJsonObject(OperationArgsV2 operationArgs) {
        return JSONUtil.parseObj(Operation.ACTION_SUPPLIER.get()
                .get(operationArgs.getMethod())
                .apply(BeanUtil.copyProperties(operationArgs, RequestParams.class)));
    }

    /**
     * data为空的场景
     * @param operationArgs 请求方法参数
     * @return CResult
     */
    public static CResult<?> parse(OperationArgsV2 operationArgs) {
        return REQUEST_NO_DATA.noData(operationArgs);
    }

}
