package io.github.liuchunchiuse.config;


/**
 * redis 连接池配置
 *
 * @author Liu Chunchi
 * @date 2024/2/6 14:47
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
