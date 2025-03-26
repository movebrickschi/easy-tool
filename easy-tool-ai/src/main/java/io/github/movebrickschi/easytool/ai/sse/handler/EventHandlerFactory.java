package io.github.movebrickschi.easytool.ai.sse.handler;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * sse处理工厂
 *
 * @author MoveBricks Chi
 * @since 3.0.1
 */
public final class EventHandlerFactory {

    static ConcurrentHashMap<String, FluxEventHandler> OPERATION_MAP = new ConcurrentHashMap<>();

    static {
        OPERATION_MAP.put("true", new AcceptEventHandler());
        OPERATION_MAP.put("false", new AcceptObjectHandler());
    }

    public static FluxEventHandler apply(String operation) {
        return Optional.ofNullable(OPERATION_MAP.get(operation))
                .orElseThrow(() -> new RuntimeException("No operation found"));
    }
}
