package io.github.movebrickschi.easytool.core.utils.watermark;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import io.github.movebrickschi.easytool.core.constants.ImagePool;
import io.github.movebrickschi.easytool.core.dto.WatermarkParameters;
import io.github.movebrickschi.easytool.core.exception.NullException;
import io.github.movebrickschi.easytool.core.utils.file.InputStreamToFileUtil;
import io.github.movebrickschi.easytool.core.utils.ssl.SslUtil;
import io.github.movebrickschi.easytool.core.utils.string.StringUtil;
import io.github.movebrickschi.easytool.core.utils.url.UrlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 水印工具类
 *
 * @author MoveBricks Chi
 * @since 1.0
 */
@Slf4j
public final class WatermarkUtil {

    // 定义水印位置常量
    private static final String POSITION_CENTER = "center";
    private static final String POSITION_BOTTOM_LEFT = "bottom-left";
    private static final String POSITION_BOTTOM_RIGHT = "bottom-right";

    // 支持的图片格式映射
    private static final Map<String, String> SUFFIX_MAP = new ConcurrentHashMap<>();

    static {
        SUFFIX_MAP.put(ImagePool.JPG, ImagePool.JPEG);
        SUFFIX_MAP.put(ImagePool.JPEG, ImagePool.JPEG);
        SUFFIX_MAP.put(ImagePool.PNG, ImagePool.PNG);
        SUFFIX_MAP.put(ImagePool.BMP, ImagePool.BMP);
        SUFFIX_MAP.put(ImagePool.GIF, ImagePool.GIF);

        // 强制禁用SSL证书验证
        SslUtil.disableSSLCertificateValidation();
    }

    private WatermarkUtil() {
    }

    /**
     * 为图片添加文字水印
     * @param imageUrl 图片URL
     * @param watermarkParameters 水印参数
     * @return 处理后的图片Base64编码
     * @throws IOException 读取图片或处理图片时发生错误
     */
    public static String forImage(String imageUrl, WatermarkParameters watermarkParameters) throws IOException {
        log.info("开始为图片添加文字水印，图片为：{}，水印参数为：{}", imageUrl, JSONUtil.toJsonStr(watermarkParameters));
        if (CharSequenceUtil.isBlank(watermarkParameters.getText())) {
            throw new NullException("水印内容不能为空！");
        }
        try {
            // 从URL读取图片
            URL url = new URL(imageUrl);
            BufferedImage processedImage = ImageIO.read(url);
            return execute(UrlUtil.extractFileName(imageUrl), watermarkParameters, processedImage);
        } catch (MalformedURLException e) {
            throw new MalformedURLException("url格式错误");
        } catch (IOException e) {
            throw new IOException("图片水印处理图片时发生错误", e);
        }
    }

    /**
     * 为图片添加文字水印
     * @param file 图片文件
     * @param watermarkParameters 水印参数
     * @return 处理后的图片Base64编码
     * @throws IOException 读取图片或处理图片时发生错误
     */
    public static String forImage(File file, WatermarkParameters watermarkParameters) throws IOException {
        log.info("开始为图片添加文字水印，水印参数为：{}", JSONUtil.toJsonStr(watermarkParameters));
        // 将上传的文件转换为Image对象
        BufferedImage processedImage = null;
        try {
            processedImage = ImageIO.read(Files.newInputStream(file.toPath()));
            return execute(file.getName(), watermarkParameters, processedImage);
        } catch (IOException e) {
            throw new IOException("图片水印处理图片时发生错误", e);
        }
    }

