package io.github.movebrickschi.easytool.ai.sse.handler;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

/**
 * FluxEventHandler
 *
 * @author MoveBricks Chi
 * @version 1.0
 * @since 3.0.1
 */
public interface FluxEventHandler {

    Flux<ServerSentEvent<String>> handle(FluxEventData fluxEventData);

}
