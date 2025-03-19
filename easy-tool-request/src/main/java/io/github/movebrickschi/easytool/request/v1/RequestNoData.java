package io.github.movebrickschi.easytool.request.v1;

import io.github.movebrickschi.easytool.core.constants.LccConstants;
import io.github.movebrickschi.easytool.request.core.CResult;
import io.github.movebrickschi.easytool.request.core.Operation;
import io.github.movebrickschi.easytool.request.core.LogFormatUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 请求没有数据
 * @author MoveBricks Chi
 * @version 1.0
 */
@Slf4j
public class RequestNoData implements Operation {
    @Override
    public CResult noData(CResult<Object> cResult, OperationArgs operationArgs) {
        if (cResult.getCode().intValue() == LccConstants.FAIL.intValue()) {
            return CResult.failed(cResult.getMessage());
        }
        log.info("end----------------success,v1 url:{},param:{}", operationArgs.getUrl(),
                LogFormatUtil.printSubPre(operationArgs.getIsPrintArgsLog(), operationArgs.getParams(),
                        operationArgs.getPrintLength()));
        return cResult;
    }


}
