package io.github.move.bricks.chi.utils.request;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 请求参数对象
 * @author MoveBricks Chi
 * @version 1.0
 */
@Data
public class OperationArgsV2 {

    /**
     * 请求地址
     */
    private String url;

    /**
     * 请求和响应时间分别是30秒
     */
    private int timeout = 30 * 1000;

    /**
     * 连接超时时间(毫秒),默认30秒
     */
    private int connectionTimeout = 30 * 1000;

    /**
     * 读取超时时间(毫秒),默认30秒
     */
    private int readTimeout = 30 * 1000;

    /**
     * 参数对象(可以是json字符串、Map或者对象实体)
     * 如果自动转换为指定格式，配合writePropertyNamingStrategy使用
     * param高于params和body
     * @since 2.1.0
     */
    private Object param;

    /**
     * 请求方式,默认为post
     */
    private Operation.Method method = Operation.Method.POST_BODY;

    /**
     * 请求header,默认为json
     */
    private Operation.Application application = Operation.Application.JSON;

    /**
     * 多个header,同一个name
     */
    private Map<String, List<String>> headers = Maps.newHashMap();

    /**
     * 多个header,不同的name
     */
    private Map<String, String> headersMap = Maps.newHashMap();

    /**
     * 读取结果转换配置
     */
    private ObjectConvertConfig readConvertConfig;
    /**
     * 写入参数转换配置
     */
    private ObjectConvertConfig writeConvertConfig;

    /**
     * 日志配置
     */
    private LogConfig logConfig;

    /**
     * 返回结果配置
     */
    private ReturnConfig returnConfig;


    public static class Builder {
        /**
         * 请求地址
         */
        private String url;

        /**
         * 请求和响应时间分别是30秒
         */
        private int timeout = 30 * 1000;

        /**
         * 连接超时时间(毫秒),默认30秒
         */
        private int connectionTimeout = 30 * 1000;

        /**
         * 读取超时时间(毫秒),默认30秒
         */
        private int readTimeout = 30 * 1000;


        /**
         * 参数对象(可以是json字符串、Map或者对象实体)
         * 如果自动转换为指定格式，配合writePropertyNamingStrategy使用
         * param高于params和body
         * @since 2.1.0
         */
        private Object param;

        /**
         * 请求方式,默认为post
         */
        private Operation.Method method = Operation.Method.POST_BODY;

        /**
         * 请求header,默认为json
         */
        private Operation.Application application = Operation.Application.JSON;

        /**
         * 多个header,同一个name
         */
        private Map<String, List<String>> headers = Maps.newHashMap();

        /**
         * 多个header,不同的name
         */
        private Map<String, String> headersMap = Maps.newHashMap();


        /**
         * 读取数据格式配置
         * @since 2.1.3
         */
        private ObjectConvertConfig readConvertConfig;

        /**
         * 参数数据格式配置
         * @since 2.1.3
         */
        private ObjectConvertConfig writeConvertConfig;

        private ReturnConfig returnConfig = new ReturnConfig();

        private LogConfig logConfig = new LogConfig();

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder timeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder connectionTimeout(int connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        public Builder readTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public Builder param(Object param) {
            this.param = param;
            return this;
        }

        public Builder method(Operation.Method method) {
            this.method = method;
            return this;
        }

        public Builder application(Operation.Application application) {
            this.application = application;
            return this;
        }

        public Builder returnConfig(ReturnConfig returnConfig) {
            this.returnConfig = returnConfig;
            return this;
        }

        public Builder logConfig(LogConfig logConfig) {
            this.logConfig = logConfig;
            return this;
        }

        public Builder logConfig(int printLength) {
            this.logConfig = new LogConfig(true, true, printLength);
            return this;
        }

        public Builder logConfig(Boolean isPrintArgsLog, Boolean isPrintResultLog, int printLength) {
            this.logConfig = new LogConfig(isPrintArgsLog, isPrintResultLog, printLength);
            return this;
        }

        public Builder headers(Map<String, List<String>> headers) {
            this.headers = headers;
            return this;
        }

        public Builder headersMap(Map<String, String> headersMap) {
            this.headersMap = headersMap;
            return this;
        }

        public Builder writeConvertConfig(String namingStrategy, String... ignoreFields) {
            this.writeConvertConfig = new ObjectConvertConfig(namingStrategy, ignoreFields);
            return this;
        }

        public Builder readConvertConfig(String namingStrategy, Class<?> tClass) {
            this.readConvertConfig = new ObjectConvertConfig(namingStrategy, tClass);
            return this;
        }

        public Builder readConvertConfig(Class<?> tClass) {
            this.readConvertConfig = new ObjectConvertConfig(tClass);
            return this;
        }

        public OperationArgsV2 build() {
            OperationArgsV2 operationArgsV2 = new OperationArgsV2();
            operationArgsV2.setUrl(url);
            operationArgsV2.setParam(param);
            operationArgsV2.setMethod(method);
            operationArgsV2.setApplication(application);
            operationArgsV2.setHeaders(headers);
            operationArgsV2.setHeadersMap(headersMap);
            operationArgsV2.setWriteConvertConfig(writeConvertConfig);
            operationArgsV2.setReadConvertConfig(readConvertConfig);
            operationArgsV2.setLogConfig(logConfig);
            operationArgsV2.setReturnConfig(returnConfig);
            return operationArgsV2;
        }

    }


    public void setUrl(String url) {
        if (!isValidUrl(url)) {
            throw new IllegalArgumentException("Invalid URL format");
        }
        this.url = url;
    }

    private boolean isValidUrl(String url) {
        // 使用正则表达式或其他方式验证URL格式
        return url != null && url.matches("^(https?|ftp)://.*$");
    }

}
