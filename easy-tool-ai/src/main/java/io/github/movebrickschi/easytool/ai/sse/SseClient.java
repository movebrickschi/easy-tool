package io.github.movebrickschi.easytool.ai.sse;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * sse 客户端
 *
 * @author MoveBricks Chi
 * @version 1.0
 * @since 2.1.5
 */
public interface SseClient {

    RequestBodyUri post();

    RequestBodyUri get();

    RequestBodyUri put();

    interface Uri<S extends RequestHeaders<?>> {
        S uri(String uri);
    }

    interface RequestHeaders<S extends RequestHeaders<S>> {
        S accept(MediaType... acceptableMediaTypes);

        S accept(MediaType acceptableMediaTypes);

        S process(Consumer<ServerSentEvent<String>> consumer);

        S stopBy(BooleanSupplier supplier);

        Flux<ServerSentEvent<String>> execute();
    }


    interface RequestBody extends RequestHeaders<RequestBody> {
        RequestBodyUri contentType(MediaType contentType);

        RequestHeadersUri<?> bodyValue(Object body);

        RequestHeadersUri<?> bodyValue(Object body, boolean toJson);

        RequestHeadersUri<?> bodyValue(Object body, String propertyNamingStrategy, String... ignoreFields);

        RequestHeadersUri<?> endEvent(String endEvent);

    }


    interface Response {
    }

    interface RequestHeadersUri<S extends RequestHeaders<S>> extends Uri<S>, RequestHeaders<S> {

    }

    interface RequestBodyUri extends RequestBody, Response, RequestHeadersUri<RequestBody> {

    }

}
