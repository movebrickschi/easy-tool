package io.github.movebrickschi.easytool.core.utils.watermark;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.google.common.collect.Lists;
import io.github.movebrickschi.easytool.core.constants.FileTypeConstants;
import io.github.movebrickschi.easytool.core.dto.WatermarkParameters;
import io.github.movebrickschi.easytool.core.utils.watermark.factory.WatermarkProcessorFactory;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * 水印工具类
 * 使用前提对应服务器要有支持中文的字体，否则
 * 中文不能正常显示
 *
 * @author MoveBricks Chi
 * @since 1.0
 */
@Slf4j
public final class WatermarkUtil {

    /**
     * 给文件添加水印
     * @param file  文件
     * @param watermarkParameters 水印参数
     * @return 加完水印文件base64
     */
    public static String addWaterMark(File file, WatermarkParameters watermarkParameters) {
        try {
            String fileType = getSourceType(Files.newInputStream(file.toPath()));
            WatermarkProcessor processor = WatermarkProcessorFactory.getProcessor(fileType);
            return processor.addWatermark(file, watermarkParameters);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 给网络地址文件添加水印
     * @param url 文件地址
     * @param watermarkParameters 水印参数
     * @return 加完水印文件base64
     */
    public static String addWaterMark(String url, WatermarkParameters watermarkParameters) {
        try {
            String fileType = getSourceType(new URL(url).openStream());
            WatermarkProcessor processor = WatermarkProcessorFactory.getProcessor(fileType);
            return processor.addWatermark(url, watermarkParameters);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 给文件添加水印,保留元数据
     * @param file  文件
     * @param watermarkParameters 水印参数
     * @return 加完水印文件base64
     */
    public static String addWaterMarkKeepMetadata(File file, WatermarkParameters watermarkParameters) {
        try {
            String fileType = getSourceType(Files.newInputStream(file.toPath()));
            WatermarkProcessor processor = WatermarkProcessorFactory.getProcessor(fileType);
            return processor.addWatermarkKeepMetadata(file, watermarkParameters);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 给网络地址文件添加水印，保留元数据
     * @param url 文件地址
     * @param watermarkParameters 水印参数
     * @return 加完水印文件base64
     */
    public static String addWaterMarkKeepMetadata(String url, WatermarkParameters watermarkParameters) {
        try {
            String fileType = getSourceType(new URL(url).openStream());
            WatermarkProcessor processor = WatermarkProcessorFactory.getProcessor(fileType);
            return processor.addWatermarkKeepMetadata(url, watermarkParameters);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 获取文件类型
     * @param inputStream 文件输入流
     * @return 文件类型
     */
    private static String getSourceType(InputStream inputStream) {
        String fileType = FileTypeUtil.getType(inputStream);

        try {
            FileTypeConstants.ImagePool.getImagePool(fileType);
            return FileTypeConstants.IMAGE;
        } catch (Exception e) {
            // 不是图片类型
        }
        try {
            FileTypeConstants.VideoPool.getVideoPool(fileType);
            return FileTypeConstants.VIDEO;
        } catch (Exception e) {
            // 不是视频类型
        }

        if (fileType.equalsIgnoreCase(FileTypeConstants.PDF)) {
            return FileTypeConstants.PDF;
        }
        return FileTypeConstants.UNKNOWN;
    }


    /**
     * 获取支持中文的字体
     *
     * @param specifiedFontName 指定字体名称
     * @param size 字体大小
     * @return 支持中文的字体
     */
    public static Font getSupportedFont(String specifiedFontName, int size) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        log.info("系统可用字体数量: {}", ge.getAvailableFontFamilyNames().length);
        // 按优先级排序的字体列表（增加Linux友好的字体）
        String[] fontNames = {
                // Linux友好的字体
                "Noto Sans CJK SC",
                "WenQuanYi Micro Hei",
                "AR PL UKai CN",
                "AR PL UMing CN",
                "WenQuanYi Zen Hei",

                // 跨平台字体
                "DejaVu Sans",
                "Arial Unicode MS",

                // Windows字体（在Linux上可能通过wine或其他方式安装）
                "微软雅黑",
                "Microsoft YaHei",
                "SimHei",
                "黑体",
                "Songti SC",
                "宋体",

                // 默认备选
                "Dialog",
                "SansSerif"
        };

        String testString = "测试字体是否支持";

        ArrayList<String> fontNamesList = Lists.newArrayList(fontNames);

        if (CharSequenceUtil.isNotBlank(specifiedFontName)) {
            fontNamesList.add(0, specifiedFontName);
        }

        for (String fontName : fontNamesList) {
            try {
                Font font = new Font(fontName, Font.BOLD, size);
                // 检查字体是否能正确显示中文
                if (font.canDisplayUpTo(testString) == -1) {
                    log.info("使用字体: {} (大小: {})", fontName, size);
                    return font;
                } else {
                    log.warn("字体 {} 不能完全显示测试文本", fontName);
                }
            } catch (Exception e) {
                log.error("字体 {} 不可用: {}", fontName, e.getMessage());
            }
        }

        // 如果没有找到合适的字体，使用系统默认字体
        log.warn("未找到合适的中文字体，使用系统默认字体");
        return new Font(Font.DIALOG, Font.BOLD, size);
    }


    /**
     * 创建一个透明水印图片
     * @param text 水印文字
     * @param fontSize 字体大小
     * @return 水印图片
     */
    public static BufferedImage createWaterMarkImage(String text, int fontSize) {
        try {
            BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            Graphics2D tempG2D = tempImage.createGraphics();
            InputStream fontStream = WatermarkUtil.class.getClassLoader().getResourceAsStream("Alibaba-PuHuiTi" +
                    "-Regular.ttf");
            if (fontStream == null) {
                throw new RuntimeException("Font file not found in resources");
            }
            Font font = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(Font.BOLD, fontSize);
            fontStream.close();
            // 使用支持中文的字体
            tempG2D.setFont(font);
            FontMetrics fontMetrics = tempG2D.getFontMetrics();
            int textWidth = fontMetrics.stringWidth(text);
            int textHeight = fontMetrics.getHeight();
            tempG2D.dispose();

            // 计算图片的宽度和高度
            // 边距
            int padding = 20;
            int width = textWidth + 2 * padding;
            int height = textHeight + 2 * padding;

            // 创建一个透明的BufferedImage
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();

            // 设置背景为透明
            g2d.setComposite(AlphaComposite.Clear);
            g2d.fillRect(0, 0, width, height);

            // 设置文字样式
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // 设置透明度
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            // 文字颜色
            g2d.setColor(Color.WHITE);
            // 使用支持中文的字体
            g2d.setFont(font);

            // 计算文字的位置
            int textX = padding;
            int textY = (height + textHeight) / 2 - fontMetrics.getDescent();
            // 绘制文字
            g2d.drawString(text, textX, textY);

            // 释放资源
            g2d.dispose();

            return image;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


//    public static void main(String[] args) throws IOException {
//        String path = "F:\\lcc\\workSpace\\WaterMarkDemo\\src\\main\\resources\\视频.mp4";
//        String base64 = WatermarkUtil.addWaterMark(new File(path), WatermarkParameters.builder()
//                .text("AI生成")
//                .size(20)
//                .position(PositionEnum.TOP_RIGHT.getPosition())
//                .build());
//        Base64Util.toFile(base64, "F:\\lcc\\workSpace\\WaterMarkDemo\\src\\main\\resources\\水印.mp4");


//        String url = "https://file-editing.oss-cn-shanghai.aliyuncs.com/AIOralVideo/2025/9/19/1758253859218huizhi_D26A6446-72AE-407F-B915-4957E36A6061.mp4";
//        String base64 = WatermarkUtil.addWaterMark(url, WatermarkParameters.builder()
//                .text("AI生成")
//                .size(50)
//                .position(PositionEnum.BOTTOM_RIGHT.getPosition())
//                .build());
//        Base64Util.toFile(base64, "F:\\lcc\\workSpace\\WaterMarkDemo\\src\\main\\resources\\水印.mp4");

//        String path = "F:\\lcc\\workSpace\\WaterMarkDemo\\src\\main\\resources\\PDF.pdf";
//        String base64 = WatermarkUtil.addWaterMark(new File(path), WatermarkParameters.builder()
//                .text("AI生成")
//                .size(50)
//                .rotation(60f)
//                .build());
//        Base64Util.toFile(base64, "F:\\lcc\\workSpace\\WaterMarkDemo\\src\\main\\resources\\水印.pdf");
//    }


}
