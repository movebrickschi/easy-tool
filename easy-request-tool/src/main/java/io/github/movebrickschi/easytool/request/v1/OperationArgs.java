package io.github.movebrickschi.easytool.request.v1;

import com.google.common.collect.Maps;
import io.github.movebrickschi.easytool.core.constants.LccConstants;
import io.github.movebrickschi.easytool.core.constants.NamingStrategyConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 请求参数对象
 * @author MoveBricks Chi
 * @version 1.0
 */
@Deprecated(since = "2.1.11")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OperationArgs {

    /**
     * 请求地址
     */
    private String url;

    /**
     * 请求和响应时间分别是30秒
     */
    @Builder.Default
    private int timeout = 30 * 1000;

    /**
     * 连接超时时间(毫秒),默认30秒
     */
    @Builder.Default
    private int connectionTimeout = 30 * 1000;

    /**
     * 读取超时时间(毫秒),默认30秒
     */
    @Builder.Default
    private int readTimeout = 30 * 1000;

    /**
     * Map类型请求参数
     */
    @Deprecated(since = "2.1.4")
    @Builder.Default
    private Map<String, Object> params = Collections.emptyMap();

    /**
     * json字符串类型请求参数
     */
    @Deprecated(since = "2.1.4")
    private String body;

    /**
     * 参数对象(可以是json字符串、Map或者对象实体)
     * 如果自动转换为指定格式，配合writePropertyNamingStrategy使用
     * param高于params和body
     * @since 2.1.0
     */
    private Object param;

    /**
     * 请求方式,默认为post
     */
    @Builder.Default
    private Operation.Method method = Operation.Method.POST_BODY;

    /**
     * 请求header,默认为json
     */
    @Builder.Default
    private Operation.Application application = Operation.Application.JSON;

    /**
     * 设置状态码默认返回的字段，默认为code
     */
    @Builder.Default
    private String returnCodeField = "code";
    /**
     * 设置返回值默认返回的字段，默认为data
     */
    @Builder.Default
    private String returnDataField = "data";
    /**
     * 设置消息默认返回的字段，默认为message
     */
    @Builder.Default
    private String returnMessageField = "message";

    /**
     * 第三方正确返回的状态码,默认200
     */
    @Builder.Default
    private Integer returnSuccessCode = LccConstants.SuccessEnum.SUCCESS_2_HUNDRED.getCode();

    /**
     * 业务需要返回的状态码
     */
    @Builder.Default
    private Integer bizReturnSuccessCode = LccConstants.SuccessEnum.SUCCESS_ZERO.getCode();

    /**
     * 是否打印返回日志
     * 增加此字段因为如果返回内容过多导致日志文件过大,比如Embedding
     */
    @Builder.Default
    private Boolean isPrintResultLog = true;

    /**
     * 是否打印返回日志
     * 增加此字段因为如果返回内容过多导致日志文件过大,比如Embedding
     */
    @Builder.Default
    private Boolean isPrintArgsLog = true;

    /**
     * 是否打印返回日志
     * 增加此字段因为如果返回内容过多导致日志文件过大,比如Embedding
     * 默认全部打印
     */
    @Builder.Default
    private int printLength = -1;


    /**
     * 多个header,同一个name
     */
    private Map<String, List<String>> headers = Maps.newHashMap();

    /**
     * 多个header,不同的name
     */
    private Map<String, String> headersMap = Maps.newHashMap();

    /**
     * 用于读取数据时，例如将case_id_list字段转换为caseIdList
     * {@link NamingStrategyConstants}
     * @since 2.1.0
     */
    private String readPropertyNamingStrategy = null;

    /**
     * 用于传入参数对象，例如将字段caseIdList转换为case_id_list,
     * {@link NamingStrategyConstants}
     * @since 2.1.0
     */
    @Builder.Default
    private String writePropertyNamingStrategy = null;

    /**
     * 忽略字段，将指定字段排移除
     * 使用此功能，必须在对应实体类上使用@{@link com.fasterxml.jackson.annotation.JsonFilter}
     * @since 2.1.3
     */
    private String[] ignoreFields = null;

    public void setUrl(String url) {
        if (!isValidUrl(url)) {
            throw new IllegalArgumentException("Invalid URL format");
        }
        this.url = url;
    }

    private boolean isValidUrl(String url) {
        // 使用正则表达式或其他方式验证URL格式
        return url != null && url.matches("^(https?|ftp)://.*$");
    }

}
