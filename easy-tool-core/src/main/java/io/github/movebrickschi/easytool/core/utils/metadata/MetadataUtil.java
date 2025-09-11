package io.github.movebrickschi.easytool.core.utils.metadata;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;
import io.github.movebrickschi.easytool.core.constants.ImagePool;
import io.github.movebrickschi.easytool.core.dto.ImplicitMetadata;
import io.github.movebrickschi.easytool.core.exception.NullException;
import io.github.movebrickschi.easytool.core.utils.file.InputStreamToFileUtil;
import io.github.movebrickschi.easytool.core.utils.ssl.SslUtil;
import io.github.movebrickschi.easytool.core.utils.url.UrlUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 元数据工具类
 *
 * @author Liu Chunchi
 */
@Slf4j
public final class MetadataUtil {

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


    /**
     * 写入元数据到图片
     * @param file 图片文件
     * @param implicitMetadata 元数据
     * @return 处理后的图片字节数组
     * @throws IOException 读取图片或处理图片时发生错误
     */
    public static byte[] writeToImage(File file, ImplicitMetadata implicitMetadata) throws IOException {
        BufferedImage originalImage = ImageIO.read(Files.newInputStream(file.toPath()));
        String implicitMetadataContent = getImplicitMetadata(implicitMetadata);
        log.info("开始写入元数据到图片，key: {},metadata:{}", implicitMetadata.getKey(), implicitMetadataContent);
        String suffix = SUFFIX_MAP.getOrDefault(InputStreamToFileUtil.extension(file.getName()), ImagePool.PNG);
        return Optional.ofNullable(IMAGE_TRANSFER_FUNCTION.get().get(suffix))
                .orElseThrow(() -> new UnsupportedOperationException("暂不支持"))
                .apply(ImageTransfer.builder()
                        .bufferedImage(originalImage)
                        .key(implicitMetadata.getKey())
                        .suffix(suffix)
                        .content(implicitMetadataContent)
                        .build());
    }

    /**
     * 写入元数据到图片
     * @param imageUrl 图片链接
     * @param implicitMetadata 元数据
     * @return 处理后的图片字节数组
     * @throws IOException 读取图片或处理图片时发生错误
     */
    public static byte[] writeToImage(String imageUrl, ImplicitMetadata implicitMetadata) throws IOException {
        // 从URL读取图片
        URL url = new URL(imageUrl);
        BufferedImage originalImage = ImageIO.read(url);
        String implicitMetadataContent = getImplicitMetadata(implicitMetadata);
        log.info("开始写入元数据到图片，key: {},metadata:{}", implicitMetadata.getKey(), implicitMetadataContent);
        String suffix = SUFFIX_MAP.getOrDefault(UrlUtil.extractFileName(imageUrl), ImagePool.PNG);
        return Optional.ofNullable(IMAGE_TRANSFER_FUNCTION.get().get(suffix))
                .orElseThrow(() -> new UnsupportedOperationException("暂不支持"))
                .apply(ImageTransfer.builder()
                        .bufferedImage(originalImage)
                        .key(implicitMetadata.getKey())
                        .suffix(suffix)
                        .content(implicitMetadataContent)
                        .build());
    }


    static Function<ImageTransfer, byte[]> jpg = imageTransfer -> {
        ByteArrayOutputStream imageBaos = new ByteArrayOutputStream();
        try {
            ImageIO.write(imageTransfer.getBufferedImage(), imageTransfer.getSuffix(), imageBaos);
            byte[] imageBytes = imageBaos.toByteArray();
            byte[] processedBytes = updateJPEGMetadataWithCommonsImaging(imageBytes,
                    imageTransfer.getKey(),
                    imageTransfer.getContent());
            if (processedBytes != null) {
                log.info("成功处理JPEG元数据");
                return processedBytes;
            }
        } catch (Exception e) {
            log.error("使用处理JPEG元数据时出错: {}", e.getMessage());
        }
        return new byte[0];
    };

