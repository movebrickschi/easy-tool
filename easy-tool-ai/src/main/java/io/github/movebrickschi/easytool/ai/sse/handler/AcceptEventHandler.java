package io.github.movebrickschi.easytool.ai.sse.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

import java.util.Objects;

/**
 * AcceptEventHandler
 *
 * @author Liu Chunchi
 */
@Slf4j
public class AcceptEventHandler implements FluxEventHandler {
    @Override
    public Flux<ServerSentEvent<String>> handle(FluxEventData fluxEventData) {
        return fluxEventData.getPost()
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {
                })
                .doOnNext(event -> {
                    log.info("Received event: data={}, id={}, event={}", event.data(), event.id(),
                            event.event());
                    if (Objects.nonNull(fluxEventData.getConsumer())) {
                        fluxEventData.getConsumer().accept(event);
                    }
                })
                .concatWith(Flux.just(ServerSentEvent.builder(fluxEventData.getEndEvent()).event(fluxEventData.getEndEvent()).build()))
                .onErrorResume(e -> {
                    log.error("Error in optimizePrompt: {}", e.getMessage(), e);
                    return Flux.error(e);
                });
    }
}
