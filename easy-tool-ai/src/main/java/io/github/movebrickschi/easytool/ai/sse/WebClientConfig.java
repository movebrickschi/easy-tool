package io.github.movebrickschi.easytool.ai.sse;

import org.springframework.web.reactive.function.client.WebClient;

public interface WebClientConfig {

    WebClientProperties webClientProperties();

    WebClient createWebClient(WebClientProperties webClientProperties);

    default SseClient sseClient(WebClient webClient) {
        return new DefaultSseClient(webClient);
    }
}