package io.github.movebrickschi.easytool.core.utils.image;

import io.github.movebrickschi.easytool.core.exception.NullException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 图片工具类
 *
 * @author MoveBricks Chi
 * @since 1.0
 */
public final class ImageUtil {
    private ImageUtil() {
    }

    /**
     * 根据传入图片文件平均分割生成传入数量的图片集合
     * 前提：源图片的宽高必须为分割数量的平方根的整数倍
     *
     * @param sourceFile 源图片文件
     * @param count 分割数量(必须是完全平方数,如4、9、16等)
     * @return 分割后的图片集合
     * @throws IOException 读取文件失败
     * @throws IllegalArgumentException count不是完全平方数或小于1
     */
    public static List<BufferedImage> splitImage(File sourceFile, int count) throws IOException {
        BufferedImage sourceImage = ImageIO.read(sourceFile);
        if (sourceImage == null) {
            throw new IOException("无法读取图片文件");
        }
        return splitImage(sourceImage, count);
    }


    /**
     * 根据传入图片对象平均分割生成传入数量的图片集合
     * 前提：源图片的宽高必须为分割数量的平方根的整数倍
     *
     * @param bufferedImage 源图片对象
     * @param count 分割数量(必须是完全平方数,如4、9、16等)
     * @return 分割后的图片集合
     * @throws IOException 读取文件失败
     * @throws IllegalArgumentException count不是完全平方数或小于1
     */
    public static List<BufferedImage> splitImage(BufferedImage bufferedImage, int count) throws IOException {
        if (Objects.isNull(bufferedImage)) {
            throw new NullException("图片对象不能为空");
        }
        if (count < 1) {
            throw new IllegalArgumentException("分割数量必须大于0");
        }

        int rowCol = (int) Math.sqrt(count);
        if (rowCol * rowCol != count) {
            throw new IllegalArgumentException("分割数量必须是完全平方数,如4、9、16等");
        }

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int subWidth = width / rowCol;
        int subHeight = height / rowCol;

        List<BufferedImage> result = new ArrayList<>(count);

        for (int row = 0; row < rowCol; row++) {
            for (int col = 0; col < rowCol; col++) {
                BufferedImage subImage = bufferedImage.getSubimage(col * subWidth, row * subHeight, subWidth, subHeight);
                result.add(subImage);
            }
        }
        return result;
    }

    /**
     * 根据传入图片URL平均分割生成传入数量的图片集合
     * 前提：源图片的宽高必须为分割数量的平方根的整数倍
     *
     * @param url 源图片URL
     * @param count 分割数量(必须是完全平方数,如4、9、16等)
     * @return 分割后的图片集合
     * @throws IOException 读取文件失败
     * @throws IllegalArgumentException count不是完全平方数或小于1
     */
    public static List<BufferedImage> splitImage(String url, int count) throws IOException {
        BufferedImage sourceImage = ImageIO.read(new URL(url));
        if (sourceImage == null) {
            throw new IOException("无法读取图片文件");
        }
        return splitImage(sourceImage, count);
    }


//    public static void main(String[] args) throws IOException {
//        File sourceFile = new File("C:\\Users\\Administrator\\Downloads\\b53d2e19123347a5bcb1a0bf5b966dd4.png");
//        List<BufferedImage> bufferedImages = splitImage(sourceFile, 4);
//        for (int i = 0; i < bufferedImages.size(); i++) {
//            ImageIO.write(bufferedImages.get(i), "png", new File("C:\\Users\\Administrator\\Downloads\\" + i + "
//            .png"));
//        }
//    }

}
