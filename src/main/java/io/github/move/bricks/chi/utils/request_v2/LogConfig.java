package io.github.move.bricks.chi.utils.request_v2;

/**
 * 日志配置
 *
 * @author MoveBricks Chi 
 * @version 1.0
 * @since 2.1.11
 */
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

    public LogConfig() {
    }

    public LogConfig(int printLength) {
        this.printLength = printLength;
    }

    public LogConfig(Boolean isPrintArgsLog, Boolean isPrintResultLog, int printLength) {
        this.isPrintArgsLog = isPrintArgsLog;
        this.isPrintResultLog = isPrintResultLog;
        this.printLength = printLength;
    }

    public Boolean getPrintArgsLog() {
        return isPrintArgsLog;
    }

    public void setPrintArgsLog(Boolean printArgsLog) {
        isPrintArgsLog = printArgsLog;
    }

    public Boolean getPrintResultLog() {
        return isPrintResultLog;
    }

    public void setPrintResultLog(Boolean printResultLog) {
        isPrintResultLog = printResultLog;
    }

    public int getPrintLength() {
        return printLength;
    }

    public void setPrintLength(int printLength) {
        this.printLength = printLength;
    }
}
