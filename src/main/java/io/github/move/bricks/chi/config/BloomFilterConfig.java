package io.github.move.bricks.chi.config;

import io.github.move.bricks.chi.utils.redis.RedisUtil;
import jakarta.annotation.Resource;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置布隆过滤器
 *
 * @author MoveBricks Chi
 * @version 1.0
 */
@Configuration
@AutoConfigureBefore(RedisUtil.class)
public class BloomFilterConfig {
    @Resource
    private RedissonClient redissonClient;

    /**
     * null数字员工为空布隆过滤器
     */
    @Bean
    @ConditionalOnProperty(prefix = "spring.data.redis", name = "host")
    public RBloomFilter<String> agentBloomFilter() {
        String filterName = "nullBloomFilter";
        long expectedInsertions = 10000L;
        double falseProbability = 0.01;
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(filterName);
        bloomFilter.tryInit(expectedInsertions, falseProbability);
        return bloomFilter;
    }
}