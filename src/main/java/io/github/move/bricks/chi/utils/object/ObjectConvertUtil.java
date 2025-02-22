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


    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 对象转换为json字符串
     * @param data 数据待转换对象
     * @param propertyNamingStrategy 属性命名策略
     * @param ignoreFields 忽略字段
     * @return 转换后的字符串
     */
    public static String writeWithNamingStrategy(Object data, String propertyNamingStrategy, String... ignoreFields) {
        OBJECT_MAPPER.setPropertyNamingStrategy(ConvertNamingStrategy.of(propertyNamingStrategy));
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
            return OBJECT_MAPPER.writer(filters).writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error("参数转换异常", e);
            throw new RuntimeException(e);
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
                                                            String propertyNamingStrategy) {
        OBJECT_MAPPER.setPropertyNamingStrategy(ConvertNamingStrategy.of(propertyNamingStrategy));
        try {
            return OBJECT_MAPPER.readValue(JSONUtil.toJsonStr(data),
                    OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, tClass));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("result is " + JSONUtil.toJsonStr(data));
        }
    }


    /**
     * 将一个对象转换指定类型的对象
     * @param data 待转换对象
     * @param tClass 目标类型
     * @param propertyNamingStrategy 属性命名策略
     * @return 转换后的对象
     */
    public static <T> T convertWithNamingStrategy(Object data, Class<T> tClass, String propertyNamingStrategy) {
        OBJECT_MAPPER.setPropertyNamingStrategy(ConvertNamingStrategy.of(propertyNamingStrategy));
        //忽略不存在的字段
        try {
            return OBJECT_MAPPER.readValue(JSONUtil.toJsonStr(data), tClass);
        } catch (JsonProcessingException e) {
            log.error("读取数据时转换异常", e);
            throw new RuntimeException(e);
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


}
