package io.github.move.bricks.chi.utils.request;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import io.github.move.bricks.chi.constants.LccConstants;
import io.github.move.bricks.chi.utils.request_v2.LogFormatUtil;
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
        log.info("end----------------success,request url:{},param:{}", operationArgs.getUrl(),
                LogFormatUtil.printSubPre(operationArgs.getIsPrintArgsLog(), operationArgs.getParams(),
                        operationArgs.getPrintLength()));
        return cResult;
    }


}
