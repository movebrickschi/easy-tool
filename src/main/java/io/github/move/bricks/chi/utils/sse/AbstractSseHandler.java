package io.github.move.bricks.chi.utils.sse;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

/**
 * sse工具类
 *
 * @author MoveBricks Chi
 * @version 1.0
 */
@Slf4j
public abstract class AbstractSseHandler {

    @Resource
    protected WebClient webClient;

    /**
     * 获取sse
     *
     * @param sseArgs sse参数
     * @return sse
     */
    protected abstract Flux<ServerSentEvent<String>> getSse(SseArgs sseArgs);


}
