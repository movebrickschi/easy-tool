package io.github.movebrickschi.easytool.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * jackson配置类
 *
 * @author MoveBricks Chi
 * @version 1.0
 */
@Slf4j
@AutoConfiguration
@Configuration(proxyBeanMethods = false)
public class JacksonConfig {

    @Bean
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }


}
