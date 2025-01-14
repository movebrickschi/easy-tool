package io.github.move.bricks.chi.utils.sse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.Map;

/**
 * sse参数
 *
 * @author MoveBricks Chi 
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SseArgs {

    /**
     * 请求地址
     */
    private String url;

    /**
     * Map类型请求参数
     */
    @Builder.Default
    private Map<String, Object> params = Collections.emptyMap();

    /**
     * json字符串类型请求参数
     */
    private String body;
    /**
     * 接收类型
     */
    @Builder.Default
    private MediaType acceptType = MediaType.TEXT_EVENT_STREAM;
    /**
     * 参数类型
     */
    @Builder.Default
    private MediaType contentType = MediaType.APPLICATION_JSON;

    /**
     * 结束标识
     */
    @Builder.Default
    private String endFlag = CSseConstants.SseEventType.FINISH.getEvent();


}
