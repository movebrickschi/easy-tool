package io.github.movebrickschi.easytool.core.utils.base64;

import io.github.movebrickschi.easytool.core.utils.bytes.ByteUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/**
 * Base64Util
 *
 * @author MoveBricks Chi
 * @since 1.0
 */
public final class Base64Util {

    private Base64Util() {
    }

    /**
     * base64 转 InputStream
     * @param base64 base64
     * @return InputStream
     */
    public static InputStream toInputStream(String base64) {
        if (base64.contains(",")) {
            base64 = base64.split(",")[1];
        }
        byte[] decode = Base64.getDecoder().decode(base64);
        return new ByteArrayInputStream(decode);
    }

    /**
     * 将带有数据URI前缀的Base64字符串转换为文件
     *
     * @param base64String 带有数据URI前缀的Base64编码字符串
     * @param filePath 文件路径
     * @return 生成的文件
     * @throws IOException IO异常
     */
    public static File toFile(String base64String, String filePath) throws IOException {
        // 移除数据URI前缀（如data:image/png;base64,）
        String base64Data = base64String;
        if (base64String.contains(",")) {
            base64Data = base64String.split(",")[1];
        }
        // 解码Base64字符串为字节数组
        byte[] decodedBytes = Base64.getDecoder().decode(base64Data);

        return ByteUtil.toFile(decodedBytes, filePath);
    }

}
