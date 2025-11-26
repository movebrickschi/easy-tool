package io.github.movebrickschi.easytool.core.utils.video;

import io.github.movebrickschi.easytool.core.enums.H264Encoder;
import io.github.movebrickschi.easytool.core.enums.H265Encoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 *  FFmpegCapabilityVerifier
 *  用于检测当前环境下 FFmpeg 支持的编码器情况
 *
 * @author MoveBricks Chi
 * @since 2.5.7.1
 */
public class FFmpegCapabilityVerifier {

    private final String ffmpegPath;
    // 使用 Set 缓存已支持的编码器名称，避免重复执行命令
    private Set<String> supportedEncodersCache = null;

    public FFmpegCapabilityVerifier(String ffmpegPath) {
        this.ffmpegPath = ffmpegPath;
    }

    /**
     * 判断当前环境是否支持指定的编码器枚举
     */
    public boolean isSupported(FFmpegEncoder encoder) {
        if (supportedEncodersCache == null) {
            refreshAvailableEncoders();
        }
        return supportedEncodersCache.contains(encoder.getName());
    }

    /**
     * 获取当前环境支持的最佳 H.264 编码器 (优先硬解，降级软解)
     */
    public H264Encoder getBestAvailableH264() {
        // 优先级顺序：N卡 -> Mac -> Intel -> AMD -> CPU
        if (isSupported(H264Encoder.NVENC) && testEncoder(H264Encoder.NVENC)) return H264Encoder.NVENC;
        if (isSupported(H264Encoder.VIDEOTOOLBOX) && testEncoder(H264Encoder.VIDEOTOOLBOX))
            return H264Encoder.VIDEOTOOLBOX;
        if (isSupported(H264Encoder.QSV) && testEncoder(H264Encoder.QSV)) return H264Encoder.QSV;
        if (isSupported(H264Encoder.AMF) && testEncoder(H264Encoder.AMF)) return H264Encoder.AMF;
        return H264Encoder.LIBX264; // 兜底
    }

    /**
     * 获取当前环境支持的最佳 H.265 编码器
     */
    public H265Encoder getBestAvailableH265() {
        if (isSupported(H265Encoder.NVENC) && testEncoder(H265Encoder.NVENC)) return H265Encoder.NVENC;
        if (isSupported(H265Encoder.VIDEOTOOLBOX) && testEncoder(H265Encoder.VIDEOTOOLBOX))
            return H265Encoder.VIDEOTOOLBOX;
        if (isSupported(H265Encoder.QSV) && testEncoder(H265Encoder.QSV)) return H265Encoder.QSV;
        if (isSupported(H265Encoder.AMF) && testEncoder(H265Encoder.AMF)) return H265Encoder.AMF;
        return H265Encoder.LIBX265; // 兜底
    }

    /**
     * 核心方法：执行 ffmpeg -encoders 并解析结果
     */
    private synchronized void refreshAvailableEncoders() {
        supportedEncodersCache = new HashSet<>();

        // 构建命令: ffmpeg -encoders
        ProcessBuilder pb = new ProcessBuilder(ffmpegPath, "-encoders");
        pb.redirectErrorStream(true);

        try {
            Process process = pb.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                // 输出示例: " V..... libx264              x264 H.264/AVC encoder (codec h264)"
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    // 简单的解析逻辑：如果行包含 'V' (Video) 并且包含编码器名称
                    // 这里为了简单，我们只要提取出的第二列字符串匹配即可
                    // 实际输出通常在第2列，或者我们可以直接简单粗暴地判断 contains

                    // 更严谨的解析:
                    // 跳过头部说明，查找以 V 开头的行
                    if (line.startsWith("V")) {
                        String[] parts = line.split("\\s+"); // 按空格分割
                        if (parts.length >= 2) {
                            String encoderName = parts[1]; // 第二列通常是编码器名
                            supportedEncodersCache.add(encoderName);
                        }
                    }
                }
            }
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            System.err.println("无法执行 FFmpeg 检测命令: " + e.getMessage());
            // 发生异常时，至少把基础软解加入，防止程序完全崩溃
            supportedEncodersCache.add("libx264");
            supportedEncodersCache.add("libx265");
        }
    }

    /**
     * 实际测试编码器是否真正可用
     * 通过编码一帧黑色画面来验证
     */
    private boolean testEncoder(FFmpegEncoder encoder) {
        try {
            // 生成1秒钟的黑色视频测试编码器
            ProcessBuilder pb = new ProcessBuilder(
                    ffmpegPath,
                    "-f", "lavfi",
                    "-i", "color=black:s=320x240:d=0.1",
                    "-c:v", encoder.getName(),
                    "-f", "null",
                    "-"
            );
            pb.redirectErrorStream(true);

            Process process = pb.start();

            // 读取输出避免缓冲区阻塞
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                while (reader.readLine() != null) {
                    // 忽略输出
                }
            }

            int exitCode = process.waitFor();
            return exitCode == 0;

        } catch (Exception e) {
            return false;
        }
    }

//    public static void main(String[] args) {
//        // 配置路径
//        String ffmpegExec = "ffmpeg"; // 确保已配置环境变量，否则写全路径
//        String sourceFile = "C:\\Users\\Administrator\\Downloads\\output_h264.mp4";
//        String targetFile = "C:\\Users\\Administrator\\Downloads\\output_h265.mp4";
//
//        System.out.println("开始将视频转为 H.265 (HEVC)... 此过程可能较慢，请耐心等待。");
//        long start = System.currentTimeMillis();
//
//        boolean success = convertToH265(sourceFile, targetFile);
//
//        long end = System.currentTimeMillis();
//        if (success) {
//            System.out.println("耗时: " + (end - start) / 1000 + " 秒");
//        }
//    }


}