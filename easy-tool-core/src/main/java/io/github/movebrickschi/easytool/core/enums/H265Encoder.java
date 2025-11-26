package io.github.movebrickschi.easytool.core.enums;

import io.github.movebrickschi.easytool.core.utils.video.FFmpegEncoder;

/**
 * H.265 (HEVC) 编码器枚举
 *
 * @author MoveBricks Chi
 * @since 2.5.7.1
 */
public enum H265Encoder implements FFmpegEncoder {

    // --- 软件编码 ---
    LIBX265("libx265", "【CPU软解】开源标准，画质最好体积最小，但编码极慢。无硬件要求。"),
    SVT_HEVC("libsvt_hevc", "【CPU软解】Intel/Netflix开发，多核优化好，适合服务器端。"),

    // --- 硬件编码 (GPU加速) ---
    NVENC("hevc_nvenc", "【NVIDIA显卡】GTX 900系列及以上。强烈推荐，解决H.265编码慢的痛点。"),
    QSV("hevc_qsv", "【Intel核显】Haswell架构及以上核显支持。"),
    AMF("hevc_amf", "【AMD显卡】较新的Radeon显卡支持。"),
    VIDEOTOOLBOX("hevc_videotoolbox", "【macOS专用】Apple设备硬件加速，M1/M2芯片效率极高。"),
    MEDIACODEC("hevc_mediacodec", "【Android专用】调用安卓底层MediaCodec接口进行硬编。");

    private final String name;
    private final String description;

    H265Encoder(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String getName() { return name; }

    @Override
    public String getDescription() { return description; }
}