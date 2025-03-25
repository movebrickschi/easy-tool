package io.github.movebrickschi.easytool.request.v2.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.github.movebrickschi.easytool.core.constants.LccConstants;
import io.github.movebrickschi.easytool.request.core.CResult;
import io.github.movebrickschi.easytool.request.core.ComboResult;
import io.github.movebrickschi.easytool.request.v2.AbstractGetResult;
import io.github.movebrickschi.easytool.request.v2.OperationArgsV2;
import io.github.movebrickschi.easytool.request.v2.RequestFormatApi;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * 返回jsonObject格式
 *
 * @author MoveBricks Chi
 * @version 1.0
 * @since 3.0.1
 */
@Slf4j
public class RequestFormatJsonObjectHandler extends AbstractGetResult implements Serializable, RequestFormatApi {

    @Override
    public CResult<JSONObject> toJSONObject(OperationArgsV2 operationArgsV2) {
        CResult<ComboResult> resultString = getResultString(operationArgsV2);
        if (LccConstants.FAIL.equals(resultString.getCode())) {
            return CResult.failed(resultString.getMessage());
        }
        JSONObject entries = JSONUtil.parseObj(resultString.getData().getResult());
        logRequestEnd(this.getClass().getSimpleName());
        return CResult.success(entries);
    }
}
