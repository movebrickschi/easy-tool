package io.github.movebrickschi.easytool.ai.sse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

/**
 * sse工具类
 *
 * @author MoveBricks Chi
 * @version 1.0
 */
@Slf4j
public abstract class AbstractSseHandler {

    /**
     * 获取sse
     *
     * @param sseArgs sse参数
     * @return sse
     */
    protected abstract Flux<ServerSentEvent<String>> getSse(SseArgs sseArgs);


}
