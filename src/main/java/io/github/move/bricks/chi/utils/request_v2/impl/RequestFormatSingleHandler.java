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
import io.github.move.bricks.chi.utils.request_v2.RequestFormatApi;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;
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

        log.info("start----------------single format request:{},url:{},param:{}", operationArgs.getMethod(),
                operationArgs.getUrl(),
                Boolean.TRUE.equals(operationArgs.getIsPrintArgsLog()) ?
                        CharSequenceUtil.subPre(JSONUtil.toJsonStr(operationArgs.getParams()),
                                operationArgs.getPrintLength()) : "");
        //返回为空
        if (Objects.isNull(cResult.getData())) {
            log.info("end and return empty----------------success request url:{},param:{}", operationArgs.getUrl(),
                    JSONUtil.toJsonStr(operationArgs.getParams()));
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
                            Boolean.TRUE.equals(operationArgs.getIsPrintArgsLog()) ?
                                    CharSequenceUtil.subPre(JSONUtil.toJsonStr(operationArgs.getParams()),
                                            operationArgs.getPrintLength()) : "");
                    return CResult.success();
                }
            }
        }


        //基本数据类型或者string
        if (tClass.isPrimitive() || String.class.equals(tClass) || Number.class.isAssignableFrom(tClass)) {
            log.info("end----------------success,base type single request url:{},param:{},CResult:{}",
                    operationArgs.getUrl(),
                    Boolean.TRUE.equals(operationArgs.getIsPrintResultLog()) ?
                            CharSequenceUtil.subPre(JSONUtil.toJsonStr(cResult.getData()),
                                    operationArgs.getPrintLength()) : "");

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
        //如果需要字段转换
        if (Objects.nonNull(operationArgs.getPropertyNamingStrategy())) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setPropertyNamingStrategy(operationArgs.getPropertyNamingStrategy());
            try {
                CResult.success(objectMapper.readValue(JSONUtil.toJsonStr(data), tClass));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return CResult.success(JSONUtil.toBean(JSONUtil.toJsonStr(data), tClass));
    }
}
