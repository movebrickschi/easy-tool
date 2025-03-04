package io.github.move.bricks.chi.utils.request;

public class ObjectConvertConfig {
    /**
     * 忽略字段，将指定字段排移除
     * 使用此功能，必须在对应实体类上使用@{@link com.fasterxml.jackson.annotation.JsonFilter}
     * @since 2.1.3
     */
    private String[] ignoreFields;
    /**
     * 命名策略
     * {@link io.github.move.bricks.chi.utils.request_v2.NamingStrategyConstants}
     * @since 2.1.0
     */
    private String namingStrategy;
    /**
     * 目标类型
     * @since 2.1.0
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

    public String[] getIgnoreFields() {
        return ignoreFields;
    }

    public void setIgnoreFields(String[] ignoreFields) {
        this.ignoreFields = ignoreFields;
    }

    public String getNamingStrategy() {
        return namingStrategy;
    }

    public void setNamingStrategy(String namingStrategy) {
        this.namingStrategy = namingStrategy;
    }

    public Class<?> gettClass() {
        return tClass;
    }

    public void settClass(Class<?> tClass) {
        this.tClass = tClass;
    }
}
