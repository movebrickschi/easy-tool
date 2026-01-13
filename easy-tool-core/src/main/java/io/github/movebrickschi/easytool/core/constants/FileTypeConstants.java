package io.github.movebrickschi.easytool.core.constants;

import io.github.movebrickschi.easytool.core.exception.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 文件类型常量
 *
 * @author MoveBricks Chi
 * @since 1.0
 */
public final class FileTypeConstants {

    public static final String IMAGE = "image";
    public static final String VIDEO = "video";
    public static final String UNKNOWN = "unknown";
    public static final String PDF = "pdf";

    /**
     * 视频常量池
     *
     * @author MoveBricks Chi
     * @since 1.0
     */
    @Getter
    @AllArgsConstructor
    public enum VideoPool {
        MP4("mp4"),
        FLV("flv"),
        MKV("mkv"),
        WMV("wmv"),
        AVI("avi"),
        MOV("mov"),
        ;

        private final String type;

        public static VideoPool getVideoPool(String type) {
            return Arrays.stream(VideoPool.values()).filter(item -> type.equalsIgnoreCase(item.getType()))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("未知类型"));
        }


    }


    /**
     * 图片常量池
     *
     * @author MoveBricks Chi
     * @since 1.0
     */
    @Getter
    @AllArgsConstructor
    public enum ImagePool {

        JPG("jpg"),
        JPEG("jpeg"),
        PNG("png"),
        BMP("bmp"),
        GIF("gif");


        private final String type;

        public static ImagePool getImagePool(String type) {
            return Arrays.stream(ImagePool.values())
                    .filter(item -> type.equalsIgnoreCase(item.getType()))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("图片格式不存在"));
        }


    }


}
