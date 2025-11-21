package io.github.movebrickschi.easytool.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import io.github.movebrickschi.easytool.redis.config.RedisProperties;
import io.github.movebrickschi.easytool.redis.utils.redis.EasyRedisUtil;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 自动配置
 *
 * @author MoveBricks Chi
 * @version 1.0
 */
@Configuration(proxyBeanMethods = false)
@AutoConfiguration
public class AutoConfig {


    @Bean
    public EasyRedisUtil easyRedisUtil() {
        return new EasyRedisUtil();
    }

    @Bean
    public RedisProperties constructBootRedisConfig(Environment environment) {
        Binder binder = Binder.get(environment);

        // 先尝试绑定 spring.data.redis
        RedisProperties properties = binder.bind("spring.data.redis", RedisProperties.class)
                .orElse(null);

        // 如果 spring.data.redis 没有配置，尝试 spring.redis
        if (properties == null || properties.getHost() == null) {
            properties = binder.bind("spring.redis", RedisProperties.class)
                    .orElse(new RedisProperties());
        }

        return properties;
    }

    @Bean("redisTemplateByJacksonSerializer")
    @ConditionalOnClass(RedisProperties.class)
    @ConditionalOnMissingBean(name = "redisTemplateByJacksonSerializer")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 配置连接工厂
        template.setConnectionFactory(factory);

        ObjectMapper objectMapper = new ObjectMapper();
        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

        //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值（默认使用JDK的序列化方式）
        Jackson2JsonRedisSerializer<Object> jacksonSeial = new Jackson2JsonRedisSerializer<>(Object.class);
        jacksonSeial.setObjectMapper(objectMapper);

        // 值采用json序列化
        template.setValueSerializer(jacksonSeial);
        //使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());

        // 设置hash key 和value序列化模式
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jacksonSeial);
        template.afterPropertiesSet();

        return template;
    }


}
