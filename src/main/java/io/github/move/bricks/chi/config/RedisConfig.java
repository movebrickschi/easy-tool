package io.github.move.bricks.chi.config;

/**
 * redis配置
 *
 * @author MoveBricks Chi
 * @version 1.0
 */
public class RedisConfig {
    private RedisPoolConfig pool = new RedisPoolConfig();
    private String host;
    private String password;
    private String timeout;
    private String database;
    private String port;
    private String enable;
    private String sysName;
    private String userKey;
    private Long refreshTimeout;

    public RedisPoolConfig getPool() {
        return pool;
    }

    public void setPool(RedisPoolConfig pool) {
        this.pool = pool;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public Long getRefreshTimeout() {
        return refreshTimeout;
    }

    public void setRefreshTimeout(Long refreshTimeout) {
        this.refreshTimeout = refreshTimeout;
    }
}
