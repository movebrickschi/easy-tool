package io.github.liuchunchiuse.config;

import jakarta.annotation.Resource;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置布隆过滤器
 *
 * @author Liu Chunchi
 * @version 1.0
 */
@Configuration
public class BloomFilterConfig {
    @Resource
    private RedissonClient redissonClient;

    /**
     * null数字员工为空布隆过滤器
     */
    @Bean
    public RBloomFilter<String> agentBloomFilter() {
        String filterName = "nullBloomFilter";
        long expectedInsertions = 10000L;
        double falseProbability = 0.01;
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(filterName);
        bloomFilter.tryInit(expectedInsertions, falseProbability);
        return bloomFilter;
    }
}