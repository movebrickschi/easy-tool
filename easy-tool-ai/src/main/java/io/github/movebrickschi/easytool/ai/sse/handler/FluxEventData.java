package io.github.movebrickschi.easytool.ai.sse.handler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;

/**
 * FluxEventData
 *
 * @author Liu Chunchi
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FluxEventData implements Serializable {

    private WebClient.RequestBodyUriSpec post;

    /**
     * 内容的字段
     */
    private String contentField;

    private Consumer<ServerSentEvent<String>> consumer;

    private String endEvent;
}
