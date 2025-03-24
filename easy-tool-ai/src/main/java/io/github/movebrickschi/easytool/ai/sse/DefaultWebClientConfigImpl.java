package io.github.movebrickschi.easytool.ai.sse;

import io.github.movebrickschi.easytool.core.utils.loadbalance.HttpLoadBalancerClient;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
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
public class DefaultWebClientConfigImpl implements io.github.movebrickschi.easytool.ai.sse.WebClientConfig, Ordered {

    @Resource
    private HttpLoadBalancerClient httpLoadBalancerClient;

    @Bean
    @ConditionalOnMissingBean(WebClientProperties.class)
    @ConfigurationProperties(prefix = WebClientProperties.PREFIX)
    @ConditionalOnProperty(prefix = WebClientProperties.PREFIX, value = "enabled", havingValue = "true",
            matchIfMissing = false)
    @Override
    public WebClientProperties webClientProperties() {
        return new WebClientProperties();
    }

    @Override
    @Bean
    @ConditionalOnBean(WebClientProperties.class)
    public WebClient createWebClient(WebClientProperties webClientProperties) {
        // 配置HTTP连接池
        ConnectionProvider provider = ConnectionProvider.builder("custom")
                //设置连接池中的最大连接数
                .maxConnections(webClientProperties.getMaxConnections())
                //设置连接池中连接的最大空闲时间
                .maxIdleTime(Duration.ofSeconds(webClientProperties.getMaxIdleTime()))
                .build();

        // 配置HTTP客户端
        HttpClient httpClient = HttpClient.create(provider)
                //设置连接超时时间
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, webClientProperties.getConnectTimeout())
                //设置响应超时时间，如果服务器在指定时间内未返回任何数据，则会抛出超时异常。
                .responseTimeout(Duration.ofSeconds(webClientProperties.getResponseTimeout()))
                //限制客户端向服务器发送数据的时间。如果在指定时间内未能完成数据发送，则会抛出超时异常。
                //限制客户端等待从服务器读取数据的时间。如果在指定时间内未接收到数据，则会抛出超时异常。
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


    @Override
    @Bean
    @ConditionalOnBean(WebClient.class)
    public SseUtil sseUtil(WebClient webClient) {
        return new SseUtil(webClient);
    }

    @Override
    @Bean
    @ConditionalOnBean(WebClient.class)
    public SseClient sseClient(WebClient webClient) {
        return new DefaultSseClient(webClient);
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}