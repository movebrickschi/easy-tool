package io.github.movebrickschi.easytool.ai.sse;

import lombok.Data;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;

import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

/**
 * sse参数
 *
 * @author MoveBricks Chi 
 * @version 1.0
 */
@Data
public class SseArgs {

    /**
     * 请求地址
     */
    private String url;

    /**
     * Map类型请求参数
     */
    private Map<String, Object> params = Collections.emptyMap();

    private Object object;

    /**
     * json字符串类型请求参数
     */
    private String body;
    /**
     * 接收类型
     */
    private MediaType acceptType = MediaType.TEXT_EVENT_STREAM;
    /**
     * 参数类型
     */
    private MediaType contentType = MediaType.APPLICATION_JSON;

    /**
     * 结束标识
     */
    private String endFlag = CSseConstants.SseEventType.FINISH.getEvent();

    /**
     * 事件处理
     */
    private Consumer<ServerSentEvent<String>> process = null;

    /**
     * 参数转化对象
     */
    private ObjectConverter objectConverter;


    // 静态内部构建器类
    public static class Builder {
        private String url;
        private Map<String, Object> params = Collections.emptyMap();
        private ObjectConverter objectConverter;
        private String body;
        private MediaType acceptType = MediaType.TEXT_EVENT_STREAM;
        private MediaType contentType = MediaType.APPLICATION_JSON;
        private String endFlag = CSseConstants.SseEventType.FINISH.getEvent();
        private Consumer<ServerSentEvent<String>> process = null;

        // 为每个字段添加 setter 方法
        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder params(Map<String, Object> params) {
            this.params = params;
            return this;
        }

        public Builder objectWithIgnoreFields(Object object, String writePropertyNamingStrategy,
                                              String... ignoreFields) {
            this.objectConverter = new ObjectConverter(object, writePropertyNamingStrategy, ignoreFields);
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder acceptType(MediaType acceptType) {
            this.acceptType = acceptType;
            return this;
        }

        public Builder contentType(MediaType contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder endFlag(String endFlag) {
            this.endFlag = endFlag;
            return this;
        }

        public Builder process(Consumer<ServerSentEvent<String>> process) {
            this.process = process;
            return this;
        }

        // 构建方法
        public SseArgs build() {
            SseArgs sseArgs = new SseArgs();
            sseArgs.setUrl(url);
            sseArgs.setParams(params);
            sseArgs.setObjectConverter(objectConverter);
            sseArgs.setBody(body);
            sseArgs.setAcceptType(acceptType);
            sseArgs.setContentType(contentType);
            sseArgs.setEndFlag(endFlag);
            sseArgs.setProcess(process);
            return sseArgs;
        }
    }

    // 提供 builder 方法
    public static Builder builder() {
        return new Builder();
    }


}
