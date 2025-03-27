package io.github.movebrickschi.easytool.request.v2.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import io.github.movebrickschi.easytool.core.constants.LccConstants;
import io.github.movebrickschi.easytool.core.utils.object.ObjectConvertUtil;
import io.github.movebrickschi.easytool.request.core.CResult;
import io.github.movebrickschi.easytool.request.v2.AbstractGetResult;
import io.github.movebrickschi.easytool.request.v2.OperationArgsV2;
import io.github.movebrickschi.easytool.request.v2.RequestFormatApi;
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
    public <T> CResult<List<T>> toList(OperationArgsV2 operationArgsV2, Class<T> tClass, String... keys) {
        CResult<?> cResult = switchResult(operationArgsV2);
        if (cResult.getCode().intValue() == LccConstants.FAIL.intValue()) {
            return CResult.failed(cResult.getMessage());
        }
        String simpleName = this.getClass().getSimpleName();
        logRequestStartFormat(operationArgsV2, simpleName);
        //返回为空
        if (Objects.isNull(cResult.getData()) || String.valueOf(cResult.getData()).startsWith("[]")) {
            logEmptyResponse(operationArgsV2);
            return CResult.success(Collections.emptyList());
        }
        String resultByLevelKey = JSONUtil.toJsonStr(cResult.getData());
        //keys为子集的key
        if (ArrayUtil.isNotEmpty(keys)) {
            for (String key : keys) {
                resultByLevelKey = JSONUtil.parseObj(resultByLevelKey).getStr(key);
                if (CharSequenceUtil.isBlank(resultByLevelKey) || resultByLevelKey.startsWith("[]")) {
                    logEmptyResponse(operationArgsV2);
                    return CResult.success(Collections.emptyList());
                }
            }
        }
        //如果需要字段转换
        if (Objects.nonNull(operationArgsV2.getReadConvertConfig())) {
            List<T> result = ObjectConvertUtil.convertListWithNamingStrategy(JSONUtil.toJsonStr(resultByLevelKey),
                    tClass, operationArgsV2.getReadConvertConfig().getNamingStrategy());
            logRequestFormatEnd(simpleName);
            return CResult.success(result);
        }
        List<T> list = JSONUtil.toList(JSONUtil.parseArray(resultByLevelKey), tClass);
        logRequestFormatEnd(simpleName);
        return CResult.success(list);
    }

}
