package io.github.move.bricks.chi.utils.request;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * 封装公用请求类
 * 使用示例：{@link io.github.move.bricks.chi.demo.RequestUtilDemo}
 * @author MoveBricks Chi
 * @version 1.0
 */
@Slf4j
public final class RequestUtil {
    private RequestUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 将data转换成list的场景
     * @param operationArgs 请求方法参数
     * @param tClass 返回类型
     * @param keys 内嵌子集key,由外向内的嵌套key
     * @return 返回类型
     * @param <T> 参数类型
     */
    public static <T> CResult<List<T>> parseArray(OperationArgs operationArgs, Class<T> tClass, String... keys) {
        return new RequestArrayListData().toList(getResult(operationArgs), operationArgs, tClass, keys);
    }

    /**
     * 单例形式,返回对应数据类型
     * @param operationArgs 请求方法参数
     * @param tClass 返回类型
     * @param keys 内嵌子集key,由外向内的嵌套key
     * @return 返回类型
     * @param <T> 参数类型
     */
    public static <T> CResult<T> parseObj(OperationArgs operationArgs, Class<T> tClass, String... keys) {
        return new RequestSingleData().toSingle(getResult(operationArgs), operationArgs, tClass, null, keys);
    }

    /**
     * 取出data中的一个字段转换成对应的类型
     * @param operationArgs 请求方法参数
     * @param tClass 返回类型
     * @param key data里面的一个key
     * @return 返回类型
     * @param <T> 参数类型
     */
    public static <T> CResult<T> parseObj(OperationArgs operationArgs, Class<T> tClass, String key) {
        return new RequestSingleData().toSingle(getResult(operationArgs), operationArgs, tClass, key);
    }

    /**
     * data为空的场景
     * @param operationArgs 请求方法参数
     * @return CResult
     */
    public static CResult parse(OperationArgs operationArgs) {
        return new RequestNoData().noData(getResult(operationArgs), operationArgs);
    }

    /**
     * 转换为map形式
     * @param operationArgs 请求方法参数
     * @param tClass 返回类型
     * @param siblingKes 同级key
     * @param keys 内嵌子集key,由外向内的嵌套key
     * @return 返回结果
     * @param <T> 请求类型
     */
    public static <T> CResult<Map<String, Object>> parseMap(OperationArgs operationArgs, Class<T> tClass, List<String> siblingKes,
                                                            String... keys) {
        return new RequestMapData().toMap(getResult(operationArgs), operationArgs, tClass, siblingKes, keys);
    }

    /**
     * 获取Data下面的数据，如果穿keys则取对应的key和value存入map，否则取全部data放入map
     * @param operationArgs 请求方法参数
     * @param keys data下对应key的字段，例如：data下有a,b,c三个key，则keys为["a","b","c"]
     * @return 返回结果
     */
    public static CResult<Map<String, Object>> parseMap(OperationArgs operationArgs, String... keys) {
        return new RequestMapData().toMap(getResult(operationArgs), operationArgs, keys);
    }

    /**
     * 返回结果原始结果转为JSONObject
     * @param operationArgs 请求参数
     * @return 原始数据
     */
    public static JSONObject parseJsonObject(OperationArgs operationArgs) {
        return JSONUtil.parseObj(Operation.ACTION_SUPPLIER.get().get(operationArgs.getMethod()).apply(operationArgs));
    }


    private static CResult<Object> getResult(OperationArgs operationArgs) {
        String resultStr = null;
        try {
            resultStr = Operation.ACTION_SUPPLIER.get().get(operationArgs.getMethod()).apply(operationArgs);
        } catch (Exception e) {
            log.error("request url:{}---------------------error:{}", operationArgs.getUrl(), e.getMessage());
            return CResult.failed(e.getMessage());
        }
        log.info("request:{},url:{},param:{},return resultStr:{}", operationArgs.getMethod(), operationArgs.getUrl(),
                operationArgs.getParams().isEmpty() ? CharSequenceUtil.subPre(operationArgs.getBody(), operationArgs.getPrintLength()) :
                        CharSequenceUtil.subPre(JSONUtil.toJsonStr(operationArgs.getParams()), operationArgs.getPrintLength()),
                Boolean.TRUE.equals(operationArgs.getIsPrintResultLog()) ? CharSequenceUtil.subPre(resultStr, operationArgs.getPrintLength()) : "未设置打印");
        if (CharSequenceUtil.isBlank(resultStr)) {
            log.info("end----------------request url:{},param:{},resultStr return null", operationArgs.getUrl(),
                    operationArgs.getParams().isEmpty() ? CharSequenceUtil.subPre(operationArgs.getBody(), operationArgs.getPrintLength()) :
                            CharSequenceUtil.subPre(JSONUtil.toJsonStr(operationArgs.getParams()), operationArgs.getPrintLength()));
            return CResult.failed("request resultStr is null");
        }
        JSONObject jsonObject = JSONUtil.parseObj(resultStr);
        CResult<Object> CResult = new CResult<>();
        //防止出现字符串"null"
        CResult.setData(jsonObject.isNull(operationArgs.getReturnDataField()) ? null :
                jsonObject.get(operationArgs.getReturnDataField()));
        CResult.setCode(jsonObject.getInt(operationArgs.getReturnCodeField()));
        CResult.setMessage(jsonObject.getStr(operationArgs.getReturnMessageField()));
        if (operationArgs.getReturnSuccessCode().intValue() != CResult.getCode().intValue()) {
            log.error("end----------------request url:{},param:{},error:{}", operationArgs.getUrl(),
                    operationArgs.getParams().isEmpty() ? CharSequenceUtil.subPre(operationArgs.getBody(), operationArgs.getPrintLength()) :
                            CharSequenceUtil.subPre(JSONUtil.toJsonStr(operationArgs.getParams()), operationArgs.getPrintLength()),
                    CResult.getMessage());
            return CResult.failed(CResult.getMessage());
        }
        CResult.setCode(operationArgs.getBizReturnSuccessCode());
        return CResult;
    }


}
