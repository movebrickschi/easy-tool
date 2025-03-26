package io.github.movebrickschi.easytool.ai.sse;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import io.github.movebrickschi.easytool.ai.sse.handler.EventHandlerFactory;
import io.github.movebrickschi.easytool.ai.sse.handler.FluxEventData;
import io.github.movebrickschi.easytool.core.utils.object.ObjectConvertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * 默认的SseClient实现
 *
 * @author MoveBricks Chi 
 * @version 1.0
 * @since 3.0.0
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

        private String contentField;

        private Consumer<ServerSentEvent<String>> consumer;

        private WebClient webClient;

        private WebClient.RequestBodyUriSpec post;


        public DefaultRequestBodyUri(HttpMethod httpMethod, WebClient webClient) {
            this.httpMethod = httpMethod;
            this.webClient = webClient;
            // set default post
            this.post = this.webClient.post();
            //set default contentType and acceptType
            this.post.contentType(this.contentType);
            this.post.accept(this.acceptType);
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
            this.post.uri(this.uri);
            return this;
        }

        @Override
        public RequestHandle bodyValue(Object body) {
            this.body = body;
            this.post.bodyValue(this.body);
            return this;
        }

        @Override
        public RequestHandle bodyValue(Object body, boolean toJson) {
            this.body = body;
            if (toJson) {
                this.body = JSONUtil.toJsonStr(body);
            }
            this.post.bodyValue(this.body);
            return this;
        }

        @Override
        public RequestHandle bodyValue(Object body, String propertyNamingStrategy, String... ignoreFields) {
            this.body = ObjectConvertUtil.customConvertToString(body,
                    () -> ObjectConvertUtil.writeWithNamingStrategy(body,
                            propertyNamingStrategy,
                            ignoreFields));
            this.post.bodyValue(this.body);
            return this;
        }

        @Override
        public RequestBodyUri contentField(String contentField) {
            this.contentField = contentField;
            return this;
        }

        @Override
        public RequestBodyUri contentType(MediaType contentType) {
            getHeaders().setContentType(contentType);
            this.contentType = contentType;
            this.post.contentType(this.contentType);
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
            this.post.accept(this.acceptType);
            return this;
        }

        @Override
        public RequestBodyUri endEvent(String endEvent) {
            this.endEvent = endEvent;
            if (CharSequenceUtil.isBlank(endEvent)) {
                this.endEvent = CSseConstants.SseEventType.FINISH.getEvent();
            }
            return this;
        }

        @Override
        public RequestBodyUri process(Consumer<ServerSentEvent<String>> consumer) {
            this.consumer = consumer;
            return this;
        }

        @Override
        public Flux<ServerSentEvent<String>> execute() {
            logger.info("request starting...");
            logger.info("""
                            ==>uri:{}
                            ==>acceptType:{}
                            ==>contentType:{}
                            ==>body:{}""",
                    this.uri, this.acceptType, this.contentType, this.body);
            return EventHandlerFactory.apply(String.valueOf(CharSequenceUtil.isBlank(this.contentField)))
                    .handle(FluxEventData.builder()
                            .consumer(this.consumer)
                            .contentField(this.contentField)
                            .endEvent(this.endEvent)
                            .post(this.post)
                            .build());
        }
    }
}
