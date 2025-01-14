package io.github.move.bricks.chi.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * webclient属性配置
 *
 * @author MoveBricks Chi
 * @version 1.0
 */
@Data
@ConditionalOnMissingBean(WebClientProperties.class)
@ConfigurationProperties(prefix = WebClientProperties.PREFIX)
public class WebClientProperties {

    /**
     * 配置前缀
     */
    public static final String PREFIX = "webclient";

    /**
     * 基础url
     */
    private String defaultUrl;

    /**
     * 最大连接数
     */
    private int maxConnections = 500;

    /**
     * 最大空闲时间
     */
    private int maxIdleTime = 20;

    /**
     * 连接超时时间（毫秒）
     */
    private int connectTimeout = 5000;

    /**
     * 响应时间（秒）
     */

    private int responseTimeout = 5;

    /**
     * 读取超时时间（秒）
     */
    private int readTimeout = 5;

    /**
     * 写入超时时间（秒）
     */
    private int writeTimeout = 5;

}
