package io.github.movebrickschi.easytool.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * FFmpegEnums
 *
 * @author MoveBricks Chi
 * @since 2.5.7.1
 */
public final class FFmpegEnums {

    @Getter
    @AllArgsConstructor
    public enum FfmpegPreset {
        /**
         * ultrafast 极速
         */
        ULTRAFAST("ultrafast"),
        /**
         * superfast 超快
         */
        SUPERFAST("superfast"),
        /**
         * veryfast 非常快
         */
        VERYFAST("veryfast"),
        /**
         * faster 很快
         */
        FASTER("faster"),
        /**
         * fast 快
         */
        FAST("fast"),
        /**
         * medium 中等
         */
        MEDIUM("medium"),
        /**
         * slow 慢
         */
        SLOW("slow"),
        /**
         * slower 很慢
         */
        SLOWER("slower"),
        /**
         * veryslow 非常慢
         */
        VERYSLOW("veryslow");
        private final String value;
    }

}
