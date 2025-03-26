package io.github.movebrickschi.easytool.request.v2.impl;

import io.github.movebrickschi.easytool.core.constants.LccConstants;
import io.github.movebrickschi.easytool.request.core.CResult;
import io.github.movebrickschi.easytool.request.v2.AbstractGetResult;
import io.github.movebrickschi.easytool.request.v2.OperationArgsV2;
import io.github.movebrickschi.easytool.request.v2.RequestFormatApi;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * 请求没有数据
 * @author MoveBricks Chi
 * @version 1.0
 * @since 3.0.0
 */
@Slf4j
public class RequestFormatNoDataHandler extends AbstractGetResult implements Serializable, RequestFormatApi {
    @Override
    public CResult<?> noData(OperationArgsV2 operationArgs) {
        CResult<?> cResult = getResult(operationArgs);
        if (cResult.getCode().intValue() == LccConstants.FAIL.intValue()) {
            return CResult.failed(cResult.getMessage());
        }
        logRequestFormatEnd(this.getClass().getSimpleName());
        return cResult;
    }
}
