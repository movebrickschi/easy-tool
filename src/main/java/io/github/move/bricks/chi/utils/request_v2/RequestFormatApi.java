package io.github.move.bricks.chi.utils.request_v2;

import io.github.move.bricks.chi.utils.request.CResult;
import io.github.move.bricks.chi.utils.request.OperationArgs;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 数据转换接口
 *
 * @author MoveBricks Chi 
 * @version 1.0
 * @since 2.1.0
 */
public interface RequestFormatApi {

    /**
     * 单例形式,直接返回对应数据类型
     * @param operationArgs 请求方法参数
     * @param tClass 返回类型
     * @return 返回类型
     * @param <T> 参数类型
     */
    default <T> CResult<T> toSingle(OperationArgs operationArgs, Class<T> tClass) {
        return toSingle(operationArgs, tClass, null, ArrayUtils.EMPTY_STRING_ARRAY);
    }

    /**
     * 单例形式,根据内嵌key获取到最底层数据，直接返回对应数据类型
     * @param operationArgs 请求方法参数
     * @param tClass 返回类型
     * @param keys data下内嵌子集key,由外向内的嵌套key
     * @return 返回类型
     * @param <T> 参数类型
     */
    default <T> CResult<T> toSingle(OperationArgs operationArgs, Class<T> tClass,
                                    String... keys) {
        return toSingle(operationArgs, tClass, null, keys);
    }

    /**
     * 单例形式,根据key取出data中的一个字段，直接返回对应数据类型
     * @param operationArgs 请求方法参数
     * @param tClass 返回类型
     * @param key data里面的一个key
     * @return 返回类型
     * @param <T> 参数类型
     */
    default <T> CResult<T> toSingle(OperationArgs operationArgs, Class<T> tClass, String key) {
        return toSingle(operationArgs, tClass, key, ArrayUtils.EMPTY_STRING_ARRAY);
    }

    /**
     * 主方法，key和keys只能选其一
     * @param operationArgs 请求方法参数
     * @param tClass 返回类型
     * @param key data里面的一个key(取出data中的一个字段转换成对应的类型)
     * @param keys data下内嵌子集key,由外向内的嵌套key
     * @return 返回类型
     * @param <T> 参数类型
     */
    default <T> CResult<T> toSingle(OperationArgs operationArgs, Class<T> tClass, String key,
                                    String... keys) {
        return CResult.success();
    }


    /**
     * 将data转换成list的场景
     * @param operationArgs 请求方法参数
     * @param tClass 返回类型
     * @param keys data下内嵌子集key,由外向内的嵌套key
     * @return 返回类型
     * @param <T> 参数类型
     */
    default <T> CResult<List<T>> toList(OperationArgs operationArgs, Class<T> tClass,
                                        String... keys) {
        return CResult.success(Collections.emptyList());
    }


    /**
     * 主方法转换为map形式,如果Class设置为Object，则最后的数据必须是json格式
     * @param operationArgs 请求方法参数
     * @param tClass 返回类型
     * @param siblingKes data下对应同级key
     * @param keys data下内嵌子集key,由外向内的嵌套key
     * @param siblingKesEnd 获取到data下对应同级key后是否结束
     * @return 返回结果
     * @param <T> 请求类型
     */
    default <T> CResult<Map<String, Object>> toMap(OperationArgs operationArgs,
                                                   Class<T> tClass, List<String> siblingKes,
                                                   Boolean siblingKesEnd,
                                                   String... keys) {
        return CResult.success(Collections.emptyMap());
    }

    /**
     * 获取Data下面的数据，如果穿keys则取对应的key和value存入map，否则取全部data放入map
     * @param operationArgs 请求方法参数
     * @param tClass 返回类型
     * @param siblingKes data下对应同级key
     * @param keys data下对应key的字段，例如：data下有a,b,c三个key，则keys为["a","b","c"]
     * @return 返回结果
     */
    default <T> CResult<Map<String, Object>> toMap(OperationArgs operationArgs, Class<T> tClass,
                                                   List<String> siblingKes, String... keys) {
        return toMap(operationArgs, tClass, siblingKes, false, keys);
    }

    /**
     * 获取Data下面的数据，如果穿keys则取对应的key和value存入map，否则取全部data放入map
     * @param operationArgs 请求方法参数
     * @param tClass 返回类型
     * @param keys data下对应key的字段，例如：data下有a,b,c三个key，则keys为["a","b","c"]
     * @return 返回结果
     */
    default <T> CResult<Map<String, Object>> toMap(OperationArgs operationArgs, Class<T> tClass, String... keys) {
        return toMap(operationArgs, tClass, Collections.emptyList(), false, keys);
    }

    /**
     * 获取Data下面的数据，如果穿keys则取对应的key和value存入map，否则取全部data放入map
     * @param operationArgs 请求方法参数
     * @param tClass 返回类型
     * @param siblingKes data下对应同级key
     * @return 返回结果
     */
    default <T> CResult<Map<String, Object>> toMap(OperationArgs operationArgs, Class<T> tClass,
                                                   List<String> siblingKes) {
        return toMap(operationArgs, tClass, siblingKes, false, ArrayUtils.EMPTY_STRING_ARRAY);
    }

    /**
     * 获取Data下面的数据
     * @param operationArgs 请求方法参数
     * @param tClass 返回类型
     * @return 返回结果
     */
    default <T> CResult<Map<String, Object>> toMap(OperationArgs operationArgs, Class<T> tClass) {
        return toMap(operationArgs, tClass, ArrayUtils.EMPTY_STRING_ARRAY);
    }

    /**
     * 获取Data下面的数据，如果穿keys则取对应的key和value存入map，否则取全部data放入map
     * @param operationArgs 请求方法参数
     * @param keys data下对应key的字段，例如：data下有a,b,c三个key，则keys为["a","b","c"]
     * @return 返回结果
     */
    default CResult<Map<String, Object>> toMap(OperationArgs operationArgs, String... keys) {
        return toMap(operationArgs, Object.class, Arrays.asList(keys), true, ArrayUtils.EMPTY_STRING_ARRAY);
    }

    /**
     * data为空的场景
     * @param operationArgs 请求方法参数
     * @return CResult
     */
    default CResult<?> noData(OperationArgs operationArgs) {
        return CResult.success();
    }


}
