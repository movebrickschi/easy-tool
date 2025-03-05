package io.github.move.bricks.chi.utils.request_v3.client;

import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

/**
 * ObjectMapperRequestClient
 *
 * @author Liu Chunchi
 */
public interface ObjectMapperRequestClient {

    finalRequest post();

    finalRequest get();

    finalRequest put();

    finalRequest delete();


    /**
     * url继承header接口
     * @param <S>
     */
    interface Uri<S extends RequestHeaders<?>> {
        S uri(String uri);

        S connectionTimeout(int timeout);

        S readTimeout(int timeout);

    }

    /**
     * header接口
     * @param <S>
     */
    interface RequestHeaders<S extends RequestHeaders<S>> {
        S accept(MediaType acceptableMediaTypes);

        S headers(Map<String, List<String>> headers);

        S headersMap(Map<String, String> headerMap);

        S contentType(String contentType);
    }


    interface RequestHeadersUri<S extends RequestHeaders<S>>
            extends Uri<S>, RequestHeaders<S> {
    }

    /**
     * requestBody接口继承format接口
     */
    interface RequestBody extends ResponseFormat, RequestHeaders<RequestBody> {

        RequestBody form(Map<String, Object> formMap);

        default RequestBody bodyValue(Object body) {
            return bodyValue(body, true);
        }

        RequestBody bodyValue(Object body, Boolean... toJson);

        /**
         * 字段需要转换的时候使用
         * @param body 对象
         * @param propertyNamingStrategy 命名策略
         * @param ignoreFields 忽略字段
         */
        RequestBody bodyValue(Object body, String propertyNamingStrategy, String... ignoreFields);

    }

    interface RequestBodyUriSpec extends RequestBody, RequestHeadersUri<RequestBody> {
    }

    interface ResponseFormat extends RequestExecute {
        default <T> ResponseFormat convertTo(Class<T> tClass, String... propertyNamingStrategy) {
            return convertTo(tClass, true, propertyNamingStrategy);
        }

        <T> ResponseFormat convertTo(Class<T> tClass, Boolean ignoreUnknownFiled, String... propertyNamingStrategy);

        <T> ResponseFormat convertToList(Class<T> tClass, Boolean ignoreUnknownFiled,
                                     String... propertyNamingStrategy);

        ResponseFormat setReturnProperties(RequestReturnProperties returnProperties);

        default ResponseFormat setPrintProperties() {
            return setPrintProperties(-1);
        }

        default ResponseFormat setPrintProperties(int printLength) {
            return setPrintProperties(true, true, printLength);
        }

        ResponseFormat setPrintProperties(Boolean isPrintResultLog, Boolean isPrintArgsLog, int printLength);


    }

    interface RequestExecute<T> {
        T execute();
    }

    interface RequestExecuteFormat extends ResponseFormat, RequestExecute {

    }

    interface finalRequest extends RequestExecuteFormat, RequestBodyUriSpec {

    }

}
