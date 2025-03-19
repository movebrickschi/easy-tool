package io.github.movebrickschi.easytool.request.request_v2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 日志配置
 *
 * @author MoveBricks Chi 
 * @version 1.0
 * @since 2.1.11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogConfig {

    /**
     * 是否打印返回日志
     * 增加此字段因为如果返回内容过多导致日志文件过大,比如Embedding
     */
    private Boolean isPrintResultLog = true;

    /**
     * 是否打印返回日志
     * 增加此字段因为如果返回内容过多导致日志文件过大,比如Embedding
     */
    private Boolean isPrintArgsLog = true;

    /**
     * 是否打印返回日志
     * 增加此字段因为如果返回内容过多导致日志文件过大,比如Embedding
     * 默认全部打印
     */
    private int printLength = -1;

    public LogConfig(int printLength) {
        this.printLength = printLength;
    }

}
