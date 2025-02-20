package io.github.move.bricks.chi.utils.request_v2.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import io.github.move.bricks.chi.constants.LccConstants;
import io.github.move.bricks.chi.utils.request.CResult;
import io.github.move.bricks.chi.utils.request.OperationArgs;
import io.github.move.bricks.chi.utils.request_v2.AbstractGetResult;
import io.github.move.bricks.chi.utils.request_v2.LogFormatUtil;
import io.github.move.bricks.chi.utils.request_v2.RequestFormatApi;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Objects;

/**
 * 请求返回单例类型
 *
 * @author MoveBricks Chi
 * @version 1.0
 * @since 2.1.0
 */
@Slf4j
public class RequestFormatSingleHandler extends AbstractGetResult implements Serializable, RequestFormatApi {
    @Override
    public <T> CResult<T> toSingle(OperationArgs operationArgs, Class<T> tClass, String key,
                                   String... keys) {
        CResult<Object> cResult = getResult(operationArgs);
        if (cResult.getCode().intValue() == LccConstants.FAIL.intValue()) {
            return CResult.failed(cResult.getMessage());
        }
        logRequest(operationArgs, this.getClass().getSimpleName());
        //返回为空
        if (Objects.isNull(cResult.getData())) {
            logEmptyResponse(operationArgs);
            return CResult.success();
        }

        Object data = cResult.getData();
        //keys和key两种情况只兼容其中一种
        if (ArrayUtil.isEmpty(keys) && CharSequenceUtil.isNotBlank(key)) {
            keys = new String[]{key};
        }

        //keys为子集的key
        if (ArrayUtil.isNotEmpty(keys)) {
            for (String it : keys) {
                data = JSONUtil.parseObj(cResult.getData()).get(it);
                if (Objects.isNull(data) || data.toString().startsWith("[]")) {
                    logEmptyResponse(operationArgs);
                    return CResult.success();
                }
            }
        }


        //基本数据类型或者string
        if (isBasicType(tClass)) {
            log.info("end----------------success,base type single request url:{},param:{},CResult:{}",
                    operationArgs.getUrl(),
                    Boolean.TRUE.equals(operationArgs.getIsPrintResultLog()) ?
                            LogFormatUtil.subPre(JSONUtil.toJsonStr(cResult.getData()),
                                    operationArgs.getPrintLength()) : "");

            if (String.class.equals(tClass)) {
                // If tClass is String, directly return the data as String
                return CResult.success((T) data.toString());
            }

            if (Number.class.isAssignableFrom(tClass)) {
                // Convert CResult.getData() to the appropriate Number type
                return convertNumber(data, tClass);
            }
        }
        //如果需要字段转换
        if (CharSequenceUtil.isNotBlank(operationArgs.getReadPropertyNamingStrategy())) {
            return convertWithNamingStrategy(data, tClass, operationArgs.getReadPropertyNamingStrategy());
        }
        return CResult.success(JSONUtil.toBean(JSONUtil.toJsonStr(data), tClass));
    }
}
