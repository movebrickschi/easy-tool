package io.github.movebrickschi.easytool.core.utils.bytes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 字节工具类
 *
 * @author MoveBricks Chi
 * @since 1.0
 */
public final class ByteUtil {
    private ByteUtil() {
    }

    /**
     * 转为文件，推荐使用
     * @param bytes 文件字节数组
     * @param filePath 文件路径
     * @throws IOException 抛出IO异常
     */
    public static File toFile(byte[] bytes, String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Files.write(path, bytes);
        return path.toFile();
    }


}
