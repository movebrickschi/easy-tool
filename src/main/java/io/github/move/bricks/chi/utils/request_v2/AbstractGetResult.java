package io.github.move.bricks.chi.utils.request_v2;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import io.github.move.bricks.chi.utils.request.CResult;
import io.github.move.bricks.chi.utils.request.Operation;
import io.github.move.bricks.chi.utils.request.OperationArgs;
import lombok.extern.slf4j.Slf4j;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Objects;

/**
 * 获取结果抽象类
 *
 * @author MoveBricks Chi
 * @version 1.0
 * @since 2.1.0
 */
@Slf4j
public abstract class AbstractGetResult implements GetResult {
    @Override
    public CResult<Object> getResult(OperationArgs operationArgs) {
        String resultStr = null;
        if (Objects.nonNull(operationArgs.getParam()) && CharSequenceUtil.isNotBlank(operationArgs.getWritePropertyNamingStrategy())) {
            if (ArrayUtil.isNotEmpty(operationArgs.getIgnoreFields())) {
                operationArgs.setBody(writeWithNamingStrategy(operationArgs.getParam(),
                        operationArgs.getWritePropertyNamingStrategy(), operationArgs.getIgnoreFields()));
            } else {
                operationArgs.setBody(writeWithNamingStrategy(operationArgs.getParam(),
                        operationArgs.getWritePropertyNamingStrategy()));
            }
        }

        if (MapUtil.isNotEmpty(operationArgs.getParams())) {
            operationArgs.setBody(JSONUtil.toJsonStr(operationArgs.getParams()));
        }

        try {
            resultStr = Operation.ACTION_SUPPLIER.get().get(operationArgs.getMethod()).apply(operationArgs);
        } catch (Exception e) {
            log.error("request url:{}---------------------error:{}", operationArgs.getUrl(), e.getMessage());
            return CResult.failed(e.getMessage());
        }
        log.info("\n==>request:{}\n==>url:{}\n==>param:{}\n==>return:{}", operationArgs.getMethod(),
                operationArgs.getUrl(), LogFormatUtil.subPre(operationArgs.getBody(),
                        operationArgs.getPrintLength()),
                LogFormatUtil.printSubPre(operationArgs.getIsPrintResultLog(), resultStr,
                        operationArgs.getPrintLength()));
        if (CharSequenceUtil.isBlank(resultStr)) {
            log.info("end----------------request \n==>url:{}\n==>param:{}\n==>return null", operationArgs.getUrl(),
                    LogFormatUtil.subPre(operationArgs.getBody(),
                            operationArgs.getPrintLength()));
            return CResult.failed("request resultStr is null");
        }
        JSONObject jsonObject = JSONUtil.parseObj(resultStr);
        CResult<Object> CResult = new CResult<>();
        //防止出现字符串"null"
        CResult.setData(jsonObject.isNull(operationArgs.getReturnDataField()) ? null :
                jsonObject.get(operationArgs.getReturnDataField()));
        CResult.setCode(jsonObject.getInt(operationArgs.getReturnCodeField()));
        CResult.setMessage(jsonObject.getStr(operationArgs.getReturnMessageField()));
        if (operationArgs.getReturnSuccessCode().intValue() != CResult.getCode().intValue()) {
            log.error("end----------------request \n==>url:{}\n==>param:{}\n==>error:{}", operationArgs.getUrl(),
                    operationArgs.getParams().isEmpty() ? LogFormatUtil.subPre(operationArgs.getBody(),
                            operationArgs.getPrintLength()) :
                            LogFormatUtil.subPre(JSONUtil.toJsonStr(operationArgs.getParams()),
                                    operationArgs.getPrintLength()),
                    CResult.getMessage());
            return CResult.failed(CResult.getMessage());
        }
        CResult.setCode(operationArgs.getBizReturnSuccessCode());
        return CResult;
    }


    public void logRequest(OperationArgs operationArgs, String className) {
        log.info("{} start format----------------\n==>request:{}\n==>url:{}\n==>param:{}", className,
                operationArgs.getMethod(),
                operationArgs.getUrl(),
                Boolean.TRUE.equals(operationArgs.getIsPrintArgsLog()) ?
                        LogFormatUtil.subPre(operationArgs.getBody(),
                                operationArgs.getPrintLength()) : "");
    }

    public boolean isEmptyData(Object data) {
        return Objects.isNull(data) || String.valueOf(data).startsWith("[]");
    }

    public void logEmptyResponse(OperationArgs operationArgs) {
        log.info("end and return empty----------------success\n==>request url:{}\n==>param:{}", operationArgs.getUrl(),
                Boolean.TRUE.equals(operationArgs.getIsPrintArgsLog()) ?
                        LogFormatUtil.subPre(operationArgs.getBody(),
                                operationArgs.getPrintLength()) : "");
    }

    public String getNestedValue(String resultByLevelKey, String... keys) {
        for (String key : keys) {
            resultByLevelKey = JSONUtil.parseObj(resultByLevelKey).getStr(key);
            if (CharSequenceUtil.isBlank(resultByLevelKey) || resultByLevelKey.startsWith("[]")) {
                return resultByLevelKey;
            }
        }
        return resultByLevelKey;
    }

    @Override
    public <T> CResult<List<T>> convertListWithNamingStrategy(Object data, Class<T> tClass,
                                                              String propertyNamingStrategy) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(ConvertNamingStrategy.of(propertyNamingStrategy));
        //忽略不存在的字段
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return CResult.success(objectMapper.readValue(JSONUtil.toJsonStr(data),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, tClass)));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("result is " + JSONUtil.toJsonStr(data));
        }
    }


    @Override
    public <T> CResult<T> convertWithNamingStrategy(Object data, Class<T> tClass,
                                                    String propertyNamingStrategy) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(ConvertNamingStrategy.of(propertyNamingStrategy));
        //忽略不存在的字段
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return CResult.success(objectMapper.readValue(JSONUtil.toJsonStr(data), tClass));
        } catch (JsonProcessingException e) {
            log.error("读取数据时转换异常", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String writeWithNamingStrategy(Object data, String propertyNamingStrategy, String... ignoreFields) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(ConvertNamingStrategy.of(propertyNamingStrategy));
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(ignoreFields);
        JsonFilter jsonFilterAnnotation = data.getClass().getAnnotation(JsonFilter.class);
        if (Objects.isNull(jsonFilterAnnotation) || CharSequenceUtil.isBlank(jsonFilterAnnotation.value())) {
            throw new IllegalArgumentException("请使用@JsonFilter注解标注需要过滤的字段");
        }
        String filterName = jsonFilterAnnotation.value();
        log.info("filterName:{}", filterName);
        FilterProvider filters = new SimpleFilterProvider().addFilter(filterName, filter);
        try {
            return objectMapper.writer(filters).writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error("参数转换异常", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String writeWithNamingStrategy(Object data, String propertyNamingStrategy) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(ConvertNamingStrategy.of(propertyNamingStrategy));
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error("参数转换异常", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> CResult<T> convertNumber(Object data, Class<T> tClass) {
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
        return CResult.failed("Unsupported number type");
    }

    @Override
    public <T> boolean isBasicType(Class<T> tClass) {
        return tClass.isPrimitive() || String.class.equals(tClass) || Number.class.isAssignableFrom(tClass);
    }
}
