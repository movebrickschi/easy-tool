package io.github.movebrickschi.easytool.ai.sse;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

/**
 * sse 客户端
 *
 * @author MoveBricks Chi
 * @version 1.0
 * @since 3.0.0
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

        S contentType(MediaType contentType);

        S process(Consumer<ServerSentEvent<String>> consumer);

        Flux<ServerSentEvent<String>> execute();

        RequestHeadersUri<?> endEvent(String endEvent);
    }

    interface RequestBody extends RequestHeaders<RequestBody> {

        RequestHandle bodyValue(Object body);

        RequestHandle bodyValue(Object body, boolean toJson);

        RequestHandle bodyValue(Object body, String propertyNamingStrategy, String... ignoreFields);

    }

    interface RequestHandle extends RequestHeadersUri<RequestBody> {
        RequestHeaders<RequestBody> contentField(String contentField);
    }


    interface Response {

    }

    interface RequestHeadersUri<S extends RequestHeaders<S>> extends Uri<S>, RequestHeaders<S> {

    }

    interface RequestBodyUri extends RequestBody, RequestHandle, Response, RequestHeadersUri<RequestBody> {

    }

}
