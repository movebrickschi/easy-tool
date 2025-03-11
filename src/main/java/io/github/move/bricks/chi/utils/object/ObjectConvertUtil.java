package io.github.move.bricks.chi.utils.object;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import io.github.move.bricks.chi.utils.object.serial.CustomModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

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
        mapper.registerModule(new CustomModule());
        //读取时候，是否忽略在json字符串中存在但java对象实际没有的属性
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    });
    @Serial
    private static final long serialVersionUID = -8576822600806112042L;

    /**
     * 对象转换为json字符串
     * @param data 数据待转换对象
     * @param propertyNamingStrategy 属性命名策略
     * @param isIncludeNull 是否包含值为null的字段
     * @param ignoreFields 忽略字段
     * @return 转换后的字符串
     */
    public static String writeWithNamingStrategy(Object data, String propertyNamingStrategy, Boolean isIncludeNull,
                                                 String... ignoreFields) {
        ObjectMapper objectMapper = OBJECT_MAPPER_THREAD_LOCAL.get();
        objectMapper.setPropertyNamingStrategy(ConvertNamingStrategy.of(propertyNamingStrategy));
        configIncludeNullField(objectMapper, isIncludeNull);
        FilterProvider filters = null;
        if (ArrayUtil.isNotEmpty(ignoreFields)) {
            JsonFilter jsonFilterAnnotation = data.getClass().getAnnotation(JsonFilter.class);
            if (Objects.isNull(jsonFilterAnnotation) || CharSequenceUtil.isBlank(jsonFilterAnnotation.value())) {
                throw new IllegalArgumentException("请使用@JsonFilter注解标注需要过滤的字段");
            }
            String filterName = jsonFilterAnnotation.value();
            log.info("filterName:{}", filterName);
            SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(ignoreFields);
            filters = new SimpleFilterProvider().addFilter(filterName, filter);
        }
        try {

            return Objects.nonNull(filters) ? objectMapper.writer(filters).writeValueAsString(data) :
                    objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error("参数转换异常");
            throw new RuntimeException(e);
        } finally {
            OBJECT_MAPPER_THREAD_LOCAL.remove();
        }
    }

    /**
     * 对象转换为json字符串，包含null字段
     * @param data 数据待转换对象
     * @param propertyNamingStrategy 属性命名策略 {@link io.github.move.bricks.chi.constants.NamingStrategyConstants}
     * @param ignoreFields 忽略字段
     * @return 转换后的字符串
     */
    public static String writeWithNamingStrategy(Object data, String propertyNamingStrategy, String... ignoreFields) {
        return writeWithNamingStrategy(data, propertyNamingStrategy, true, ignoreFields);
    }

    /**
     * 将一个对象或者json字符串转换指定类型的对象集合
     * @param object 可以是json或者对象
     * @param tClass 目标类型
     * @param propertyNamingStrategy 属性命名策略
     * @return 转换后的对象集合
     */
    public static <T> List<T> convertListWithNamingStrategy(Object object, Class<T> tClass,
                                                            String... propertyNamingStrategy) {
        ObjectMapper objectMapper = OBJECT_MAPPER_THREAD_LOCAL.get();
        setPropertyNamingStrategy(objectMapper, propertyNamingStrategy);
        try {
            if (object instanceof String json) {
                return objectMapper.readValue(json,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, tClass));
            }
            return objectMapper.convertValue(object,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, tClass));
        } catch (Exception e) {
            log.error("参数转换异常");
            throw new RuntimeException("Error parsing: " + e);
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
        return convertWithNamingStrategy(data, tClass, true, propertyNamingStrategy);
    }


    /**
     * 将一个对象或者json字符串转换指定类型的对象
     * @param data 待转换对象
     * @param tClass 目标类型
     * @param propertyNamingStrategy 属性命名策略
     * @return 转换后的对象
     */
    public static <T> T convertWithNamingStrategy(Object data, Class<T> tClass, Boolean isIncludeNull,
                                                  String... propertyNamingStrategy) {
        ObjectMapper objectMapper = OBJECT_MAPPER_THREAD_LOCAL.get();
        setPropertyNamingStrategy(objectMapper, propertyNamingStrategy);
        configIncludeNullField(objectMapper, isIncludeNull);
        try {
            if (data instanceof String json) {
                return objectMapper.readValue(json, tClass);
            }
            return objectMapper.convertValue(data, tClass);
        } catch (Exception e) {
            log.error("参数转换异常");
            throw new RuntimeException(e);
        } finally {
            OBJECT_MAPPER_THREAD_LOCAL.remove();
        }
    }


    /**
     * 自定义转换为json字符串，如果是String类型直接返回，如果是Map类型则转换为json字符串，否则返回默认值
     * @param object 待转换对象
     * @param convertSupplier 转换器
     * @return 转换后的字符串
     */
    public static String customConvertToString(Object object, Supplier<String> convertSupplier) {
        if (object instanceof String result) {
            return result;
        } else if (object instanceof Map<?, ?>) {
            return JSONUtil.toJsonStr(object);
        } else {
            return convertSupplier.get();
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
        if (data instanceof Number number1) {
            number = number1;
        } else if (data instanceof String string) {
            // Try to parse the String to a Number
            try {
                number = NumberFormat.getInstance().parse(string);
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
        return tClass.isPrimitive() || String.class.equals(tClass) || Number.class.isAssignableFrom(tClass)
                || Character.class.equals(tClass) || Boolean.class.equals(tClass);
    }


    /**
     * 转换为基本类型
     * @param data 待转换数据
     * @param tClass 目标类型
     * @return 转换后的基本类型
     */
    public static <T> T convertBasicType(Object data, Class<T> tClass) {
        if (String.class.equals(tClass)) {
            return (T) data.toString();
        }
        if (Number.class.isAssignableFrom(tClass)) {
            T t = ObjectConvertUtil.convertNumber(data, tClass);
            if (Objects.isNull(t)) {
                throw new IllegalArgumentException("转换失败");
            }
            return t;
        }
        if (Boolean.class.equals(tClass)) {
            return (T) data;
        }
        throw new IllegalArgumentException("不支持的类型: " + tClass.getName());
    }

    /**
     * 将对象转换为query形式
     * @param data 待转换对象
     * @param isIncludeNull 是否包含值为null的字段
     * @param namingStrategy 命名策略
     * @return 转换后的带有?的query字符串
     * @since 2.2.1
     */
    public static String convertToQueryString(Object data, Boolean isIncludeNull, String... namingStrategy) {
        Map<String, Object> converted = null;
        if (ArrayUtil.isEmpty(namingStrategy)) {
            converted = ObjectConvertUtil.convertWithNamingStrategy(data, Map.class, isIncludeNull);
        } else {
            converted = ObjectConvertUtil.convertWithNamingStrategy(data, Map.class, isIncludeNull, namingStrategy);
        }
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        converted.forEach((k, v) -> queryParams.add(k, Objects.nonNull(v) ? v.toString() : ""));
        return "?" + UriComponentsBuilder.newInstance()
                .queryParams(queryParams)
                .build()
                .getQuery();
    }

    /**
     * 将对象转换为query形式,不包含null字段
     * @param data 待转换对象
     * @param namingStrategy 命名策略
     * @return 转换后的带有?的query字符串
     * @since 2.1.11
     */
    public static String convertToQueryString(Object data, String... namingStrategy) {
        return convertToQueryString(data, false, namingStrategy);
    }

    /**
     * 配置是否包含null字段
     * @param objectMapper objectMapper
     * @param isIncludeNull 是否包含null字段
     */
    private static void configIncludeNullField(ObjectMapper objectMapper, Boolean isIncludeNull) {
        if (Boolean.FALSE.equals(isIncludeNull)) {
            //写入序列化时，忽略值为null的属性
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }
    }


}
