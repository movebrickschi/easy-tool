package io.github.movebrickschi.easytool.core.utils.watermark.factory;


import io.github.movebrickschi.easytool.core.constants.FileTypeConstants;
import io.github.movebrickschi.easytool.core.dto.WatermarkParameters;
import io.github.movebrickschi.easytool.core.utils.watermark.ImageWatermarkProcessor;
import io.github.movebrickschi.easytool.core.utils.watermark.PdfWatermarkProcessor;
import io.github.movebrickschi.easytool.core.utils.watermark.VideoWatermarkProcessor;
import io.github.movebrickschi.easytool.core.utils.watermark.WatermarkProcessor;

import java.io.File;

/**
 * 水印工厂类
 *
 * @author MoveBricks Chi
 * @since 1.0
 */
public class WatermarkProcessorFactory {
    private WatermarkProcessorFactory() {
    }

    public static WatermarkProcessor getProcessor(String fileType) {
        return switch (fileType.toLowerCase()) {
            case FileTypeConstants.IMAGE -> new ImageWatermarkProcessor();
            case FileTypeConstants.VIDEO -> new VideoWatermarkProcessor();
            case FileTypeConstants.PDF -> new PdfWatermarkProcessor();
            default -> new WatermarkProcessor() {
                @Override
                public String addWatermark(File file, WatermarkParameters watermarkParameters) throws Exception {
                    return "";
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
            };
        };
    }
}
