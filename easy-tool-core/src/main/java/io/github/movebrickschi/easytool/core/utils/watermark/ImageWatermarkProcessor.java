package io.github.movebrickschi.easytool.core.utils.watermark;

import ar.com.hjg.pngj.chunks.PngChunkTextVar;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import io.github.movebrickschi.easytool.core.constants.FileTypeConstants;
import io.github.movebrickschi.easytool.core.dto.WatermarkParameters;
import io.github.movebrickschi.easytool.core.enums.PositionEnum;
import io.github.movebrickschi.easytool.core.exception.NullException;
import io.github.movebrickschi.easytool.core.utils.file.FileUtil;
import io.github.movebrickschi.easytool.core.utils.ssl.SslUtil;
import io.github.movebrickschi.easytool.core.utils.string.StringUtil;
import io.github.movebrickschi.easytool.core.utils.url.UrlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ImageWatermarkProcessor
 *
 * @author MoveBricks Chi
 */
@Slf4j
public class ImageWatermarkProcessor extends WatermarkProcessor {
    // 支持的图片格式映射
    private static final Map<String, FileTypeConstants.ImagePool> SUFFIX_MAP = new ConcurrentHashMap<>();

    static {
        SUFFIX_MAP.put(FileTypeConstants.ImagePool.JPG.getType(), FileTypeConstants.ImagePool.JPEG);
        SUFFIX_MAP.put(FileTypeConstants.ImagePool.JPEG.getType(), FileTypeConstants.ImagePool.JPEG);
        SUFFIX_MAP.put(FileTypeConstants.ImagePool.PNG.getType(), FileTypeConstants.ImagePool.PNG);
        SUFFIX_MAP.put(FileTypeConstants.ImagePool.BMP.getType(), FileTypeConstants.ImagePool.BMP);
        SUFFIX_MAP.put(FileTypeConstants.ImagePool.GIF.getType(), FileTypeConstants.ImagePool.GIF);

        // 强制禁用SSL证书验证
        SslUtil.disableSSLCertificateValidation();
    }

