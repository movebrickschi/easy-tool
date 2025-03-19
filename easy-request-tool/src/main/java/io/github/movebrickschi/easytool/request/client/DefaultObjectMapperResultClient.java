package io.github.movebrickschi.easytool.request.client;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.Method;
import cn.hutool.json.JSONUtil;
import io.github.movebrickschi.easytool.core.utils.object.ObjectConvertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

/**
 * DefaultObjectMapperResultClient
 *
 * @author Liu Chunchi
 */
public class DefaultObjectMapperResultClient implements ObjectMapperRequestClient {

    private final Logger logger = LoggerFactory.getLogger(DefaultObjectMapperResultClient.class);

    @Override
    public finalRequest delete() {
        return new DefaultRequestBodyUri(Method.DELETE);
    }

    @Override
    public finalRequest post() {
        return new DefaultRequestBodyUri(Method.POST);
    }

    @Override
    public finalRequest get() {
        return new DefaultRequestBodyUri(Method.GET);
    }

    @Override
    public finalRequest put() {
        return new DefaultRequestBodyUri(Method.PUT);
    }

    private class DefaultRequestBodyUri implements finalRequest {

        HttpRequest httpRequest;

        private String url;

        private Method method;

        private Object body;

        private Map<String, Object> formMap;

        private ObjectMapperConvertStrategy objectMapperConvertStrategy;

        private int connectionTimeout = 30 * 1000;

        private int readTimeout = 30 * 1000;

        private String contentType;

        private RequestReturnProperties returnProperties;

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

        private Map<String, List<String>> headers;

        private Map<String, String> headerMap;

        public DefaultRequestBodyUri(Method method) {
            logger.info("\n==>method:{}", method);
            this.method = method;
        }

        @Override
        public RequestBody form(Map<String, Object> formMap) {
            this.formMap = formMap;
            this.httpRequest.form(this.formMap);
            logger.info("\n==>form:{}", JSONUtil.toJsonStr(formMap));
            return this;
        }

        @Override
        public RequestBody bodyValue(Object body, Boolean... toJson) {
            Boolean parse2Json = toJson[0];
            this.body = Boolean.TRUE.equals(parse2Json) ? JSONUtil.toJsonStr(body) : body;
            httpRequest.body(this.body.toString());
            logger.info("\n==>body:{}", this.body);
            return this;
        }

        @Override
        public RequestBody bodyValue(Object body, String propertyNamingStrategy, String... ignoreFields) {
            this.body = ObjectConvertUtil.writeWithNamingStrategy(body, propertyNamingStrategy, ignoreFields);
            httpRequest.body(this.body.toString());
            logger.info("\n==>body:{}", this.body);
            return this;
        }

        @Override
        public RequestBody accept(MediaType acceptableMediaTypes) {
            return this;
        }

        @Override
        public <T> ResponseFormat convertTo(Class<T> tClass, Boolean ignoreUnknownFiled,
                                            String... propertyNamingStrategy) {

            if (ArrayUtil.isNotEmpty(propertyNamingStrategy)) {
                this.objectMapperConvertStrategy = new ObjectMapperConvertStrategy(ignoreUnknownFiled,
                        propertyNamingStrategy[0], tClass);
            } else {
                this.objectMapperConvertStrategy = new ObjectMapperConvertStrategy(ignoreUnknownFiled, tClass);
            }
            return this;
        }

        @Override
        public <T> ResponseFormat convertToList(Class<T> tClass, Boolean ignoreUnknownFiled,
                                                String... propertyNamingStrategy) {
            if (ArrayUtil.isNotEmpty(propertyNamingStrategy)) {
                this.objectMapperConvertStrategy = new ObjectMapperConvertStrategy(ignoreUnknownFiled,
                        propertyNamingStrategy[0], tClass);
            } else {
                this.objectMapperConvertStrategy = new ObjectMapperConvertStrategy(ignoreUnknownFiled, tClass);
            }
            return this;
        }

        @Override
        public Object execute() {
            httpRequest.method(this.method);
            String result = httpRequest.execute().body();
            logger.info("\n==>result:{}", result);
            return this;
        }

        @Override
        public RequestBody uri(String uri) {
            this.url = uri;
            this.httpRequest = HttpRequest.of(uri);
            logger.info("\n==>url:{}", this.url);
            return this;
        }

        @Override
        public RequestBody connectionTimeout(int timeout) {
            this.connectionTimeout = timeout;
            this.httpRequest.setConnectionTimeout(this.connectionTimeout);
            logger.info("\n==>connectionTimeout:{}", this.connectionTimeout);
            return this;
        }

        @Override
        public RequestBody readTimeout(int timeout) {
            this.readTimeout = timeout;
            this.httpRequest.setReadTimeout(this.readTimeout);
            logger.info("\n==>readTimeout:{}", this.readTimeout);
            return this;
        }

        @Override
        public RequestBody contentType(String contentType) {
            this.contentType = contentType;
            this.httpRequest.header(Header.CONTENT_TYPE.getValue(), this.contentType);
            logger.info("\n==>contentType:{}", this.contentType);
            return this;
        }

        @Override
        public RequestBody headers(Map<String, List<String>> headers) {
            this.headers = headers;
            this.httpRequest.header(this.headers);
            logger.info("\n==>headers:{}", this.headers);
            return this;
        }

        @Override
        public RequestBody headersMap(Map<String, String> headerMap) {
            this.headerMap = headerMap;
            this.httpRequest.headerMap(this.headerMap, true);
            logger.info("\n==>headerMap:{}", this.headerMap);
            return this;
        }

        @Override
        public ResponseFormat setReturnProperties(RequestReturnProperties returnProperties) {
            this.returnProperties = returnProperties;
            return this;
        }

        @Override
        public ResponseFormat setPrintProperties(Boolean isPrintResultLog, Boolean isPrintArgsLog, int printLength) {
            this.isPrintArgsLog = isPrintArgsLog;
            this.isPrintResultLog = isPrintResultLog;
            this.printLength = printLength;
            return this;
        }
    }

}
