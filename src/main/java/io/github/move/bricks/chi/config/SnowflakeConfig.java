package io.github.move.bricks.chi.config;

import cn.hutool.core.lang.Snowflake;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 雪花算法初始化全局对象
 *
 * @author MoveBricks Chi
 * @version 1.0
 */
@Configuration
@ConditionalOnProperty(prefix = "snowflake", name = "enabled", havingValue = "true")
public class SnowflakeConfig {

    @Value("${snowflake.workerId}")
    private Long workerId;

    @Value("${snowflake.datacenterId}")
    private Long dataCenterId;


    @Bean
    @ConditionalOnMissingBean(Snowflake.class)
    public Snowflake getSnowflake() {
        return new Snowflake(workerId, dataCenterId);
    }


}
