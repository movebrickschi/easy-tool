package io.github.movebrickschi.easytool.request.v2;


import io.github.movebrickschi.easytool.request.core.CResult;
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

}
