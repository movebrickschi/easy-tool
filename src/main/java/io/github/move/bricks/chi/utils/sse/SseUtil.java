package io.github.move.bricks.chi.utils.sse;

import io.github.move.bricks.chi.utils.object.ObjectConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

import java.util.Objects;

/**
 * sseUtil工具类
 *
 * @author MoveBricks Chi
 * @version 1.0
 */
@Deprecated(since = "2.1.5", forRemoval = true)
@Slf4j
public final class SseUtil extends AbstractSseHandler {

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
