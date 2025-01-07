package io.github.move.bricks.chi.utils.request;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import io.github.move.bricks.chi.constants.LccConstants;
import lombok.extern.slf4j.Slf4j;

/**
 * 请求没有数据
 * @author Liu Chunchi
 * @version 1.0
 */
@Slf4j
public class RequestNoData implements Operation {
    @Override
    public CResult noData(CResult<Object> CResult, OperationArgs operationArgs) {
        if (CResult.getCode().intValue() == LccConstants.FAIL.intValue()) {
            return CResult.failed(CResult.getMessage());
        }
        log.info("end----------------success,request url:{},param:{}", operationArgs.getUrl(),
                Boolean.TRUE.equals(operationArgs.getIsPrintArgsLog()) ?
                        CharSequenceUtil.subPre(JSONUtil.toJsonStr(operationArgs.getParams()), operationArgs.getPrintLength()) : "");
        return CResult;
    }


}
