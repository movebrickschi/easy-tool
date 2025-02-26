package io.github.move.bricks.chi.utils.request;

public class ObjectConvertConfig {
    private Object object;
    private String[] ignoreFields;
    private String namingStrategy;
    private Class<?> tClass;

    public ObjectConvertConfig(Object object, String namingStrategy, String... ignoreFields) {
        this.object = object;
        this.namingStrategy = namingStrategy;
        this.ignoreFields = ignoreFields;
    }

    public ObjectConvertConfig(Object object) {
        this.object = object;
    }

    public ObjectConvertConfig(Object object, String namingStrategy, Class<?> tClass) {
        this.object = object;
        this.namingStrategy = namingStrategy;
        this.tClass = tClass;
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

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Class<?> gettClass() {
        return tClass;
    }

    public void settClass(Class<?> tClass) {
        this.tClass = tClass;
    }
}
