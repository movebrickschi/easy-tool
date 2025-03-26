package io.github.movebrickschi.easytool.ai.sse.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * 接收大模型返回的原始数据
 *
 * @author MoveBricks Chi
 * @since 3.0.1
 */
@Slf4j
public class AcceptObjectHandler implements FluxEventHandler {

    @Override
    public Flux<ServerSentEvent<String>> handle(FluxEventData fluxEventData) {
        ObjectMapper objectMapper = new ObjectMapper();
        return fluxEventData.getPost().retrieve()
                .bodyToFlux(byte[].class)
                .map(String::new)
                .filter(line -> !line.isEmpty())
                .flatMap(line -> {
                    try {
                        return Mono.just(objectMapper.readTree(line));
                    } catch (Exception e) {
                        System.out.println("解析错误: " + e.getMessage());
                        return Mono.empty();
                    }
                })
                .filter(jsonObject -> null != findContentField(jsonObject, fluxEventData.getContentField()))
                .map(jsonObject -> Objects.requireNonNull(findContentField(jsonObject,
                        fluxEventData.getContentField())).asText())
                .map(content -> ServerSentEvent.builder(content).build())
                .doOnNext(event -> {
                    log.info("Received event: data={}, id={}, event={}", event.data(), event.id(),
                            event.event());
                    if (Objects.nonNull(fluxEventData.getConsumer())) {
                        fluxEventData.getConsumer().accept(event);
                    }
                })
                .concatWith(Flux.just(ServerSentEvent.builder(fluxEventData.getEndEvent()).event(fluxEventData
                        .getEndEvent()).build()))
                .onErrorResume(e -> {
                    log.error("Error in optimizePrompt: {}", e.getMessage(), e);
                    return Flux.error(e);
                });

    }


    private static JsonNode findContentField(JsonNode node, String contentField) {
        if (node.isObject()) {
            for (JsonNode childNode : node) {
                if (childNode.has(contentField)) {
                    return childNode.get(contentField);
                } else {
                    JsonNode result = findContentField(childNode, contentField);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }
}
