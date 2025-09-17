package io.github.movebrickschi.easytool.core.utils.watermark;

import ar.com.hjg.pngj.chunks.PngChunkTextVar;
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
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
     * 为图片添加文字水印（保留原图元数据）
     * @param file 图片文件
     * @param watermarkParameters 水印参数
     * @return 处理后的图片Base64编码
     * @throws IOException 读取图片或处理图片时发生错误
     */
    public static String forImageKeepMetadata(File file, WatermarkParameters watermarkParameters) throws IOException,
            ImageReadException {
        log.info("开始为图片添加文字水印并保留元数据，水印参数为：{}", JSONUtil.toJsonStr(watermarkParameters));

        if (CharSequenceUtil.isBlank(watermarkParameters.getText())) {
            throw new NullException("水印内容不能为空！");
        }

        // 读取原始图片及其元数据
        byte[] originalImageBytes = Files.readAllBytes(file.toPath());
        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(originalImageBytes));
        ImageMetadata originalMetadata = Imaging.getMetadata(originalImageBytes);

        // 添加水印
        BufferedImage watermarkedImage = watermarkToImage(
                originalImage,
                watermarkParameters.getText(),
                watermarkParameters.getPosition(),
                watermarkParameters.getAlpha(),
                watermarkParameters.getSize());

        // 将添加水印后的图片写入字节数组，并保留原始元数据
        String suffix = SUFFIX_MAP.getOrDefault(UrlUtil.suffix(file.getName()), ImagePool.PNG);
        byte[] watermarkedImageBytes = writeImageWithMetadata(watermarkedImage, suffix, originalMetadata,
                originalImageBytes);

        // 将图片转换为Base64编码
        return Base64.getEncoder().encodeToString(watermarkedImageBytes);
    }

    /**
     * 为图片添加文字水印（保留原图元数据）
     * @param imageUrl 图片URL
     * @param watermarkParameters 水印参数
     * @return 处理后的图片Base64编码
     * @throws IOException 读取图片或处理图片时发生错误
     */
    public static String forImageKeepMetadata(String imageUrl, WatermarkParameters watermarkParameters) throws IOException, ImageReadException {
        log.info("开始为图片添加文字水印并保留元数据，图片为：{}，水印参数为：{}", imageUrl, JSONUtil.toJsonStr(watermarkParameters));

        if (CharSequenceUtil.isBlank(watermarkParameters.getText())) {
            throw new NullException("水印内容不能为空！");
        }

        // 从URL读取图片
        URL url = new URL(imageUrl);
        byte[] originalImageBytes;
        try (InputStream inputStream = url.openStream()) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            originalImageBytes = buffer.toByteArray();
        }
        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(originalImageBytes));
        ImageMetadata originalMetadata = Imaging.getMetadata(originalImageBytes);

        // 添加水印
        BufferedImage watermarkedImage = watermarkToImage(
                originalImage,
                watermarkParameters.getText(),
                watermarkParameters.getPosition(),
                watermarkParameters.getAlpha(),
                watermarkParameters.getSize());

        // 将添加水印后的图片写入字节数组，并保留原始元数据
        String suffix = SUFFIX_MAP.getOrDefault(UrlUtil.extractFileName(imageUrl), ImagePool.PNG);
        byte[] watermarkedImageBytes = writeImageWithMetadata(watermarkedImage, suffix, originalMetadata,
                originalImageBytes);

        // 将图片转换为Base64编码
        return Base64.getEncoder().encodeToString(watermarkedImageBytes);
    }

    /**
     * 将BufferedImage写入字节数组，并保留原始元数据
     * @param image 图片
     * @param format 图片格式
     * @param originalMetadata 原始元数据
     * @return 图片字节数组
     * @throws IOException 写入图片时发生错误
     */
    private static byte[] writeImageWithMetadata(BufferedImage image, String format, ImageMetadata originalMetadata,
                                                 byte[] pngBytes) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // 如果是JPEG格式且有原始元数据，则保留元数据
        if ((ImagePool.JPEG.equalsIgnoreCase(format) || ImagePool.JPG.equalsIgnoreCase(format)) && originalMetadata != null) {
            try {
                // 先将水印图片写入字节数组
                ByteArrayOutputStream tempBaos = new ByteArrayOutputStream();
                ImageIO.write(image, format, tempBaos);
                byte[] watermarkedImageBytes = tempBaos.toByteArray();

                // 尝试保留原始元数据
                TiffOutputSet outputSet = getTiffOutputSet(originalMetadata);
                if (outputSet != null) {
                    new ExifRewriter().updateExifMetadataLossless(watermarkedImageBytes, baos, outputSet);
                    return baos.toByteArray();
                }
            } catch (Exception e) {
                log.warn("保留JPEG元数据时出错: {}", e.getMessage());
            }
        }
        // 如果是PNG格式，使用PNG元数据保留方法
        else if (ImagePool.PNG.equalsIgnoreCase(format)) {
            try {
                List<PngChunkTextVar> textChunks = PngProcessor.readTextChunks(pngBytes);
                return PngProcessor.writePngWithWatermarkAndText(pngBytes, image, textChunks);
            } catch (Exception e) {
                log.warn("保留PNG元数据时出错: {}", e.getMessage());
            }
        }

        // 如果保留元数据失败，使用普通方式写入
        ImageIO.write(image, format, baos);
        return baos.toByteArray();
    }

    /**
     * 获取TiffOutputSet用于JPEG元数据保留
     */
    private static TiffOutputSet getTiffOutputSet(ImageMetadata metadata) throws Exception {
        TiffOutputSet outputSet = null;
        if (metadata instanceof JpegImageMetadata) {
            JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
            TiffImageMetadata exif = jpegMetadata.getExif();
            if (exif != null) {
                outputSet = exif.getOutputSet();
            }
        }
        return outputSet;
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

        String testString = "测试字体是否支持";

        for (String fontName : fontNames) {
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
