package io.github.move.bricks.chi.utils.sse;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import io.github.move.bricks.chi.utils.object.ObjectConvertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 默认的SseClient实现
 *
 * @author MoveBricks Chi 
 * @version 1.0
 * @since 2.1.5
 */
public final class DefaultSseClient implements SseClient {

    private final Logger logger = LoggerFactory.getLogger(DefaultSseClient.class);

    private final WebClient webClient;

    public DefaultSseClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public RequestBodyUri get() {
        return new DefaultRequestBodyUri(HttpMethod.GET, webClient);
    }

    @Override
    public RequestBodyUri post() {
        return new DefaultRequestBodyUri(HttpMethod.POST, webClient);
    }

    @Override
    public RequestBodyUri put() {
        return new DefaultRequestBodyUri(HttpMethod.PUT, webClient);
    }

    private class DefaultRequestBodyUri implements RequestBodyUri {
        private final HttpMethod httpMethod;

        private String uri;

        private HttpHeaders headers;

        private Object body;

        private MediaType contentType = MediaType.APPLICATION_JSON;

        private MediaType acceptType = MediaType.TEXT_EVENT_STREAM;

        private String endEvent;

        private Consumer<ServerSentEvent<String>> consumer;

        private WebClient webClient;


        public DefaultRequestBodyUri(HttpMethod httpMethod, WebClient webClient) {
            this.httpMethod = httpMethod;
            this.webClient = webClient;
        }

        private HttpHeaders getHeaders() {
            if (this.headers == null) {
                this.headers = new HttpHeaders();
            }
            return this.headers;
        }

        @Override
        public RequestBodyUri uri(String uri) {
            this.uri = uri;
            return this;
        }

        @Override
        public RequestHeadersUri<?> bodyValue(Object body) {
            this.body = body;
            return this;
        }

        @Override
        public RequestHeadersUri<?> bodyValue(Object body, boolean toJson) {
            this.body = body;
            if (toJson) {
                this.body = JSONUtil.toJsonStr(body);
            }
            return this;
        }

        @Override
        public RequestHeadersUri<?> bodyValue(Object body, String propertyNamingStrategy, String... ignoreFields) {
            this.body = ObjectConvertUtil.customConvertToString(body,
                    obj -> ObjectConvertUtil.writeWithNamingStrategy(obj,
                            propertyNamingStrategy,
                            ignoreFields));
            return this;
        }

        @Override
        public RequestBodyUri contentType(MediaType contentType) {
            getHeaders().setContentType(contentType);
            this.contentType = contentType;
            return this;
        }

        @Override
        public RequestBodyUri accept(MediaType... acceptableMediaTypes) {
            getHeaders().setAccept(Arrays.asList(acceptableMediaTypes));
            return this;
        }

        @Override
        public RequestBodyUri accept(MediaType acceptableMediaTypes) {
            this.acceptType = acceptableMediaTypes;
            return this;
        }

        @Override
        public RequestHeadersUri<?> endEvent(String endEvent) {
            this.endEvent = endEvent;
            if (CharSequenceUtil.isBlank(endEvent)) {
                this.endEvent = CSseConstants.SseEventType.FINISH.getEvent();
            }
            return this;
        }

        @Override
        public RequestHeadersUri<?> process(Consumer<ServerSentEvent<String>> consumer) {
            this.consumer = consumer;
            return this;
        }

        @Override
        public Flux<ServerSentEvent<String>> execute() {
            return webClient.post()
                    .uri(this.uri)
                    .accept(this.acceptType)
                    .contentType(this.contentType)
                    .bodyValue(this.body)
                    .retrieve()
                    .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {
                    })
                    .doOnNext(event -> {
                        logger.info("Received event: data={}, id={}, event={}", event.data(), event.id(),
                                event.event());
                        if (Objects.nonNull(this.consumer)) {
                            this.consumer.accept(event);
                        }
                    })
                    .concatWith(Flux.just(ServerSentEvent.builder(this.endEvent).event(this.endEvent).build()))
                    .onErrorResume(e -> {
                        logger.error("Error in optimizePrompt: {}", e.getMessage(), e);
                        return Flux.error(e);
                    });
        }
    }
}
