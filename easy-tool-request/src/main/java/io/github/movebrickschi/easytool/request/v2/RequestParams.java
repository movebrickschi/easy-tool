package io.github.movebrickschi.easytool.request.v2;

import com.google.common.collect.Maps;
import io.github.movebrickschi.easytool.request.v1.Operation;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 请求参数
 *
 * @author MoveBricks Chi 
 * @version 1.0
 * @since 2.1.11
 */
@Setter
@Getter
public class RequestParams implements Serializable {


    @Serial
    private static final long serialVersionUID = 7699295261058876700L;
    /**
     * 请求地址
     */
    private String url;

    /**
     * 请求和响应时间分别是30秒
     */
    private int timeout = 30 * 1000;

    /**
     * 连接超时时间(毫秒),默认30秒
     */
    private int connectionTimeout = 30 * 1000;

    /**
     * 读取超时时间(毫秒),默认30秒
     */
    private int readTimeout = 30 * 1000;

    /**
     * 参数对象(可以是json字符串、Map或者对象实体)
     * 如果自动转换为指定格式，配合writePropertyNamingStrategy使用
     * param高于params和body
     */
    private transient Map<String, Object> mapParams;

    private String body;

    /**
     * 请求方式,默认为post
     */
    private Operation.Method method = Operation.Method.POST_BODY;

    /**
     * 请求header,默认为json
     */
    private Operation.Application application = Operation.Application.JSON;

    /**
     * 多个header,同一个name
     */
    private Map<String, List<String>> headers = Maps.newHashMap();

    /**
     * 多个header,不同的name
     */
    private Map<String, String> headersMap = Maps.newHashMap();


}
