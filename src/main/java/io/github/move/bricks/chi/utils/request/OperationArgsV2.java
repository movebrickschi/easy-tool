package io.github.move.bricks.chi.utils.request;

import com.google.common.collect.Maps;
import io.github.move.bricks.chi.constants.LccConstants;
import io.github.move.bricks.chi.utils.request_v2.NamingStrategyConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 请求参数对象
 * @author MoveBricks Chi
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
     * Map类型请求参数
     */
    @Deprecated(since = "2.1.4")
    private Map<String, Object> params = Collections.emptyMap();

    /**
     * json字符串类型请求参数
     */
    @Deprecated(since = "2.1.4")
    private String body;

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
     * 设置状态码默认返回的字段，默认为code
     */
    private String returnCodeField = "code";
    /**
     * 设置返回值默认返回的字段，默认为data
     */
    private String returnDataField = "data";
    /**
     * 设置消息默认返回的字段，默认为message
     */
    private String returnMessageField = "message";

    /**
     * 第三方正确返回的状态码,默认200
     */
    private Integer returnSuccessCode = LccConstants.SuccessEnum.SUCCESS_2_HUNDRED.getCode();

    /**
     * 业务需要返回的状态码
     */
    private Integer bizReturnSuccessCode = LccConstants.SuccessEnum.SUCCESS_ZERO.getCode();

    /**
     * 是否打印返回日志
     * 增加此字段因为如果返回内容过多导致日志文件过大,比如Embedding
     */
    private Boolean isPrintResultLog = true;

    /**
     * 是否打印返回日志
     * 增加此字段因为如果返回内容过多导致日志文件过大,比如Embedding
     */
    private Boolean isPrintArgsLog = true;

    /**
     * 是否打印返回日志
     * 增加此字段因为如果返回内容过多导致日志文件过大,比如Embedding
     * 默认全部打印
     */
    private int printLength = -1;


    /**
     * 多个header,同一个name
     */
    private Map<String, List<String>> headers = Maps.newHashMap();

    /**
     * 多个header,不同的name
     */
    private Map<String, String> headersMap = Maps.newHashMap();

    /**
     * 用于读取数据时，例如将case_id_list字段转换为caseIdList
     * {@link NamingStrategyConstants}
     * @since 2.1.0
     */
    private String readPropertyNamingStrategy = null;

    /**
     * 用于传入参数对象，例如将字段caseIdList转换为case_id_list,
     * {@link NamingStrategyConstants}
     * @since 2.1.0
     */
    private String writePropertyNamingStrategy = null;

    /**
     * 忽略字段，将指定字段排移除
     * 使用此功能，必须在对应实体类上使用@{@link com.fasterxml.jackson.annotation.JsonFilter}
     * @since 2.1.3
     */
    private String[] ignoreFields = null;

    private ObjectConvertConfig readConvertConfig;

    private ObjectConvertConfig writeConvertConfig;


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
         * Map类型请求参数
         */
        @Deprecated(since = "2.1.4")
        private Map<String, Object> params = Collections.emptyMap();

        /**
         * json字符串类型请求参数
         */
        @Deprecated(since = "2.1.4")
        private String body;

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
         * 设置状态码默认返回的字段，默认为code
         */
        private String returnCodeField = "code";
        /**
         * 设置返回值默认返回的字段，默认为data
         */
        private String returnDataField = "data";
        /**
         * 设置消息默认返回的字段，默认为message
         */
        private String returnMessageField = "message";

        /**
         * 第三方正确返回的状态码,默认200
         */
        private Integer returnSuccessCode = LccConstants.SuccessEnum.SUCCESS_2_HUNDRED.getCode();

        /**
         * 业务需要返回的状态码
         */
        private Integer bizReturnSuccessCode = LccConstants.SuccessEnum.SUCCESS_ZERO.getCode();

        /**
         * 是否打印返回日志
         * 增加此字段因为如果返回内容过多导致日志文件过大,比如Embedding
         */
        private Boolean isPrintResultLog = true;

        /**
         * 是否打印返回日志
         * 增加此字段因为如果返回内容过多导致日志文件过大,比如Embedding
         */
        private Boolean isPrintArgsLog = true;

        /**
         * 是否打印返回日志
         * 增加此字段因为如果返回内容过多导致日志文件过大,比如Embedding
         * 默认全部打印
         */
        private int printLength = -1;


        /**
         * 多个header,同一个name
         */
        private Map<String, List<String>> headers = Maps.newHashMap();

        /**
         * 多个header,不同的name
         */
        private Map<String, String> headersMap = Maps.newHashMap();

        /**
         * 用于读取数据时，例如将case_id_list字段转换为caseIdList
         * {@link NamingStrategyConstants}
         * @since 2.1.0
         */
        private String readPropertyNamingStrategy = null;

        /**
         * 用于传入参数对象，例如将字段caseIdList转换为case_id_list,
         * {@link NamingStrategyConstants}
         * @since 2.1.0
         */
        private String writePropertyNamingStrategy = null;

        /**
         * 忽略字段，将指定字段排移除
         * 使用此功能，必须在对应实体类上使用@{@link com.fasterxml.jackson.annotation.JsonFilter}
         * @since 2.1.3
         */
        private String[] ignoreFields = null;

        private ObjectConvertConfig readConvertConfig;

        private ObjectConvertConfig writeConvertConfig;

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

        public Builder params(Map<String, Object> params) {
            this.params = params;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
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

        public Builder returnCodeField(String returnCodeField) {
            this.returnCodeField = returnCodeField;
            return this;
        }

        public Builder returnDataField(String returnDataField) {
            this.returnDataField = returnDataField;
            return this;
        }

        public Builder returnSuccessCode(Integer returnSuccessCode) {
            this.returnSuccessCode = returnSuccessCode;
            return this;
        }

        public Builder bizReturnSuccessCode(Integer bizReturnSuccessCode) {
            this.bizReturnSuccessCode = bizReturnSuccessCode;
            return this;
        }

        public Builder isPrintResultLog(Boolean isPrintResultLog) {
            this.isPrintResultLog = isPrintResultLog;
            return this;
        }

        public Builder isPrintArgsLog(Boolean isPrintArgsLog) {
            this.isPrintArgsLog = isPrintArgsLog;
            return this;
        }

        public Builder printLength(int printLength) {
            this.printLength = printLength;
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

        public Builder readPropertyNamingStrategy(String readPropertyNamingStrategy) {
            this.readPropertyNamingStrategy = readPropertyNamingStrategy;
            return this;
        }

        public Builder writePropertyNamingStrategy(String writePropertyNamingStrategy) {
            this.writePropertyNamingStrategy = writePropertyNamingStrategy;
            return this;
        }

        public Builder ignoreFields(String... ignoreFields) {
            this.ignoreFields = ignoreFields;
            return this;
        }

        public Builder writeConvertConfig(Object object, String namingStrategy, String... ignoreFields) {
            this.writeConvertConfig = new ObjectConvertConfig(object, namingStrategy, ignoreFields);
            return this;
        }

        public Builder writeConvertConfig(Object object) {
            this.writeConvertConfig = new ObjectConvertConfig(object);
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
            operationArgsV2.setParams(params);
            operationArgsV2.setBody(body);
            operationArgsV2.setMethod(method);
            operationArgsV2.setApplication(application);
            operationArgsV2.setReturnCodeField(returnCodeField);
            operationArgsV2.setReturnDataField(returnDataField);
            operationArgsV2.setReturnSuccessCode(returnSuccessCode);
            operationArgsV2.setBizReturnSuccessCode(bizReturnSuccessCode);
            operationArgsV2.setIsPrintResultLog(isPrintResultLog);
            operationArgsV2.setIsPrintArgsLog(isPrintArgsLog);
            operationArgsV2.setPrintLength(printLength);
            operationArgsV2.setHeaders(headers);
            operationArgsV2.setHeadersMap(headersMap);
            operationArgsV2.setReadPropertyNamingStrategy(readPropertyNamingStrategy);
            operationArgsV2.setWritePropertyNamingStrategy(writePropertyNamingStrategy);
            operationArgsV2.setIgnoreFields(ignoreFields);
            operationArgsV2.setWriteConvertConfig(writeConvertConfig);
            operationArgsV2.setReadConvertConfig(readConvertConfig);
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
