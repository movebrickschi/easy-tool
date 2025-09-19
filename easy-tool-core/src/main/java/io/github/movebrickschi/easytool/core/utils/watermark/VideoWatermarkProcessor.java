package io.github.movebrickschi.easytool.core.utils.watermark;

import cn.hutool.core.util.IdUtil;
import io.github.movebrickschi.easytool.core.dto.WatermarkParameters;
import io.github.movebrickschi.easytool.core.enums.PositionEnum;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameFilter;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * 视频加水印处理类
 *
 * @author MoveBricks Chi
 * @since 1.0
 */
public class VideoWatermarkProcessor extends WatermarkProcessor {

    @Override
    public String addWatermark(File file, WatermarkParameters watermarkParameters) throws Exception {
        //使用javacv实现
        //临时目标文件
        File tempFileTarget = new File("watermarked-video" + IdUtil.getSnowflakeNextId() + ".mp4");
        //水印图片
        BufferedImage watermarkImage = WatermarkUtil.createWaterMarkImage(watermarkParameters.getText(),
                watermarkParameters.getSize());
        File tempFileWatermark = new File(IdUtil.getSnowflakeNextId() + ".png");
        ImageIO.write(watermarkImage, "png", tempFileWatermark);

        try {
            FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(file);
            frameGrabber.start();
            int width = frameGrabber.getImageWidth();
            int height = frameGrabber.getImageHeight();
            int channels = frameGrabber.getAudioChannels();
            FFmpegFrameRecorder frameRecorder = new FFmpegFrameRecorder(tempFileTarget, width, height, channels);
            int frameRate = (int) frameGrabber.getFrameRate();
            frameRecorder.setFrameRate(frameRate);
            frameRecorder.setSampleRate(frameGrabber.getSampleRate());
            frameRecorder.setAudioBitrate(frameGrabber.getAudioBitrate());
            frameRecorder.setVideoBitrate(frameGrabber.getVideoBitrate());
            //yuv420p颜色空间生成mp4文件
            frameRecorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
            frameRecorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            frameRecorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);

            // 根据位置参数动态设置水印位置
            String watermark;
            switch (PositionEnum.getPosition(watermarkParameters.getPosition())) {
                case CENTER:
                    // 居中位置
                    watermark = String.format("movie=%s[watermark];[in][watermark]overlay=(W-w)/2:(H-h)" +
                                    "/2:format=rgb[out]"
                            , getAvailableWaterMarkPath(tempFileWatermark.getAbsolutePath()));
                    break;
                case TOP_LEFT:
                    // 左上角位置
                    watermark = String.format("movie=%s[watermark];[in][watermark]overlay=10:10:format=rgb[out]"
                            , getAvailableWaterMarkPath(tempFileWatermark.getAbsolutePath()));
                    break;
                case TOP_RIGHT:
                    // 右上角位置
                    watermark = String.format("movie=%s[watermark];[in][watermark]overlay=(W-w-10):10:format=rgb[out]"
                            , getAvailableWaterMarkPath(tempFileWatermark.getAbsolutePath()));
                    break;
                case BOTTOM_LEFT:
                    // 左下角位置
                    watermark = String.format("movie=%s[watermark];[in][watermark]overlay=10:(H-h-10):format=rgb[out]"
                            , getAvailableWaterMarkPath(tempFileWatermark.getAbsolutePath()));
                    break;
                case BOTTOM_RIGHT:
                default:
                    // 右下角位置（默认）
                    watermark = String.format("movie=%s[watermark];[in][watermark]overlay=(W-w-10):(H-h-10)" +
                                    ":format=rgb[out]"
                            , getAvailableWaterMarkPath(tempFileWatermark.getAbsolutePath()));
                    break;
            }

            FFmpegFrameFilter frameFilter = new FFmpegFrameFilter(watermark, width, height);
            frameFilter.setPixelFormat(avutil.AV_PIX_FMT_BGR24);
            frameFilter.start();

            frameRecorder.start();
            while (true) {
                Frame frame = frameGrabber.grab();
                if (frame != null) {
                    frameFilter.push(frame);
                    Frame filteredFrame = frameFilter.pull();
                    frameRecorder.record(filteredFrame);
                } else {
                    break;
                }
            }

            frameRecorder.setMetadata(frameGrabber.getMetadata());
            frameRecorder.stop();
            frameRecorder.release();
            frameFilter.stop();
            frameFilter.release();
            frameGrabber.stop();
            frameGrabber.release();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            // 读取处理后的视频文件并转换为Base64字符串
            byte[] videoBytes = java.nio.file.Files.readAllBytes(tempFileTarget.toPath());
            String base64String = java.util.Base64.getEncoder().encodeToString(videoBytes);

            tempFileTarget.delete();
            tempFileWatermark.delete();

            return base64String;
        }
    }

    @Override
    public String addWatermark(String url, WatermarkParameters watermarkParameters) throws Exception {
        return "";
    }

    @Override
    public String addWatermarkKeepMetadata(File file, WatermarkParameters watermarkParameters) throws Exception {
        return "";
    }

    @Override
    public String addWatermarkKeepMetadata(String url, WatermarkParameters watermarkParameters) throws Exception {
        return "";
    }


    private static String getAvailableWaterMarkPath(String sourcePath) {
        sourcePath = sourcePath.replace("\\", "/");
        int colonIndex = sourcePath.indexOf(':');
        // 如果找到冒号

        if (colonIndex != -1) {
            // 在冒号前面插入 \\
            return sourcePath.substring(0, colonIndex) + "\\\\" + sourcePath.substring(colonIndex);
        }

        // 如果没有找到冒号，返回原路径
        return sourcePath;
    }
}
