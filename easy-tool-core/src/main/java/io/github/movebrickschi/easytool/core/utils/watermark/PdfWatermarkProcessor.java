package io.github.movebrickschi.easytool.core.utils.watermark;

import io.github.movebrickschi.easytool.core.dto.WatermarkParameters;

import java.io.File;


public class PdfWatermarkProcessor extends WatermarkProcessor {
    private static final int interval = 20;

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
}
