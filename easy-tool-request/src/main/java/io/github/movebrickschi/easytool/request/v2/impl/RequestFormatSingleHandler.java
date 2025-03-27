package io.github.movebrickschi.easytool.request.v2.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import io.github.movebrickschi.easytool.core.constants.LccConstants;
import io.github.movebrickschi.easytool.core.utils.object.ObjectConvertUtil;
import io.github.movebrickschi.easytool.request.core.CResult;
import io.github.movebrickschi.easytool.request.core.LogFormatUtil;
import io.github.movebrickschi.easytool.request.v2.AbstractGetResult;
import io.github.movebrickschi.easytool.request.v2.OperationArgsV2;
import io.github.movebrickschi.easytool.request.v2.RequestFormatApi;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Objects;

/**
 * 请求返回单例类型
 *
 * @author MoveBricks Chi
 * @version 1.0
 * @since 3.0.0
 */
@Slf4j
public class RequestFormatSingleHandler extends AbstractGetResult implements Serializable, RequestFormatApi {
    @Override
    public <T> CResult<T> toSingle(OperationArgsV2 operationArgsV2, Class<T> tClass, String key,
                                   String... keys) {
        CResult<?> cResult = switchResult(operationArgsV2);
        if (cResult.getCode().intValue() == LccConstants.FAIL.intValue()) {
            return CResult.failed(cResult.getMessage());
        }
        String simpleName = this.getClass().getSimpleName();
        logRequestStartFormat(operationArgsV2, simpleName);
        //返回为空
        if (Objects.isNull(cResult.getData())) {
            logEmptyResponse(operationArgsV2);
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
                    logEmptyResponse(operationArgsV2);
                    return CResult.success();
                }
            }
        }


        //基本数据类型或者string
        if (ObjectConvertUtil.isBasicType(tClass)) {
            log.info("end success,base type single\n==>url:{}\n==>CResult:{}",
                    operationArgsV2.getUrl(),
                    Boolean.TRUE.equals(operationArgsV2.getLogConfig().getIsPrintResultLog()) ?
                            LogFormatUtil.subPre(JSONUtil.toJsonStr(cResult.getData()),
                                    operationArgsV2.getLogConfig().getPrintLength()) : "");
            T result = ObjectConvertUtil.convertBasicType(data, tClass);
            logRequestFormatEnd(simpleName);
            return CResult.success(result);
        }
        //如果需要字段转换
        if (Objects.nonNull(operationArgsV2.getReadConvertConfig())) {
            T result = ObjectConvertUtil.convertWithNamingStrategy(data, tClass,
                    operationArgsV2.getReadConvertConfig().getIsIncludeNull(),
                    operationArgsV2.getReadConvertConfig().getNamingStrategy());
            logRequestFormatEnd(simpleName);
            return CResult.success(result);
        }
        T bean = JSONUtil.toBean(JSONUtil.toJsonStr(data), tClass);
        logRequestFormatEnd(simpleName);
        return CResult.success(bean);
    }
}
