package io.github.movebrickschi.easytool.request.v2;


import io.github.movebrickschi.easytool.request.core.CResult;
import io.github.movebrickschi.easytool.request.core.ComboResult;
import io.github.movebrickschi.easytool.request.v1.OperationArgs;

/**
 * 获取结果
 *
 * @author MoveBricks Chi
 * @version 1.0
 * @since 3.0.0
 */
public interface GetResult {

    /**
     * 获取结果V1
     * @param operationArgs 参数对象
     * @return 结果对象
     */
    @Deprecated(since = "3.0.0")
    CResult<Object> getResult(OperationArgs operationArgs);

    /**
     * 获取结果V2
     * @param operationArgs 参数对象
     * @return 结果对象
     * @since 3.0.0
     */
    CResult<Object> getResult(OperationArgsV2 operationArgs);

    /**
     * 获取字符串结果
     * @param operationArgs 请求参数
     * @return 结果对象
     * @since 3.0.1
     */
    CResult<ComboResult> getResultString(OperationArgsV2 operationArgs);

    /**
     * 根据returnConfig是否为null,进行不存的处理
     * @param operationArgs 请求参数
     * @return 结果
     * @since 3.0.1
     */
    CResult<?> switchResult(OperationArgsV2 operationArgs);

}
