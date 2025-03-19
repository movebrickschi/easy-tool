package io.github.movebrickschi.easytool.request.v2.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import io.github.movebrickschi.easytool.core.constants.LccConstants;
import io.github.movebrickschi.easytool.core.utils.object.ObjectConvertUtil;
import io.github.movebrickschi.easytool.request.v2.AbstractGetResult;
import io.github.movebrickschi.easytool.request.core.LogFormatUtil;
import io.github.movebrickschi.easytool.request.v2.OperationArgsV2;
import io.github.movebrickschi.easytool.request.v2.RequestFormatApi;
import io.github.movebrickschi.easytool.request.core.CResult;
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
    public <T> CResult<T> toSingle(OperationArgsV2 operationArgs, Class<T> tClass, String key,
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
        if (ObjectConvertUtil.isBasicType(tClass)) {
            log.info("end success,base type single v1\n==>url:{}\n==>CResult:{}",
                    operationArgs.getUrl(),
                    Boolean.TRUE.equals(operationArgs.getLogConfig().getIsPrintResultLog()) ?
                            LogFormatUtil.subPre(JSONUtil.toJsonStr(cResult.getData()),
                                    operationArgs.getLogConfig().getPrintLength()) : "");
            T result = ObjectConvertUtil.convertBasicType(data, tClass);
            return CResult.success(result);
        }
        //如果需要字段转换
        if (Objects.nonNull(operationArgs.getReadConvertConfig())) {
            return CResult.success(ObjectConvertUtil.convertWithNamingStrategy(data, tClass,
                    operationArgs.getReadConvertConfig().getIsIncludeNull(),
                    operationArgs.getReadConvertConfig().getNamingStrategy()));
        }
        return CResult.success(JSONUtil.toBean(JSONUtil.toJsonStr(data), tClass));
    }
}
