package io.github.move.bricks.chi.config;

import jakarta.annotation.Resource;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置布隆过滤器
 *
 * @author MoveBricks Chi
 * @version 1.0
 */
@Configuration
@ConditionalOnBean(RedissonClient.class)
public class BloomFilterConfig {
    @Resource
    private RedissonClient redissonClient;

    /**
     * null数字员工为空布隆过滤器
     */
    @Bean
    @ConditionalOnMissingBean(RBloomFilter.class)
    public RBloomFilter<String> agentBloomFilter() {
        String filterName = "nullBloomFilter";
        long expectedInsertions = 10000L;
        double falseProbability = 0.01;
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(filterName);
        bloomFilter.tryInit(expectedInsertions, falseProbability);
        return bloomFilter;
    }
}