package io.github.move.bricks.chi.utils.sse;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * sse常量
 *
 * @author MoveBricks Chi 
 * @version 1.0
 */
public final class CSseConstants {

    @Getter
    @AllArgsConstructor
    public enum SseEventType {
        /**
         * sse 结束事件类型
         */
        FINISH("finish"),
        ;
        private final String event;

    }

}
