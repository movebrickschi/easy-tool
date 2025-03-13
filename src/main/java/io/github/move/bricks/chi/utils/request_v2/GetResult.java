package io.github.move.bricks.chi.utils.request_v2;

import io.github.move.bricks.chi.utils.request.CResult;
import io.github.move.bricks.chi.utils.request.OperationArgs;

/**
 * 获取结果
 *
 * @author MoveBricks Chi
 * @version 1.0
 * @since 2.1.0
 */
public interface GetResult {

    /**
     * 获取结果V1
     * @param operationArgs 参数对象
     * @return 结果对象
     */
    @Deprecated(since = "2.1.11")
    CResult<Object> getResult(OperationArgs operationArgs);

    /**
     * 获取结果V2
     * @param operationArgs 参数对象
     * @return 结果对象
     * @since 2.1.11
     */
    CResult<Object> getResult(OperationArgsV2 operationArgs);

}
