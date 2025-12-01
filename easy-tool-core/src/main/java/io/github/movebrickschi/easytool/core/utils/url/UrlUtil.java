package io.github.movebrickschi.easytool.core.utils.url;

import lombok.extern.slf4j.Slf4j;

import java.net.URL;

/**
 * url工具类
 *
 * @author MoveBricks Chi
 * @since 1.0
 */
@Slf4j
public final class UrlUtil {

    private UrlUtil() {
        throw new IllegalStateException("Utility class");
    }


    /**
     * 从URL中提取文件名
     *
     * @param url 图片URL
     * @return 文件名
     */
    public static String extractFileName(String url) {
        try {
            String path = new URL(url).getPath();
            int lastSlashIndex = path.lastIndexOf('/');
            if (lastSlashIndex >= 0 && lastSlashIndex < path.length() - 1) {
                return path.substring(lastSlashIndex + 1);
            }
        } catch (Exception e) {
            log.warn("从URL提取文件名失败: {}", e.getMessage());
        }
        // 默认文件名
        return "image";
    }

}
