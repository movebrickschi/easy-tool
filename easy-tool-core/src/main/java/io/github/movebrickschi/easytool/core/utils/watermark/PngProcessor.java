package io.github.movebrickschi.easytool.core.utils.watermark;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.ImageLineInt;
import ar.com.hjg.pngj.PngReader;
import ar.com.hjg.pngj.PngWriter;
import ar.com.hjg.pngj.chunks.PngChunk;
import ar.com.hjg.pngj.chunks.PngChunkTextVar;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * PNG图片元数据处理类
 *
 * @author MoveBricks Chi
 * @since 1.0
 */
public final class PngProcessor {

    private PngProcessor() {
    }

    /* ===== 1. 读取原始 PNG 的所有文本块 ===== */
    public static List<PngChunkTextVar> readTextChunks(byte[] pngBytes) {
        PngReader pr = new PngReader(new ByteArrayInputStream(pngBytes));
        List<? extends PngChunk> chunks = pr.getChunksList().getById("tEXt");
        pr.close();
        return (List<PngChunkTextVar>) chunks;
    }

    /* ===== 2. 带水印 + 保留文本块 写出 PNG ===== */
    public static byte[] writePngWithWatermarkAndText(byte[] originalBytes,
                                                      BufferedImage watermarked,
                                                      List<PngChunkTextVar> textChunks) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // 1. 先读原始信息
        PngReader pr = new PngReader(new ByteArrayInputStream(originalBytes));
        ImageInfo imInfo = pr.imgInfo;

        // 2. 构造写出器
        PngWriter pw = new PngWriter(out, imInfo);

        // 3. 复制除文本块外的所有块，让PNGJ库自动处理顺序
        //    先复制非文本块，确保它们在IDAT之前
        pw.copyChunksFrom(pr.getChunksList(),
                chunk -> !"tEXt".equals(chunk.id) &&
                        !"iTXt".equals(chunk.id) &&
                        !"zTXt".equals(chunk.id));

        // 4. 把原文本chunks写回（在图像数据之前）
        //    这样确保文本块在IDAT之前写入
        if (textChunks != null) {
            for (PngChunkTextVar tc : textChunks) {
                String key = tc.getKey();
                String val = tc.getVal();
                PngChunkTextVar newChunk = pw.getMetadata().setText(key, val);
                // 设置优先级为true，确保文本块在IDAT之前写入,否则元数据会多出一个Warning
                newChunk.setPriority(true);
            }
        }

        // 5. 写图像数据
        // 使用最简单直接的方式处理图像数据
        int channels = imInfo.channels;
        for (int row = 0; row < imInfo.rows; row++) {
            // 获取整行像素数据
            int[] rgb = watermarked.getRGB(0, row, imInfo.cols, 1, null, 0, imInfo.cols);

            // 创建ImageLineInt对象
            ImageLineInt line = new ImageLineInt(imInfo);
            int[] scanline = line.getScanline();

            // 根据PNG图像的通道数处理像素数据
            // RGB
            if (channels == 3) {
                for (int col = 0, i = 0; col < imInfo.cols; col++, i += 3) {
                    int pixel = rgb[col];
                    // Red
                    scanline[i] = (pixel >> 16) & 0xFF;
                    // Green
                    scanline[i + 1] = (pixel >> 8) & 0xFF;
                    // Blue
                    scanline[i + 2] = pixel & 0xFF;
                }
            }
            // RGBA
            else if (channels == 4) {
                for (int col = 0, i = 0; col < imInfo.cols; col++, i += 4) {
                    int pixel = rgb[col];
                    // Red
                    scanline[i] = (pixel >> 16) & 0xFF;
                    // Green
                    scanline[i + 1] = (pixel >> 8) & 0xFF;
                    // Blue
                    scanline[i + 2] = pixel & 0xFF;
                    // Alpha
                    scanline[i + 3] = (pixel >> 24) & 0xFF;
                }
            }
            // Grayscale
            else if (channels == 1) {
                for (int col = 0; col < imInfo.cols; col++) {
                    int pixel = rgb[col];
                    int gray = (int) (0.299 * ((pixel >> 16) & 0xFF) +
                            0.587 * ((pixel >> 8) & 0xFF) +
                            0.114 * (pixel & 0xFF));
                    scanline[col] = gray;
                }
            }
            pw.writeRow(line);
        }
        pr.close();
        pw.end();
        return out.toByteArray();
    }

}


