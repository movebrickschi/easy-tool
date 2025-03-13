package io.github.move.bricks.chi.utils.request_v2.impl;

import io.github.move.bricks.chi.constants.LccConstants;
import io.github.move.bricks.chi.utils.request.CResult;
import io.github.move.bricks.chi.utils.request_v2.AbstractGetResult;
import io.github.move.bricks.chi.utils.request_v2.OperationArgsV2;
import io.github.move.bricks.chi.utils.request_v2.RequestFormatApi;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * 请求没有数据
 * @author MoveBricks Chi
 * @version 1.0
 * @since 2.1.0
 */
@Slf4j
public class RequestFormatNoDataHandler extends AbstractGetResult implements Serializable, RequestFormatApi {
    @Override
    public CResult<?> noData(OperationArgsV2 operationArgs) {
        CResult<?> cResult = getResult(operationArgs);
        if (cResult.getCode().intValue() == LccConstants.FAIL.intValue()) {
            return CResult.failed(cResult.getMessage());
        }
        log.info("end success,request\n==>url:{}", operationArgs.getUrl());
        return cResult;
    }
}
