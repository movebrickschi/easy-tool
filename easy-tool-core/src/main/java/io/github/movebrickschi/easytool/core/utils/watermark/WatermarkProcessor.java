package io.github.movebrickschi.easytool.core.utils.watermark;

import io.github.movebrickschi.easytool.core.dto.WatermarkParameters;

import java.io.File;

/**
 * 水印处理
 *
 * @author MoveBricks Chi
 * @since 1.0
 */
public abstract class WatermarkProcessor {


    public abstract String addWatermark(File file, WatermarkParameters watermarkParameters) throws Exception;

    public abstract String addWatermark(String url, WatermarkParameters watermarkParameters) throws Exception;

    public abstract String addWatermarkKeepMetadata(File file, WatermarkParameters watermarkParameters) throws Exception;

    public abstract String addWatermarkKeepMetadata(String url, WatermarkParameters watermarkParameters) throws Exception;


}