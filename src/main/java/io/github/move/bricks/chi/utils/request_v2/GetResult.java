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

    CResult<Object> getResult(OperationArgs operationArgs);

    <T> CResult<List<T>> convertListWithNamingStrategy(Object data, Class<T> tClass, String propertyNamingStrategy);


    <T> CResult<T> convertWithNamingStrategy(Object data, Class<T> tClass, String propertyNamingStrategy);

    String writeWithNamingStrategy(Object data, String propertyNamingStrategy);

    <T> CResult<T> convertNumber(Object data, Class<T> tClass);

    <T> boolean isBasicType(Class<T> tClass);

}
