package io.github.movebrickschi.easytool.core.utils.video;

/**
 * FFmpeg 编码器接口
 *
 * @author MoveBricks Chi
 * @since 2.5.7.1
 */
public interface FFmpegEncoder {
    /**
     * 获取传递给 FFmpeg 的参数名 (例如: libx264)
     */
    String getName();

    /**
     * 获取硬件要求和说明
     */
    String getDescription();
}