    /**
     * 普通增加水印
     * @param bufferedImage
     * @param watermarkParameters
     * @return
     */
    private BufferedImage addWatermark(BufferedImage bufferedImage, WatermarkParameters watermarkParameters) {
        int imgWidth = bufferedImage.getWidth();
        int imgHeight = bufferedImage.getHeight();
        Integer size = watermarkParameters.getSize();

        // 如果使用默认大小，可以根据图片大小进行动态调整
        if (size == 40) {
            int minDimension = Math.min(imgWidth, imgHeight);
            // 根据图片大小调整文字大小，确保在小图上不会太大，在大图上不会太小
            size = Math.max(20, Math.min(80, minDimension / 20));
        }
        BufferedImage bufImg = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics = bufImg.createGraphics();
        graphics.drawImage(bufferedImage, 0, 0, imgWidth, imgHeight, null);

        // 设置支持中文的字体
        Font font = WatermarkUtil.getSupportedFont(watermarkParameters.getFontName(), size);
//        graphics.setColor(new Color(0, 0, 0, alpha));
//        graphics.setFont(font);


        FontMetrics fontMetrics = graphics.getFontMetrics();
        int textWidth = StringUtil.width(watermarkParameters.getText(), size);
        int textHeight = fontMetrics.getHeight();

        // 根据位置参数计算水印坐标
        int x = 0;
        int y = 0;
        String text = watermarkParameters.getText();
        switch (PositionEnum.getPosition(watermarkParameters.getPosition())) {
            case CENTER:
                log.info("设置水印位置为居中");
                x = (imgWidth - textWidth) / 2;
                // 垂直居中调整
                y = imgHeight / 2 + textHeight / 4;
                break;
            case TOP_LEFT:
                log.info("设置水印位置为左上角");
                // 左边距
                x = 10;
                // 上边距
                y = textHeight;
                break;
            case TOP_RIGHT:
                log.info("设置水印位置为右上角");
                // 右边距
                x = imgWidth - textWidth - 10;
                // 上边距
                y = textHeight;
                break;
            case BOTTOM_LEFT:
                log.info("设置水印位置为左下角");
                // 左边距
                x = 10;
                // 下边距
                y = imgHeight - 10;
                break;
            case BOTTOM_RIGHT:
                log.info("设置水印位置为右下角");
                // 右边距
                x = imgWidth - textWidth - 10;
                // 下边距
                y = imgHeight - 10;
                break;
            default:
                log.warn("未知的位置参数: {}, 使用默认右下角位置", watermarkParameters.getPosition());
                // 右边距
                x = imgWidth - textWidth - 10;
                // 下边距
                y = imgHeight - 10;
        }
        drawTextWithOutline(graphics, text, x, y, watermarkParameters.getAlpha(), font);
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
     * 转为Base64编码
     * @param bufferedImage
     * @param suffix
     * @param watermarkParameters
     * @return
     * @throws Exception
     */
    private String convert2Base64(BufferedImage bufferedImage, String suffix,
                                  WatermarkParameters watermarkParameters) throws Exception {
        BufferedImage watermarkedImage = addWatermark(bufferedImage, watermarkParameters);
        // 直接转换图片为字节数组
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //原始文件的格式
        ImageIO.write(watermarkedImage, suffix, baos);
        byte[] imageBytes = baos.toByteArray();
        // 将图片转换为Base64编码
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    @Override
    public String addWatermark(File file, WatermarkParameters watermarkParameters) throws Exception {
        BufferedImage processedImage = ImageIO.read(Files.newInputStream(file.toPath()));
        String extension = FileUtil.extension(file.getName());
        return convert2Base64(processedImage, extension, watermarkParameters);
    }

    @Override
    public String addWatermark(String url, WatermarkParameters watermarkParameters) throws Exception {
        URL imageUrl = new URL(url);
        BufferedImage processedImage = ImageIO.read(imageUrl);
        return convert2Base64(processedImage, UrlUtil.extractFileName(url), watermarkParameters);
    }

    @Override
    public String addWatermarkKeepMetadata(File file, WatermarkParameters watermarkParameters) throws Exception {
        log.info("开始为图片添加文字水印并保留元数据，水印参数为：{}", JSONUtil.toJsonStr(watermarkParameters));

        if (CharSequenceUtil.isBlank(watermarkParameters.getText())) {
            throw new NullException("水印内容不能为空！");
        }

        // 读取原始图片及其元数据
        byte[] originalImageBytes = Files.readAllBytes(file.toPath());
        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(originalImageBytes));
        ImageMetadata originalMetadata = Imaging.getMetadata(originalImageBytes);

        // 添加水印
        BufferedImage watermarkedImage = addWatermark(originalImage, watermarkParameters);

        // 将添加水印后的图片写入字节数组，并保留原始元数据
        String suffix =
                SUFFIX_MAP.getOrDefault(UrlUtil.suffix(file.getName()), FileTypeConstants.ImagePool.PNG).getType();
        byte[] watermarkedImageBytes = writeImageWithMetadata(watermarkedImage, suffix, originalMetadata,
                originalImageBytes);

        // 将图片转换为Base64编码
        return Base64.getEncoder().encodeToString(watermarkedImageBytes);
    }

    @Override
    public String addWatermarkKeepMetadata(String url, WatermarkParameters watermarkParameters) throws Exception {
        log.info("开始为图片添加文字水印并保留元数据，图片为：{}，水印参数为：{}", url, JSONUtil.toJsonStr(watermarkParameters));

        if (CharSequenceUtil.isBlank(watermarkParameters.getText())) {
            throw new NullException("水印内容不能为空！");
        }

        // 从URL读取图片
        URL imageUrl = new URL(url);
        byte[] originalImageBytes;
        try (InputStream inputStream = imageUrl.openStream()) {
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
        BufferedImage watermarkedImage = addWatermark(originalImage, watermarkParameters);

        // 将添加水印后的图片写入字节数组，并保留原始元数据
        String suffix =
                SUFFIX_MAP.getOrDefault(UrlUtil.extractFileName(url), FileTypeConstants.ImagePool.PNG).getType();
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
        if ((FileTypeConstants.ImagePool.JPEG.getType().equalsIgnoreCase(format)
                || FileTypeConstants.ImagePool.JPG.getType().equalsIgnoreCase(format)) && originalMetadata != null) {
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
        else if (FileTypeConstants.ImagePool.PNG.getType().equalsIgnoreCase(format)) {
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
