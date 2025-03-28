package io.github.movebrickschi.easytool.ai.sse;

import io.github.movebrickschi.easytool.core.utils.object.ObjectConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.Objects;

/**
 * sseUtil工具类
 *
 * @author MoveBricks Chi
 * @version 1.0
 */
@Deprecated(since = "3.0.0", forRemoval = true)
@Slf4j
public final class SseUtil extends AbstractSseHandler {

    private final WebClient webClient;

    public SseUtil(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Flux<ServerSentEvent<String>> getSse(SseArgs sseArgs) {
        String body = null;
        if (Objects.nonNull(sseArgs.getObjectConverter())) {
            body = ObjectConvertUtil.customConvertToString(sseArgs.getObject(),
                    () -> ObjectConvertUtil.writeWithNamingStrategy(sseArgs.getObjectConverter().getObject(),
                            sseArgs.getObjectConverter().getWritePropertyNamingStrategy(),
                            sseArgs.getObjectConverter().getIgnoreFields()));

        }
        log.info("Start to get sse\n==>url:{}\n==>acceptType:{}\n==>contentType:{}\n==>body:{}",
                sseArgs.getUrl(), sseArgs.getAcceptType(), sseArgs.getContentType(), body);
        return webClient.post()
                .uri(sseArgs.getUrl())
                .accept(sseArgs.getAcceptType())
                .contentType(sseArgs.getContentType())
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {
                })
                .doOnNext(event -> {
                    log.info("Received event: data={}, id={}, event={}", event.data(), event.id(), event.event());
                    if (Objects.nonNull(sseArgs.getProcess())) {
                        sseArgs.getProcess().accept(event);
                    }
                })
                .concatWith(Flux.just(ServerSentEvent.builder(sseArgs.getEndFlag()).event(sseArgs.getEndFlag()).build()))
                .onErrorResume(e -> {
                    log.error("Error in optimizePrompt: {}", e.getMessage(), e);
                    return Flux.error(e);
                });
    }

}
