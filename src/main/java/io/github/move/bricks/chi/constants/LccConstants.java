package io.github.move.bricks.chi.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 常量类
 *
 * @author Liu Chunchi
 */
public final class LccConstants {


    /**
     * 成功
     */
    public static final Integer SUCCESS = 0;

    /**
     * 失败
     */
    public static final Integer FAIL = 1;

    @Getter
    @AllArgsConstructor
    public enum SuccessEnum {
        /**
         * 0成功码
         */
        SUCCESS_ZERO(0),
        /**
         * 200成功码
         */
        SUCCESS_2_HUNDRED(200);
        private final int code;
    }


}