    static Function<ImageTransfer, byte[]> png = imageTransfer -> {
        try {
            // 获取PNG ImageWriter
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("png");
            if (!writers.hasNext()) {
                throw new RuntimeException("未找到PNG格式的ImageWriter");
            }

            ImageWriter writer = writers.next();
            ImageWriteParam writeParam = writer.getDefaultWriteParam();

            // 获取默认的元数据
            IIOMetadata metadata = writer.getDefaultImageMetadata(
                    ImageTypeSpecifier.createFromBufferedImageType(imageTransfer.getBufferedImage().getType()),
                    writeParam
            );

            // 更新PNG元数据
            if (metadata != null) {
                updatePNGMetadata(metadata, imageTransfer.getKey(), imageTransfer.getContent());
            }

            // 将图片和元数据写入输出流
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ImageOutputStream output = new MemoryCacheImageOutputStream(baos)) {
                writer.setOutput(output);
                writer.write(null, new javax.imageio.IIOImage(imageTransfer.getBufferedImage(), null, metadata),
                        writeParam);
            } finally {
                writer.dispose();
            }

            log.info("PNG元数据写入完成");
            return baos.toByteArray();
        } catch (Exception e) {
            log.warn("处理PNG元数据时出错: {}", e.getMessage());
        }
        return new byte[0];
    };

    static Supplier<Map<String, Function<ImageTransfer, byte[]>>> IMAGE_TRANSFER_FUNCTION = () -> {
        Map<String, Function<ImageTransfer, byte[]>> map = Maps.newHashMap();
        map.put(ImagePool.JPG, jpg);
        map.put(ImagePool.JPEG, jpg);
        map.put(ImagePool.PNG, png);
        return map;
    };


    /**
     * 更新PNG格式的元数据
     */
    private static IIOMetadata updatePNGMetadata(IIOMetadata metadata, String key, String content) {
        try {
            String nativeMetadataFormatName = metadata.getNativeMetadataFormatName();
            if (nativeMetadataFormatName == null) {
                return metadata;
            }

            IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(nativeMetadataFormatName);

            // 查找或创建tEXt chunk
            IIOMetadataNode textNode = findNode(root, "tEXt");
            if (textNode == null) {
                textNode = new IIOMetadataNode("tEXt");
                root.appendChild(textNode);
            }

            // 添加创建者信息
            IIOMetadataNode creatorNode = new IIOMetadataNode("tEXtEntry");
            creatorNode.setAttribute("keyword", key);
            creatorNode.setAttribute("value", content);
            textNode.appendChild(creatorNode);


            // 将修改后的元数据合并回去
            metadata.mergeTree(nativeMetadataFormatName, root);

            log.info("已更新PNG元数据，{}: {}", key, content);
            return metadata;
        } catch (Exception e) {
            log.warn("更新PNG元数据失败: {}", e.getMessage());
            return metadata;
        }
    }

    /**
     * 查找指定名称的节点
     */
    private static IIOMetadataNode findNode(IIOMetadataNode root, String nodeName) {
        NodeList childNodes = root.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getNodeName().equals(nodeName)) {
                return (IIOMetadataNode) node;
            }
        }
        return null;
    }


    /**
     * 给网络地址视频文件写入隐式元数据
     * @param videoUrl 视频地址
     * @param implicitMetadata 元数据信息
     * @param ffmpegPath ffmpeg路径,默认为本地
     * @return 文件字节
     */
    public static byte[] writeToVideo(String videoUrl, ImplicitMetadata implicitMetadata, String... ffmpegPath) {
        log.info("给网络地址视频文件写入隐式元数据");
        File inputTempFile = null;
        Snowflake snowflake = new Snowflake();
        try {
            inputTempFile = new File(snowflake.nextIdStr() + ".mp4");
            InputStreamToFileUtil.downloadFile(videoUrl, inputTempFile);
            return writeToVideo(inputTempFile, implicitMetadata, ffmpegPath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            //删除临时文件
            if (inputTempFile != null && inputTempFile.exists()) {
                inputTempFile.delete();
            }
        }

    }


    /**
     * 给指定文件设置隐式元数据
     * @param file 本地视频文件
     * @param implicitMetadata 元数据信息
     * @param ffmpegPath ffmpeg路径,默认为本地
     * @return 文件字节
     */
    public static byte[] writeToVideo(File file, ImplicitMetadata implicitMetadata, String... ffmpegPath) throws ExecutionException {
        String implicitMetadataContent = getImplicitMetadata(implicitMetadata);
        Snowflake snowflake = new Snowflake();
        File outTempFile = new File(snowflake.nextIdStr() + ".mp4");
        addMetadataToVideo(file.getAbsolutePath(), outTempFile.getAbsolutePath(),
                implicitMetadata.getKey(), implicitMetadataContent, ffmpegPath);
        // 读取处理后的文件并返回字节数组,finally删除临时文件不影响
        try {
            return Files.readAllBytes(outTempFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (outTempFile != null && outTempFile.exists()) {
                outTempFile.delete();
            }
        }
    }

    /**
     * 使用ffmpeg为视频添加元数据
     *
     * @param ffmpegPath ffmpeg可执行文件路径
     * @param inputPath 输入视频路径
     * @param outputPath 输出视频路径
     * @param key 元数据key
     * @param metadata 元数据JSON字符串
     * @return 是否成功
     */
    private static void addMetadataToVideo(String inputPath, String outputPath, String key,
                                           String metadata, String... ffmpegPath) throws ExecutionException {
        String ffmpegPathParam = "ffmpeg";
        if (ArrayUtil.isNotEmpty(ffmpegPath)) {
            ffmpegPathParam = ffmpegPath[0];
        }
        try {
            // 构建ffmpeg命令
            List<String> command = new ArrayList<>();
            command.add(ffmpegPathParam);
            command.add("-i");
            command.add(inputPath);
            command.add("-metadata");
            command.add(key + "=" + metadata);
            command.add("-movflags");
            command.add("use_metadata_tags");
            command.add("-c");
            command.add("copy");
            command.add(outputPath);

            log.info("开始执行ffmpeg命令:{}", command);

            ProcessBuilder processBuilder = new ProcessBuilder(command);

            // 执行命令
            Process process = processBuilder.start();
            // 等待5分钟
            boolean finished = process.waitFor(300, TimeUnit.SECONDS);

            if (finished && process.exitValue() == 0) {
                log.info("视频元数据添加成功");
            } else {
                log.error("ffmpeg执行失败，退出码: " + process.exitValue());
            }
        } catch (IOException | InterruptedException e) {
            log.error("执行ffmpeg命令时发生错误: " + e.getMessage());
            throw new ExecutionException("执行ffmpeg命令时发生错误", e);
        }

    }

    /**
     * 获取AIGC隐式元数据
     * @param implicitMetadata 元数据参数
     * @return 隐式元数据
     */
    public static String getImplicitMetadata(ImplicitMetadata implicitMetadata) {
        String code = implicitMetadata.getContentProducer();
        if (CharSequenceUtil.isBlank(code)) {
            if (Objects.isNull(implicitMetadata.getProducerInfo())) {
                throw new NullException("服务提供者编码不能为空！");
            } else {
                code = contentProducer(implicitMetadata.getProducerInfo());
            }
        }
        if (CharSequenceUtil.isBlank(implicitMetadata.getContentPropagator())) {
            implicitMetadata.setContentPropagator(code);
        }
        log.info("开始组合隐式元数据");
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode metadataNode = mapper.createObjectNode();
        metadataNode.put("\"Label\"", "\"" + implicitMetadata.getLabel() + "\"");
        metadataNode.put("\"ContentProducer\"", "\"" + code + "\"");
        metadataNode.put("\"ProduceID\"", "\"" + implicitMetadata.getProduceId() + "\"");
        metadataNode.put("\"ContentPropagator\"", "\"" + implicitMetadata.getContentPropagator() + "\"");
        metadataNode.put("\"PropagateID\"", "\"" + implicitMetadata.getPropagateId() + "\"");
        if (CharSequenceUtil.isNotBlank(implicitMetadata.getReservedCode1())) {
            metadataNode.put("\"ReservedCode1\"", "\"" + implicitMetadata.getReservedCode1() + "\"");
        }
        if (CharSequenceUtil.isNotBlank(implicitMetadata.getReservedCode2())) {
            metadataNode.put("\"ReservedCode2\"", "\"" + implicitMetadata.getReservedCode2() + "\"");
        }
        try {
            return mapper.writeValueAsString(metadataNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取服务提供者编码
     * @param producerInfo 提供者信息
     * @return 提供者编码
     */
    private static String contentProducer(ImplicitMetadata.ProducerInfo producerInfo) {
        log.info("开始组合主体编号");
        //第1-2位,编码规则版本
        String finalCode = producerInfo.getVersion();
        //第3位,主体类型（组织=1）
        finalCode = finalCode.concat(producerInfo.getSubjectType());
        //第4位,绑定方式（统一社会信用代码=1）
        finalCode = finalCode.concat(producerInfo.getBindingWay());
        //第5-22位,统一社会信用代码
        finalCode = finalCode.concat(producerInfo.getSubjectCode());
        //第23-27位,服务扩展码,不设置默认00000
        finalCode = finalCode.concat(producerInfo.getServiceExtensionCode());
        return finalCode;
    }

    /**
     * 使用Apache Commons Imaging更新JPEG元数据，按照规范将AIGC信息写入UserComment字段
     */
    private static byte[] updateJPEGMetadataWithCommonsImaging(byte[] imageBytes, String key, String metadataContent) {
        try {
            // 使用Apache Commons Imaging处理JPEG元数据
            ImageMetadata metadata = Imaging.getMetadata(imageBytes);

            TiffOutputSet outputSet = getTiffOutputSet(metadata);

            // 构建符合规范的完整JSON格式
            String json = "{\"" + key + "\":" + metadataContent.replace("\\\"", "") + "}";

            // 获取或创建EXIF目录
            TiffOutputDirectory exifDirectory = outputSet.getOrCreateExifDirectory();

            // 移除已存在的UserComment字段
            exifDirectory.removeField(ExifTagConstants.EXIF_TAG_USER_COMMENT);

            // 按规范将完整的元数据信息写入UserComment字段
            exifDirectory.add(ExifTagConstants.EXIF_TAG_USER_COMMENT, json);

            // 写入新的元数据
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            new ExifRewriter().updateExifMetadataLossless(imageBytes, outputStream, outputSet);

            log.info("成功更新JPEG元数据，UserComment: {}", json);
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.warn("更新JPEG元数据失败: {}", e.getMessage(), e);
            // 如果失败，返回原始图片
            return imageBytes;
        }
    }

    private static TiffOutputSet getTiffOutputSet(ImageMetadata metadata) throws ImageWriteException {
        TiffOutputSet outputSet = null;
        if (metadata instanceof JpegImageMetadata) {
            JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
            TiffImageMetadata exif = jpegMetadata.getExif();
            if (exif != null) {
                outputSet = exif.getOutputSet();
            }
        }

        // 如果没有EXIF数据，创建一个新的TiffOutputSet
        if (outputSet == null) {
            outputSet = new TiffOutputSet();
        }
        return outputSet;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    private static class ImageTransfer {
        private ImplicitMetadata implicitMetadata;

        private BufferedImage bufferedImage;

        private String suffix;

        private String key;

        private String content;
    }

//    public static void main(String[] args) {
//        File file = new File("C:\\Users\\Administrator\\Downloads\\1757424037728huizhi_F8560F8F-31CA-4DD6-B401" +
//                "-CF98CB29065D.mp4");
//        ImplicitMetadata aigc = ImplicitMetadata.builder()
//                .producerInfo(ImplicitMetadata.ProducerInfo.builder()
//                        .subjectCode("91320115MA236KWQ79")
//                        .build())
//                .key("AIGC")
//                .produceId("2343242")
//                .propagateId("2343242")
//                .build();
//        try {
//            byte[] bytes = writeToVideo(file, aigc);
//
//            File out = new File("C:\\Users\\Administrator\\Downloads\\1757424037728huizhi_F8560F8F-31CA-4DD6-B401" +
//                    "-123.mp4");
//            ByteUtil.toFile(bytes, out.getAbsolutePath());
//            File outImage = new File("C:\\Users\\Administrator\\Downloads\\33333.jpg");
//            File file1 = new File("C:\\Users\\Administrator\\Downloads\\writemetada.jpg");
//            byte[] bytes = writeToImage(file1, aigc);
//            ByteUtil.toFile(bytes, outImage.getAbsolutePath());
//
//
//            String base64 = WatermarkUtil.forImageKeepMetadata(outImage, WatermarkParameters.builder()
//                    .text("AI生成")
//                    .build());
//            File outWaterImage = new File("C:\\Users\\Administrator\\Downloads\\555555555.jpg");
//            Base64Util.toFile(base64, outWaterImage.getAbsolutePath());
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//    }
}
