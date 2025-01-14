package io.github.move.bricks.chi.config;

import io.github.move.bricks.chi.utils.loadbalance.HttpLoadBalancerClient;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

/**
 * webclient配置
 *
 * @author MoveBricks Chi 
 * @version 1.0
 */
@Slf4j
@AutoConfiguration
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({WebClientProperties.class})
public class WebClientConfig {

    @Resource
    private HttpLoadBalancerClient httpLoadBalancerClient;

    @Bean
    @ConditionalOnMissingBean(WebClient.class)
    public WebClient createWebClient(WebClientProperties webClientProperties) {
        // 配置HTTP连接池
        ConnectionProvider provider = ConnectionProvider.builder("custom")
                .maxConnections(webClientProperties.getMaxConnections())
                .maxIdleTime(Duration.ofSeconds(webClientProperties.getMaxIdleTime()))
                .build();

        // 配置HTTP客户端
        HttpClient httpClient = HttpClient.create(provider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, webClientProperties.getConnectTimeout())
                .responseTimeout(Duration.ofSeconds(webClientProperties.getResponseTimeout()))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(webClientProperties.getReadTimeout()))
                                .addHandlerLast(new WriteTimeoutHandler(webClientProperties.getWriteTimeout())));

        // 构建WebClient实例
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(httpLoadBalancerClient.chooseDynamic(webClientProperties.getDefaultUrl()))
//                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                // 添加请求日志记录功能
                .filter(ExchangeFilterFunction.ofRequestProcessor(
                        clientRequest -> {
                            log.debug("Request: {} {}",
                                    clientRequest.method(),
                                    clientRequest.url());
                            return Mono.just(clientRequest);
                        }
                ))
                // 添加响应日志记录功能
                .filter(ExchangeFilterFunction.ofResponseProcessor(
                        clientResponse -> {
                            log.debug("Response status: {}",
                                    clientResponse.statusCode());
                            return Mono.just(clientResponse);
                        }
                ))
                .build();
    }
}