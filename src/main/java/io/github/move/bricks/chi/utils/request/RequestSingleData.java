
package io.github.move.bricks.chi.utils.request;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import io.github.move.bricks.chi.constants.LccConstants;
import lombok.extern.slf4j.Slf4j;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Objects;

/**
 * 请求返回单例类型
 *
 * @author Liu Chunchi
 * @version 1.0
 */
@Slf4j
public class RequestSingleData implements Operation {
    @Override
    public <T> CResult<T> toSingle(CResult<Object> CResult, OperationArgs operationArgs, Class<T> tClass, String... keys) {
//        CResult CResult = Operation.getResult(operationArgs, tClass, null, keys);
        if (CResult.getCode().intValue() == LccConstants.FAIL.intValue()) {
            return CResult.failed(CResult.getMessage());
        }

        log.info("start----------------single format request:{},url:{},param:{}", operationArgs.getMethod(), operationArgs.getUrl(),
                Boolean.TRUE.equals(operationArgs.getIsPrintArgsLog()) ?
                        CharSequenceUtil.subPre(JSONUtil.toJsonStr(operationArgs.getParams()), operationArgs.getPrintLength()) : "");
        //返回为空
        if (Objects.isNull(CResult.getData())) {
            log.info("end and return empty----------------success request url:{},param:{}", operationArgs.getUrl(),
                    JSONUtil.toJsonStr(operationArgs.getParams()));
            return CResult.success();
        }

        String resultByLevelKey = JSONUtil.toJsonStr(CResult.getData());
        //keys为子集的key
        if (!Objects.isNull(keys)) {
            for (String key : keys) {
                resultByLevelKey = JSONUtil.parseObj(resultByLevelKey).getStr(key);
                if (CharSequenceUtil.isBlank(resultByLevelKey) || resultByLevelKey.startsWith("[]")) {
                    log.info("end and return empty----------------success post url:{},param:{}", operationArgs.getUrl(),
                            Boolean.TRUE.equals(operationArgs.getIsPrintArgsLog()) ?
                                    CharSequenceUtil.subPre(JSONUtil.toJsonStr(operationArgs.getParams()), operationArgs.getPrintLength()) : "");
                    return CResult.success();
                }
            }
        }

        //基本数据类型或者string
        if (tClass.isPrimitive() || String.class.equals(tClass) || Number.class.isAssignableFrom(tClass)) {
            log.info("end----------------success,base type single request url:{},param:{},CResult:{}", operationArgs.getUrl(),
                    Boolean.TRUE.equals(operationArgs.getIsPrintResultLog()) ?
                            CharSequenceUtil.subPre(JSONUtil.toJsonStr(CResult.getData()), operationArgs.getPrintLength()) : "");

            if (String.class.equals(tClass)) {
                // If tClass is String, directly return the data as String
                return CResult.success((T) CResult.getData().toString());
            }

            if (Number.class.isAssignableFrom(tClass)) {
                // Convert CResult.getData() to the appropriate Number type
                Object data = CResult.getData();
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
        return CResult.success(JSONUtil.toBean(resultByLevelKey, tClass));
    }
}
