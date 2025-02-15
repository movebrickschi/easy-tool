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

    CResult<Object> getResult(OperationArgs operationArgs);

}
