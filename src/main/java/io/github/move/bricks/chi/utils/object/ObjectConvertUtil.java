package io.github.move.bricks.chi.utils.object;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import io.github.move.bricks.chi.utils.request_v2.ConvertNamingStrategy;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * 对象转换工具类
 *
 * @author MoveBricks Chi
 * @version 1.0
 * @since 2.1.4
 */
@Slf4j
public final class ObjectConvertUtil implements Serializable {

    private static final ThreadLocal<ObjectMapper> OBJECT_MAPPER_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    });

    /**
     * 对象转换为json字符串
     * @param data 数据待转换对象
     * @param propertyNamingStrategy 属性命名策略
     * @param ignoreFields 忽略字段
     * @return 转换后的字符串
     */
    public static String writeWithNamingStrategy(Object data, String propertyNamingStrategy, String... ignoreFields) {
        ObjectMapper objectMapper = OBJECT_MAPPER_THREAD_LOCAL.get();
        objectMapper.setPropertyNamingStrategy(ConvertNamingStrategy.of(propertyNamingStrategy));
        JsonFilter jsonFilterAnnotation = data.getClass().getAnnotation(JsonFilter.class);
        if (Objects.isNull(jsonFilterAnnotation) || CharSequenceUtil.isBlank(jsonFilterAnnotation.value())) {
            throw new IllegalArgumentException("请使用@JsonFilter注解标注需要过滤的字段");
        }
        String filterName = jsonFilterAnnotation.value();
        log.info("filterName:{}", filterName);

        FilterProvider filters;
        if (ArrayUtil.isNotEmpty(ignoreFields)) {
            SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(ignoreFields);
            filters = new SimpleFilterProvider().addFilter(filterName, filter);
        } else {
            // 创建一个不进行任何过滤的 FilterProvider
            filters = new SimpleFilterProvider().setFailOnUnknownId(false).addFilter(filterName,
                    SimpleBeanPropertyFilter.serializeAll());
        }

        try {
            return objectMapper.writer(filters).writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error("参数转换异常", e);
            throw new RuntimeException(e);
        } finally {
            OBJECT_MAPPER_THREAD_LOCAL.remove();
        }
    }

    /**
     * 将一个对象转换指定类型的对象集合
     * @param data 待转换对象
     * @param tClass 目标类型
     * @param propertyNamingStrategy 属性命名策略
     * @return 转换后的对象集合
     */
    public static <T> List<T> convertListWithNamingStrategy(Object data, Class<T> tClass,
                                                            String... propertyNamingStrategy) {
        ObjectMapper objectMapper = OBJECT_MAPPER_THREAD_LOCAL.get();
        setPropertyNamingStrategy(objectMapper, propertyNamingStrategy);
        try {
            return objectMapper.readValue(JSONUtil.toJsonStr(data),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, tClass));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("result is " + JSONUtil.toJsonStr(data));
        } finally {
            OBJECT_MAPPER_THREAD_LOCAL.remove();
        }
    }

    private static void setPropertyNamingStrategy(ObjectMapper objectMapper, String... propertyNamingStrategy) {
        String defaultPropertyNamingStrategy = null;
        if (ArrayUtil.isNotEmpty(propertyNamingStrategy)) {
            defaultPropertyNamingStrategy = propertyNamingStrategy[0];
            objectMapper.setPropertyNamingStrategy(ConvertNamingStrategy.of(defaultPropertyNamingStrategy));
        }
    }


    /**
     * 将一个对象转换指定类型的对象
     * @param data 待转换对象
     * @param tClass 目标类型
     * @param propertyNamingStrategy 属性命名策略
     * @return 转换后的对象
     */
    public static <T> T convertWithNamingStrategy(Object data, Class<T> tClass, String... propertyNamingStrategy) {
        ObjectMapper objectMapper = OBJECT_MAPPER_THREAD_LOCAL.get();
        setPropertyNamingStrategy(objectMapper, propertyNamingStrategy);
        //忽略不存在的字段
        try {
            return objectMapper.readValue(JSONUtil.toJsonStr(data), tClass);
        } catch (JsonProcessingException e) {
            log.error("读取数据时转换异常", e);
            throw new RuntimeException(e);
        } finally {
            OBJECT_MAPPER_THREAD_LOCAL.remove();
        }
    }

    /**
     * 自定义转换为json字符串
     * @param object 待转换对象
     * @param convertSupplier 转换器
     * @return 转换后的字符串
     */
    public static String customConvertToString(Object object, Function<Object, String> convertSupplier) {
        if (object instanceof String) {
            return (String) object;
        } else if (object instanceof Map<?, ?>) {
            return JSONUtil.toJsonStr(object);
        } else {
            return convertSupplier.apply(object);
        }
    }

    /**
     * 转换为数字类型
     * @param data 待转换数据
     * @param tClass 目标类型
     * @return 转换后的数字类型
     */
    public static <T> T convertNumber(Object data, Class<T> tClass) {
        Number number;
        if (data instanceof Number) {
            number = (Number) data;
        } else if (data instanceof String) {
            // Try to parse the String to a Number
            try {
                number = NumberFormat.getInstance().parse((String) data);
            } catch (ParseException e) {
                log.error("Failed to parse String to Number: {}", data, e);
                return null;
            }
        } else {
            log.error("Unsupported data type: {}", data.getClass().getName());
            return null;
        }

        if (tClass.equals(Integer.class)) {
            return tClass.cast(number.intValue());
        } else if (tClass.equals(Double.class)) {
            return tClass.cast(number.doubleValue());
        } else if (tClass.equals(Long.class)) {
            return tClass.cast(number.longValue());
        } else if (tClass.equals(Float.class)) {
            return tClass.cast(number.floatValue());
        } else if (tClass.equals(Short.class)) {
            return tClass.cast(number.shortValue());
        } else if (tClass.equals(Byte.class)) {
            return tClass.cast(number.byteValue());
        }
        log.error("Unsupported number type");
        return null;
    }

    /**
     * 判断是否是基本类型
     * @param tClass 类型
     * @return 是否是基本类型
     */
    public static <T> boolean isBasicType(Class<T> tClass) {
        return tClass.isPrimitive() || String.class.equals(tClass) || Number.class.isAssignableFrom(tClass);
    }


}
