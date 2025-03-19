package io.github.movebrickschi.easytool.request.v2;

import io.github.movebrickschi.easytool.core.constants.NamingStrategyConstants;
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
     * 是否包含null值,默认不包含
     */
    @Builder.Default
    private Boolean isIncludeNull = false;

    public ObjectConvertConfig(String namingStrategy, Boolean isIncludeNull, String... ignoreFields) {
        this.namingStrategy = namingStrategy;
        this.isIncludeNull = isIncludeNull;
        this.ignoreFields = ignoreFields;
    }

    public ObjectConvertConfig(String namingStrategy, String... ignoreFields) {
        this.namingStrategy = namingStrategy;
        this.ignoreFields = ignoreFields;
        this.isIncludeNull = false;
    }

    public ObjectConvertConfig(String namingStrategy) {
        this.namingStrategy = namingStrategy;
        this.isIncludeNull = false;
    }


}
