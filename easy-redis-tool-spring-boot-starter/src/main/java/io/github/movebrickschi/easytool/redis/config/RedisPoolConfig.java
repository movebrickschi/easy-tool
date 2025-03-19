package io.github.movebrickschi.easytool.redis.config;


/**
 * redis 连接池配置
 *
 * @author MoveBricks Chi
 * @version 1.0
 */
public class RedisPoolConfig {
    private String maxActive;
    private String maxIdle;
    private String maxWait;

    public String getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(String maxActive) {
        this.maxActive = maxActive;
    }

    public String getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(String maxIdle) {
        this.maxIdle = maxIdle;
    }

    public String getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(String maxWait) {
        this.maxWait = maxWait;
    }
}
