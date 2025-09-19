package io.github.movebrickschi.easytool.core.utils.url;

import cn.hutool.core.text.StrPool;
import io.github.movebrickschi.easytool.core.constants.FileTypeConstants;
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
        return FileTypeConstants.IMAGE;
    }

    /**
     * 从URL中提取文件后缀
     *
     * @param url 图片URL
     * @return 文件名后缀
     */
    public static String suffix(String url) {
        try {
            if (url.contains(StrPool.DOT)) {
                return url.substring(url.lastIndexOf(StrPool.DOT) + 1);
            }
        } catch (Exception e) {
            log.warn("从URL提取文件后缀失败: {}", e.getMessage());
        }
        return null;
    }

}
