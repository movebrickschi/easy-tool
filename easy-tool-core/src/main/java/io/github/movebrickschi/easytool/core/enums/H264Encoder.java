package io.github.movebrickschi.easytool.core.enums;

import io.github.movebrickschi.easytool.core.utils.video.FFmpegEncoder;

/**
 * H.264 (AVC) 编码器枚举
 *
 * @author MoveBricks Chi
 * @since 2.5.7.1
 */
public enum H264Encoder implements FFmpegEncoder {
    
    // --- 软件编码 ---
    LIBX264("libx264", "【CPU软解】全球标准，画质最佳，兼容性最好。无特殊硬件要求。"),
    LIBOPENH264("libopenh264", "【CPU软解】Cisco开发，主要用于WebRTC实时通信。"),

    // --- 硬件编码 (GPU加速) ---
    NVENC("h264_nvenc", "【NVIDIA显卡】GeForce 600系列及以上。速度极快，首选硬编方案。"),
    QSV("h264_qsv", "【Intel核显】Quick Sync Video。Intel CPU自带核显可用，常见于办公本。"),
    AMF("h264_amf", "【AMD显卡】Radeon系列显卡支持。"),
    VIDEOTOOLBOX("h264_videotoolbox", "【macOS/iOS专用】调用Apple芯片(M1/M2/Intel)底层硬件加速。"),
    OMX("h264_omx", "【树莓派/嵌入式】Raspberry Pi 等嵌入式设备的硬件加速。");

    private final String name;
    private final String description;

    H264Encoder(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String getName() { return name; }

    @Override
    public String getDescription() { return description; }
}