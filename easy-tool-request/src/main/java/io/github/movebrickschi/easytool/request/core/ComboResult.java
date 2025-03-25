package io.github.movebrickschi.easytool.request.core;

import java.io.Serializable;

/**
 * 获取请求结果的组合对象
 *
 * @author MoveBricks Chi
 * @version 1.0
 * @since 3.0.1
 */
public class ComboResult implements Serializable {

    public ComboResult(String body, String result) {
        this.body = body;
        this.result = result;
    }

    public static ComboResult builder(String body, String result) {
        return new ComboResult(body, result);
    }

    /**
     * 请求字符串结果
     */
    private String result;

    /**
     * 请求参数字符串
     */
    private String body;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