    /**
     * 执行图片处理
     * @param originalFilename 原始文件名
     * @param watermarkParameters 水印参数
     * @param processedImage 处理的图片
     * @return  处理后的图片Base64编码
     * @throws IOException 图片处理时发生错误
     */
    private static String execute(String originalFilename, WatermarkParameters watermarkParameters,
                                  BufferedImage processedImage) throws IOException {
        // 添加文字水印
        processedImage = watermarkToImage(processedImage, watermarkParameters.getText(),
                watermarkParameters.getPosition(), watermarkParameters.getAlpha(), watermarkParameters.getSize());

        // 获取原始文件的格式
        String suffix = ImagePool.PNG;
        if (StringUtils.isNotBlank(originalFilename)) {
            suffix = SUFFIX_MAP.getOrDefault(InputStreamToFileUtil.extension(originalFilename), ImagePool.PNG);
        }
        // 直接转换图片为字节数组
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(processedImage, suffix, baos);
        byte[] imageBytes = baos.toByteArray();
        // 将图片转换为Base64编码
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    /**
     * 添加文字水印
     */
    private static BufferedImage watermarkToImage(BufferedImage originalImage, String text, String position,
                                                  Integer alpha, Integer size) {
        int imgWidth = originalImage.getWidth();
        int imgHeight = originalImage.getHeight();

        // 如果使用默认大小，可以根据图片大小进行动态调整
        if (size == 40) {
            int minDimension = Math.min(imgWidth, imgHeight);
            // 根据图片大小调整文字大小，确保在小图上不会太大，在大图上不会太小
            size = Math.max(20, Math.min(80, minDimension / 20));
        }
        BufferedImage bufImg = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics = bufImg.createGraphics();
        graphics.drawImage(originalImage, 0, 0, imgWidth, imgHeight, null);

        // 设置支持中文的字体
        Font font = getSupportedFont(size);
//        graphics.setColor(new Color(0, 0, 0, alpha));
//        graphics.setFont(font);


        FontMetrics fontMetrics = graphics.getFontMetrics();
        int textWidth = StringUtil.width(text, size);
        int textHeight = fontMetrics.getHeight();

        // 根据位置参数计算水印坐标
        int x = 0;
        int y = 0;

        log.info("处理水印位置: {}", position);
        switch (position.toLowerCase()) {
            case POSITION_CENTER:
                log.info("设置水印位置为居中");
                x = (imgWidth - textWidth) / 2;
                // 垂直居中调整
                y = imgHeight / 2 + textHeight / 4;
                break;
            case POSITION_BOTTOM_LEFT:
                log.info("设置水印位置为左下角");
                // 左边距
                x = 10;
                // 下边距
                y = imgHeight - 10;
                break;
            case POSITION_BOTTOM_RIGHT:
                log.info("设置水印位置为右下角");
                // 右边距
                x = imgWidth - textWidth - 10;
                // 下边距
                y = imgHeight - 10;
                break;
            default:
                log.warn("未知的位置参数: {}, 使用默认居中位置", position);
                // 默认居中
                x = (imgWidth - textWidth) / 2;
                y = imgHeight / 2 + textHeight / 4;
        }
        drawTextWithOutline(graphics, text, x, y, alpha, font);
        // 启用抗锯齿渲染，提高中文显示质量
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        // 添加坐标调试日志
        log.info("水印坐标 - X: {}, Y: {}", x, y);
        graphics.drawString(text, x, y);
        graphics.dispose();

        return bufImg;
    }

    /**
     * 获取支持中文的字体
     *
     * @param size 字体大小
     * @return 支持中文的字体
     */
    private static Font getSupportedFont(int size) {
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

        String testString = "测试123";

        for (String fontName : fontNames) {
            try {
                Font font = new Font(fontName, Font.BOLD, size);
                // 检查字体是否能正确显示中文
                if (font.canDisplayUpTo(testString) == -1) {
                    log.info("使用字体: {} (大小: {})", fontName, size);
                    return font;
                } else {
                    log.debug("字体 {} 不能完全显示测试文本", fontName);
                }
            } catch (Exception e) {
                log.debug("字体 {} 不可用: {}", fontName, e.getMessage());
            }
        }

        // 如果没有找到合适的字体，使用系统默认字体
        log.warn("未找到合适的中文字体，使用系统默认字体");
        return new Font(Font.DIALOG, Font.BOLD, size);
    }

    /**
     * 绘制带轮廓的文字水印，确保在各种背景上都可见
     *
     * @param graphics Graphics2D对象
     * @param text     水印文字
     * @param x        X坐标
     * @param y        Y坐标
     * @param alpha    透明度
     * @param font     字体
     */
    private static void drawTextWithOutline(Graphics2D graphics, String text, int x, int y, int alpha, Font font) {
        // 设置字体
        graphics.setFont(font);

        // 绘制黑色轮廓（半透明）
        graphics.setColor(new Color(0, 0, 0, alpha / 2));
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                // 创建圆形轮廓效果
                if (Math.abs(i) + Math.abs(j) <= 2) {
                    graphics.drawString(text, x + i, y + j);
                }
            }
        }
        // 绘制白色主文字（半透明）
        graphics.setColor(new Color(255, 255, 255, alpha));
        graphics.drawString(text, x, y);
    }


}
