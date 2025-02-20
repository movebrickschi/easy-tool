package io.github.move.bricks.chi.utils.request_v2;

import io.github.move.bricks.chi.utils.request.CResult;
import io.github.move.bricks.chi.utils.request.OperationArgs;

import java.util.List;

/**
 * 获取结果
 *
 * @author MoveBricks Chi
 * @version 1.0
 * @since 2.1.0
 */
public interface GetResult {

    /**
     * 获取结果
     * @param operationArgs 参数对象
     * @return 结果对象
     */
    CResult<Object> getResult(OperationArgs operationArgs);

    /**
     * 转成List类型
     * @param data 参数对象
     * @param tClass 对象类型
     * @param propertyNamingStrategy 命名策略
     * @return 结果对象
     */
    <T> CResult<List<T>> convertListWithNamingStrategy(Object data, Class<T> tClass, String propertyNamingStrategy);


    /**
     * 转换成对象类型
     * @param data 参数对象
     * @param tClass 对象类型
     * @param propertyNamingStrategy 命名策略
     * @return 结果对象
     */
    <T> CResult<T> convertWithNamingStrategy(Object data, Class<T> tClass, String propertyNamingStrategy);

    /**
     * 序列化对象
     * @param data 参数对象
     * @param propertyNamingStrategy 命名策略
     * @return json字符串
     */
    String writeWithNamingStrategy(Object data, String propertyNamingStrategy);

    /**
     * 序列化对象
     * @param data 参数对象
     * @param propertyNamingStrategy 命名策略
     * @param ignoreFields 忽略的字段排除显示
     * @return json字符串
     */
    String writeWithNamingStrategy(Object data, String propertyNamingStrategy, String... ignoreFields);

    /**
     * 转换为数字类型
     * @param data 参数对象
     * @param tClass 对象类型
     * @return 结果对象
     */
    <T> CResult<T> convertNumber(Object data, Class<T> tClass);

    /**
     * 判断是否是基本类型
     * @param tClass 对象类型
     * @return true/false
     */
    <T> boolean isBasicType(Class<T> tClass);

}
