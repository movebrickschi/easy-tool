package io.github.movebrickschi.easytool.core.utils.string;

/**
 * 字符串工具类
 *
 * @author MoveBricks Chi
 * @since 1.0
 */
public final class StringUtil {

    private StringUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 获取字符串的宽度
     * @param str 字符串
     * @param fontSize 字体大小
     * @return 宽度
     */
    public static int width(String str, int fontSize) {
        char[] chars = str.toCharArray();
        int fontSize2 = fontSize / 2;
        int width = 0;
        for (char c : chars) {
            int len = String.valueOf(c).getBytes().length;
            if (len != 1) {
                width += fontSize;
            } else {
                width += fontSize2;
            }
        }
        return width;
    }


}
