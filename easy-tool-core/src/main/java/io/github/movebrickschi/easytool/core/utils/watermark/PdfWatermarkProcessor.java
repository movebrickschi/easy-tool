package io.github.movebrickschi.easytool.core.utils.watermark;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import io.github.movebrickschi.easytool.core.dto.WatermarkParameters;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.util.Base64;

/**
 * PDF水印处理器
 *
 * @author MoveBricks Chi
 * @since 1.0
 */
public class PdfWatermarkProcessor extends WatermarkProcessor {
    //指定偏移量
    private static final int INTERVAL = 20;
    @Override
    public String addWatermark(File file, WatermarkParameters watermarkParameters) throws Exception {
        File tempFileTarget = new File(IdUtil.getSnowflakeNextId() + ".pdf");
        try {
            PdfReader reader = new PdfReader(FileUtil.getInputStream(file), "pdf".getBytes());
            PdfStamper stamp = new PdfStamper(reader, Files.newOutputStream(tempFileTarget.toPath()));
            //请注意，字体这边可能会报错，请替换成当前环境下支持的字体
            Rectangle pageRect = null;
            BaseFont base = BaseFont.createFont("ziti.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, BaseFont.EMBEDDED
                    , FileUtil.readBytes("Alibaba-PuHuiTi-Regular.ttf"), null, true);

            PdfGState gs = new PdfGState();
            // 设置水印的填充和描边透明度
            gs.setFillOpacity(0.3f);
            gs.setStrokeOpacity(0.4f);
            int total = reader.getNumberOfPages() + 1;
            JLabel label = new JLabel();
            FontMetrics metrics;
            int textH = 0;
            int textW = 0;
            label.setText(watermarkParameters.getText());
            metrics = label.getFontMetrics(label.getFont());
            textH = metrics.getHeight();
            textW = metrics.stringWidth(label.getText());
            PdfContentByte under;
            // 遍历PDF每一页添加水印
            for (int i = 1; i < total; i++) {
                pageRect = reader.getPageSizeWithRotation(i);
                under = stamp.getOverContent(i);
                under.saveState();
                under.setGState(gs);
                under.beginText();
                under.setFontAndSize(base, watermarkParameters.getSize());
                // 水印文字成30度角倾斜
                //你可以随心所欲的改你自己想要的角度
                // 按照指定间隔在页面上重复添加水印文字
                //interval + textH 和 interval + textW 是水印文字的起始位置偏移量
                // 根据字体大小动态计算水印文字在垂直和水平方向上的间隔

                // 垂直间距为字体大小的6倍
                int verticalSpacing = (int) (watermarkParameters.getSize() * watermarkParameters.getMultiplier());
                // 水平间距为字体大小的6倍
                int horizontalSpacing = (int) (watermarkParameters.getSize() * watermarkParameters.getMultiplier());
                for (int height = INTERVAL + textH; height < pageRect.getHeight();
                     height = height + textH + verticalSpacing) {
                    for (int width = INTERVAL + textW; width < pageRect.getWidth() + textW;
                         width = width + textW + horizontalSpacing) {
                        under.setColorFill(BaseColor.DARK_GRAY);
                        under.showTextAligned(Element.ALIGN_LEFT, watermarkParameters.getText(), width - textW,
                                height - textH, watermarkParameters.getRotation());
                    }
                }
                // 添加水印文字
                under.endText();
            }

            // 关闭
            stamp.close();
            reader.close();
            // 读取处理后的视频文件并转换为Base64字符串
            byte[] videoBytes = Files.readAllBytes(tempFileTarget.toPath());
            String base64String = Base64.getEncoder().encodeToString(videoBytes);

            tempFileTarget.deleteOnExit();
            return base64String;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public String addWatermark(String url, WatermarkParameters watermarkParameters) throws Exception {
        File file = new File("watermarked-pdf" + IdUtil.getSnowflakeNextId() + ".pdf");
        try {
            io.github.movebrickschi.easytool.core.utils.file.FileUtil.downloadFile(url, file);
            return addWatermark(file, watermarkParameters);
        } finally {
            file.deleteOnExit();
        }
    }

    @Override
    public String addWatermarkKeepMetadata(File file, WatermarkParameters watermarkParameters) throws Exception {
        return "";
    }

    @Override
    public String addWatermarkKeepMetadata(String url, WatermarkParameters watermarkParameters) throws Exception {
        return "";
    }
}
