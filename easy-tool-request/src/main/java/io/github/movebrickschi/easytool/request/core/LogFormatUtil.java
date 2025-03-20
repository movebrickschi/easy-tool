package io.github.movebrickschi.easytool.request.core;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;

import java.io.Serializable;

/**
 * LogFormatUtil
 *
 * @author MoveBricks Chi
 * @version 1.0
 * @since 3.0.0
 */
public final class LogFormatUtil implements Serializable {

    /**
     * 打印截取前缀日志
     * @param isPrint 是否打印
     * @param content 打印内容
     * @param maxLength 打印长度，小于等于0则不截取
     * @return 截取后的日志
     */
    public static String printSubPre(boolean isPrint, Object content, int maxLength) {
        if (!isPrint) {
            return "";
        }
        if (String.class.equals(content.getClass())) {
            return subPre(content.toString(), maxLength);
        }
        return subPre(content, maxLength);
    }

    /**
     * 截取前缀日志
     * @param content 打印内容
     * @param maxLength 打印长度，小于等于0则不截取
     * @return 截取后的日志
     */
    public static String subPre(Object content, int maxLength) {
        if (maxLength <= 0) {
            return JSONUtil.toJsonStr(content);
        }
        return CharSequenceUtil.subPre(JSONUtil.toJsonStr(content), maxLength);
    }

    /**
     * 截取前缀日志
     * @param content 打印内容
     * @param maxLength 打印长度，小于等于0则不截取
     * @return 截取后的日志
     */
    public static String subPre(String content, int maxLength) {
        if (maxLength <= 0) {
            return content;
        }
        return CharSequenceUtil.subPre(content, maxLength);
    }


}
