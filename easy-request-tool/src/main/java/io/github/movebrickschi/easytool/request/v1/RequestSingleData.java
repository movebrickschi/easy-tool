
package io.github.movebrickschi.easytool.request.v1;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import io.github.movebrickschi.easytool.core.constants.LccConstants;
import io.github.movebrickschi.easytool.request.request_v2.LogFormatUtil;
import lombok.extern.slf4j.Slf4j;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Objects;

/**
 * 请求返回单例类型
 *
 * @author MoveBricks Chi
 * @version 1.0
 */
@Slf4j
public class RequestSingleData implements Operation {
    @Override
    public <T> CResult<T> toSingle(CResult<Object> cResult, OperationArgs operationArgs, Class<T> tClass, String key,
                                   String... keys) {
        if (cResult.getCode().intValue() == LccConstants.FAIL.intValue()) {
            return CResult.failed(cResult.getMessage());
        }

        log.info("start----------------single format v1:{},url:{},param:{}", operationArgs.getMethod(), operationArgs.getUrl(),
                LogFormatUtil.printSubPre(operationArgs.getIsPrintArgsLog(), operationArgs.getParams(),
                        operationArgs.getPrintLength()));
        //返回为空
        if (Objects.isNull(cResult.getData())) {
            log.info("end and return empty----------------success v1 url:{},param:{}", operationArgs.getUrl(),
                    LogFormatUtil.printSubPre(operationArgs.getIsPrintArgsLog(), operationArgs.getParams(),
                            operationArgs.getPrintLength()));
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
                    log.info("end and return empty----------------success post url:{},param:{}", operationArgs.getUrl(),
                            LogFormatUtil.printSubPre(operationArgs.getIsPrintArgsLog(), operationArgs.getParams(),
                                    operationArgs.getPrintLength()));
                    return CResult.success();
                }
            }
        }


        //基本数据类型或者string
        if (tClass.isPrimitive() || String.class.equals(tClass) || Number.class.isAssignableFrom(tClass)) {
            log.info("end----------------success,base type single v1 url:{},param:{},CResult:{}", operationArgs.getUrl(),
                    LogFormatUtil.printSubPre(operationArgs.getIsPrintArgsLog(), operationArgs.getParams(),
                            operationArgs.getPrintLength()),
                    LogFormatUtil.printSubPre(operationArgs.getIsPrintArgsLog(), cResult.getData(),
                            operationArgs.getPrintLength())
            );

            if (String.class.equals(tClass)) {
                // If tClass is String, directly return the data as String
                return CResult.success((T) data.toString());
            }

            if (Number.class.isAssignableFrom(tClass)) {
                // Convert CResult.getData() to the appropriate Number type
                Number number;
                if (data instanceof Number) {
                    number = (Number) data;
                } else if (data instanceof String) {
                    // Try to parse the String to a Number
                    try {
                        number = NumberFormat.getInstance().parse((String) data);
                    } catch (ParseException e) {
                        log.error("Failed to parse String to Number: {}", data, e);
                        return CResult.failed("Failed to parse String to Number");
                    }
                } else {
                    log.error("Unsupported data type: {}", data.getClass().getName());
                    return CResult.failed("Unsupported data type");
                }

                if (tClass.equals(Integer.class)) {
                    return CResult.success(tClass.cast(number.intValue()));
                } else if (tClass.equals(Double.class)) {
                    return CResult.success(tClass.cast(number.doubleValue()));
                } else if (tClass.equals(Long.class)) {
                    return CResult.success(tClass.cast(number.longValue()));
                } else if (tClass.equals(Float.class)) {
                    return CResult.success(tClass.cast(number.floatValue()));
                } else if (tClass.equals(Short.class)) {
                    return CResult.success(tClass.cast(number.shortValue()));
                } else if (tClass.equals(Byte.class)) {
                    return CResult.success(tClass.cast(number.byteValue()));
                }
            }
        }
        return CResult.success(JSONUtil.toBean(JSONUtil.toJsonStr(data), tClass));
    }

}
