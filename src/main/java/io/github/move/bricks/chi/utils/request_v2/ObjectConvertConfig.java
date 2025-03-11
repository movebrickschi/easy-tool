package io.github.move.bricks.chi.utils.request_v2;

import io.github.move.bricks.chi.constants.NamingStrategyConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 对象转换配置
 *
 * @author MoveBricks Chi 
 * @version 1.0
 * @since 2.1.11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ObjectConvertConfig {
    /**
     * 忽略字段，将指定字段排移除
     * 使用此功能，必须在对应实体类上使用@{@link com.fasterxml.jackson.annotation.JsonFilter}
     */
    private String[] ignoreFields;
    /**
     * 命名策略
     * {@link NamingStrategyConstants}
     */
    private String namingStrategy;
    /**
     * 目标类型,用于读取
     */
    private Class<?> tClass;

    public ObjectConvertConfig(String namingStrategy, String... ignoreFields) {
        this.namingStrategy = namingStrategy;
        this.ignoreFields = ignoreFields;
    }

    public ObjectConvertConfig(String namingStrategy, Class<?> tClass) {
        this.namingStrategy = namingStrategy;
        this.tClass = tClass;
    }

    public ObjectConvertConfig(Class<?> tClass) {
        this.tClass = tClass;
    }

}
