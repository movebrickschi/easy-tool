package io.github.movebrickschi.easytool.core.utils.video;

import cn.hutool.core.text.CharSequenceUtil;
import io.github.movebrickschi.easytool.core.enums.FFmpegEnums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * VideoConverter
 *
 * @author MoveBricks Chi
 * @since 2.5.7.1
 */
@Slf4j
public final class VideoConverter {


    /**
     * 将视频转换为 H.264
     *
     * @param inputPath  源视频路径
     * @param outputPath 目标视频路径
     * @return 转换是否成功
     */
    public static boolean convertToH264(String inputPath, String outputPath) {
        return convertToH264("ffmpeg", inputPath, outputPath);
    }

    /**
     * 将视频转换为 H.264
     *
     * @param ffmpegPath FFmpeg 路径 (例如 "ffmpeg" 或 "D:\\ffmpeg\\bin\\ffmpeg.exe")
     * @param inputPath  源视频路径
     * @param outputPath 目标视频路径
     * @return 转换是否成功
     */
    public static boolean convertToH264(String ffmpegPath, String inputPath, String outputPath) {
        return convertToH264(FFmpegCapabilityVerifierArgs.builder()
                .ffmpegPath(ffmpegPath)
                .inputPath(inputPath)
                .outputPath(outputPath)
                .build());
    }

