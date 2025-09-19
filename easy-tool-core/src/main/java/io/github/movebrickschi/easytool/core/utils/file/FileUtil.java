package io.github.movebrickschi.easytool.core.utils.file;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * 输入流转文件工具类
 *
 * @author MoveBricks Chi
 * @since 1.0
 */
@Slf4j
public final class FileUtil {

    private FileUtil() {
    }

    /**
     * 使用FileOutputStream和缓冲区
     * @param inputStream 输入流
     * @param filePath 输出文件路径
     * @throws IOException IO异常
     */
    public static void toFileByOutputStream(InputStream inputStream, String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    /**
     * Files.copy实现
     * @param inputStream 输入流
     * @param filePath 输出文件路径
     * @throws IOException IO异常
     */
    public static void toFileByCopy(InputStream inputStream, String filePath) throws IOException {
        Path targetPath = Paths.get(filePath);
        Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * 使用FileChannel (适用于大文件)
     * @param inputStream 输入流
     * @param filePath 输出文件路径
     * @throws IOException IO异常
     */
    public static void toFileByFileChannel(InputStream inputStream, String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath);
             FileChannel fileChannel = fos.getChannel()) {
            ReadableByteChannel readableByteChannel = Channels.newChannel(inputStream);
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    /**
     * 获取文件后缀名
     * @param fileName 文件名
     * @return 文件后缀名
     */
    public static String extension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        }
        return null;
    }

    /**
     * 下载文件
     * @param url 网络地址
     * @param file 存储的文件
     */
    public static void downloadFile(String url, File file) {
        log.info("开始下载网络文件");
        try (ReadableByteChannel rbc = Channels.newChannel(new URL(url).openStream());
             FileOutputStream fos = new FileOutputStream(file)) {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
