package io.github.movebrickschi.easytool.request.client;

import java.io.Serializable;

/**
 * 转换策略参数
 *
 * @author MoveBricks Chi 
 * @version 1.0
 * @since 1.0
 */
public class ObjectMapperConvertStrategy implements Serializable {

    /**
     * 目标对象类型
     */
    private Class<?> tClass;
    /**
     * 是否忽略未知字段
     */
    private Boolean ignoreUnknownFiled;
    /**
     * 属性命名策略
     */
    private String propertyNamingStrategy;

    public ObjectMapperConvertStrategy(Boolean ignoreUnknownFiled, String propertyNamingStrategy, Class<?> tClass) {
        this.ignoreUnknownFiled = ignoreUnknownFiled;
        this.propertyNamingStrategy = propertyNamingStrategy;
        this.tClass = tClass;
    }

    public ObjectMapperConvertStrategy(Boolean ignoreUnknownFiled, Class<?> tClass) {
        this.ignoreUnknownFiled = ignoreUnknownFiled;
        this.tClass = tClass;
    }


    public Boolean getIgnoreUnknownFiled() {
        return ignoreUnknownFiled;
    }

    public void setIgnoreUnknownFiled(Boolean ignoreUnknownFiled) {
        this.ignoreUnknownFiled = ignoreUnknownFiled;
    }

    public String getPropertyNamingStrategy() {
        return propertyNamingStrategy;
    }

    public void setPropertyNamingStrategy(String propertyNamingStrategy) {
        this.propertyNamingStrategy = propertyNamingStrategy;
    }

    public Class<?> gettClass() {
        return tClass;
    }

    public void settClass(Class<?> tClass) {
        this.tClass = tClass;
    }
}