    /**
     * 将视频转换为H.264
     *
     * @param args 参数
     * @return 转换是否成功
     */
    public static boolean convertToH264(FFmpegCapabilityVerifierArgs args) {
        // 构建命令列表
        // 对应命令: ffmpeg -i input.mp4 -c:v libx264 -crf 23 -c:a copy output.mp4
        List<String> command = new ArrayList<>();
        command.add(args.getFfmpegPath());
        command.add("-i");
        command.add(args.getInputPath());

        // 指定视频编码器为 H.264
        command.add("-c:v");
        if (Objects.isNull(args.getEncoder())) {
            command.add(new FFmpegCapabilityVerifier(args.getFfmpegPath()).getBestAvailableH264().getName());
        } else {
            // 使用指定编码器
            command.add(args.getEncoder().getName());
        }

        // 设置画质 (CRF 23 是平衡点，数值越小画质越好体积越大)
        command.add("-crf");
        if (CharSequenceUtil.isBlank(args.getCrf())) {
            command.add("23");
        } else {
            command.add(args.getCrf());
        }

        // 预设速度 (ultrafast, superfast, veryfast, faster, fast, medium, slow, slower, veryslow)
        // medium 是默认值，想转得快点可以用 fast
        command.add("-preset");
        command.add(args.getPreset().getValue());

        // 音频直接复制，不重新编码，加快速度
        command.add("-c:a");
        command.add("copy");

        // 如果原视频有同名文件，自动覆盖 (-y)
        command.add("-y");

        command.add(args.getOutputPath());

        // 执行命令
        ProcessBuilder processBuilder = new ProcessBuilder(command);

        // 将错误流重定向到标准输出流，这样我们就能看到 FFmpeg 的进度日志
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();

            // 读取控制台输出（FFmpeg 的日志主要在 ErrorStream 里，但上面已经重定向了）
            // 这一步非常重要！如果不读取流，缓冲区满了会导致进程挂起（假死）。
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info(line);
                }
            }

            // 等待进程结束
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                log.info("转换成功！文件已保存至: " + args.getOutputPath());
                return true;
            } else {
                System.err.println("转换失败，FFmpeg 退出代码: " + exitCode);
                return false;
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 将视频转换为 H.265
     *
     * @param inputPath  源视频路径
     * @param outputPath 目标视频路径
     * @return 转换是否成功
     */
    public static boolean convertToH265(String inputPath, String outputPath) {
        return convertToH265("ffmpeg", inputPath, outputPath);
    }

    /**
     * 将视频转换为 H.265
     *
     * @param ffmpegPath FFmpeg 路径 (例如 "ffmpeg" 或 "D:\\ffmpeg\\bin\\ffmpeg.exe")
     * @param inputPath  源视频路径
     * @param outputPath 目标视频路径
     * @return 转换是否成功
     */
    public static boolean convertToH265(String ffmpegPath, String inputPath, String outputPath) {
        return convertToH265(FFmpegCapabilityVerifierArgs.builder()
                .ffmpegPath(ffmpegPath)
                .inputPath(inputPath)
                .outputPath(outputPath)
                .build());
    }


    /**
     * 将视频转换为 H.265
     *
     * @param args 参数
     * @return 转换是否成功
     */
    public static boolean convertToH265(FFmpegCapabilityVerifierArgs args) {
        List<String> command = new ArrayList<>();
        command.add(args.getFfmpegPath());

        // 输入文件
        command.add("-i");
        command.add(args.getInputPath());

        // --- 视频编码设置 ---
        command.add("-c:v");
        if (Objects.isNull(args.getEncoder())) {
            command.add(new FFmpegCapabilityVerifier(args.getFfmpegPath()).getBestAvailableH265().getName());
        } else {
            command.add(args.getEncoder().getName());
        }

        // --- 苹果设备兼容性补丁 (非常重要) ---
        // 说明：默认的 H.265 标签有时候会导致在 macOS/iOS (QuickTime/iPhone) 上无法播放
        // 添加 -tag:v hvc1 可以解决这个问题，让苹果设备也能完美播放
        command.add("-tag:v");
        command.add("hvc1");

        // --- 画质控制 (CRF) ---
        // H.265 的压缩效率高，CRF 28 左右的画质通常肉眼看来相当于 H.264 的 CRF 23
        // 数值范围 0-51，数值越小画质越好体积越大。建议设置在 26-30 之间。
        command.add("-crf");
        if (CharSequenceUtil.isBlank(args.getCrf())) {
            command.add("28");
        } else {
            command.add(args.getCrf());
        }

        // --- 编码速度预设 ---
        // 选项: ultrafast, superfast, veryfast, faster, fast, medium, slow...
        // H.265 编码很慢，建议使用 'fast' 或 'faster' 以节省时间，虽然会牺牲一点点压缩率
        command.add("-preset");
        command.add(args.getPreset().getValue());

        // --- 音频设置 ---
        // 通常直接复制音频流即可，无需重新编码
        command.add("-c:a");
        command.add("copy");

        // --- 其他设置 ---
        command.add("-y"); // 覆盖同名文件
        command.add(args.getOutputPath()); // 输出路径

        // --- 执行命令 ---
        log.info("正在执行 FFmpeg 命令: " + String.join(" ", command));

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        // 合并错误流和标准流
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();

            // 读取日志输出 (防止进程缓冲区满导致挂起)
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // 打印关键进度信息，避免刷屏太快
                    if (line.contains("time=") || line.contains("fps=")) {
                        log.info(line);
                    }
                }
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                log.info("转换成功！");
                return true;
            } else {
                System.err.println("转换失败，退出码: " + exitCode);
                return false;
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 参数验证
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class FFmpegCapabilityVerifierArgs {
        /**
         * FFmpeg 路径
         */
        private String ffmpegPath;
        /**
         * 输入视频路径
         */
        private String inputPath;
        /**
         * 输出视频路径
         */
        private String outputPath;
        /**
         * 编码器
         */
        private FFmpegEncoder encoder;
        /**
         * 编码速度预设,默认FAST
         */
        @Builder.Default
        private FFmpegEnums.FfmpegPreset preset = FFmpegEnums.FfmpegPreset.FAST;

        /**
         * 数值范围（0-51）,数值越小画质越好
         * H.264,推荐范围：18 - 28，默认23
         * H.265,推荐范围：24 - 32，默认28
         */
        private String crf;
    }

}
