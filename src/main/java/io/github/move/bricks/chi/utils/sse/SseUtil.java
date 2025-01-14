package io.github.move.bricks.chi.utils.sse;

import cn.hutool.core.text.CharSequenceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * sseUtil工具类
 *
 * @author MoveBricks Chi
 * @version 1.0
 */
@Slf4j
@Component
public class SseUtil extends AbstractSseHandler {

    @Override
    public Flux<ServerSentEvent<String>> getSse(SseArgs sseArgs) {
        return webClient.post()
                .uri(sseArgs.getUrl())
                .accept(sseArgs.getAcceptType())
                .contentType(sseArgs.getContentType())
                .bodyValue(CharSequenceUtil.isNotBlank(sseArgs.getBody()) ? sseArgs.getBody() : sseArgs.getParams())
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {
                })
                .doOnNext(event -> log.info("Received event: data={}, id={}, event={}",
                        event.data(), event.id(), event.event()))
                .concatWith(Flux.just(ServerSentEvent.builder(sseArgs.getEndFlag()).event(sseArgs.getEndFlag()).build()))
                .onErrorResume(e -> {
                    log.error("Error in optimizePrompt: {}", e.getMessage(), e);
                    return Flux.error(e);
                });
    }
}
