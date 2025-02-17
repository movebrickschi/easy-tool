package io.github.move.bricks.chi.utils.request_v2.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.move.bricks.chi.constants.LccConstants;
import io.github.move.bricks.chi.utils.request.CResult;
import io.github.move.bricks.chi.utils.request.OperationArgs;
import io.github.move.bricks.chi.utils.request_v2.AbstractGetResult;
import io.github.move.bricks.chi.utils.request_v2.ConvertNamingStrategy;
import io.github.move.bricks.chi.utils.request_v2.RequestFormatApi;
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
 * @since 2.1.0
 */
@Slf4j
public class RequestFormatListHandler extends AbstractGetResult implements Serializable, RequestFormatApi {
    @Override
    public <T> CResult<List<T>> toList(OperationArgs operationArgs, Class<T> tClass, String... keys) {
        CResult<Object> cResult = getResult(operationArgs);
        if (cResult.getCode().intValue() == LccConstants.FAIL.intValue()) {
            return CResult.failed(cResult.getMessage());
        }
        logRequest(operationArgs, this.getClass().getName());
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
        if (CharSequenceUtil.isNotBlank(operationArgs.getReadPropertyNamingStrategy())) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setPropertyNamingStrategy(ConvertNamingStrategy.of(operationArgs.getReadPropertyNamingStrategy()));
            try {
                CResult.success(objectMapper.readValue(JSONUtil.toJsonStr(resultByLevelKey),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, tClass)));
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("result is " + JSONUtil.toJsonStr(resultByLevelKey));
            }
        }
        return CResult.success(JSONUtil.toList(JSONUtil.parseArray(resultByLevelKey), tClass));
    }

}
