package io.github.movebrickschi.easytool.core;

import io.github.movebrickschi.easytool.core.utils.loadbalance.HttpLoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自动配置
 *
 * @author MoveBricks Chi
 * @version 1.0
 */
@Configuration
public class AutoConfiguration {

    @Bean
    public HttpLoadBalancerClient httpLoadBalancerClient() {
        return new HttpLoadBalancerClient();
    }



}
