package io.github.movebrickschi.easytool.request.v2.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import io.github.movebrickschi.easytool.core.constants.LccConstants;
import io.github.movebrickschi.easytool.core.utils.object.ObjectConvertUtil;
import io.github.movebrickschi.easytool.request.v2.AbstractGetResult;
import io.github.movebrickschi.easytool.request.v2.OperationArgsV2;
import io.github.movebrickschi.easytool.request.v2.RequestFormatApi;
import io.github.movebrickschi.easytool.request.core.CResult;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 返回集合类型
 *
 * @author MoveBricks Chi
 * @version 1.0
 * @since 3.0.0
 */
@Slf4j
public class RequestFormatListHandler extends AbstractGetResult implements Serializable, RequestFormatApi {
    @Override
    public <T> CResult<List<T>> toList(OperationArgsV2 operationArgs, Class<T> tClass, String... keys) {
        CResult<Object> cResult = getResult(operationArgs);
        if (cResult.getCode().intValue() == LccConstants.FAIL.intValue()) {
            return CResult.failed(cResult.getMessage());
        }
        logRequest(operationArgs, this.getClass().getSimpleName());
        //返回为空
        if (Objects.isNull(cResult.getData()) || String.valueOf(cResult.getData()).startsWith("[]")) {
            logEmptyResponse(operationArgs);
            return CResult.success(Collections.emptyList());
        }
        String resultByLevelKey = JSONUtil.toJsonStr(cResult.getData());
        //keys为子集的key
        if (ArrayUtil.isNotEmpty(keys)) {
            for (String key : keys) {
                resultByLevelKey = JSONUtil.parseObj(resultByLevelKey).getStr(key);
                if (CharSequenceUtil.isBlank(resultByLevelKey) || resultByLevelKey.startsWith("[]")) {
                    logEmptyResponse(operationArgs);
                    return CResult.success(Collections.emptyList());
                }
            }
        }
        //如果需要字段转换
        if (Objects.nonNull(operationArgs.getReadConvertConfig())) {
            return CResult.success(ObjectConvertUtil.convertListWithNamingStrategy(JSONUtil.toJsonStr(resultByLevelKey), tClass,
                    operationArgs.getReadConvertConfig().getNamingStrategy()));
        }
        return CResult.success(JSONUtil.toList(JSONUtil.parseArray(resultByLevelKey), tClass));
    }

}
