package com.lcc.tool.utils.request;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * 封装公用请求类
 *
 * @author Liu Chunchi
 * @date 2023/8/31 10:46:02
 */
@Slf4j
public final class RequestUtil {
    private RequestUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static <T> Result<List<T>> parseArray(OperationArgs operationArgs, Class<T> tClass, String... keys) {
        return new RequestArrayListData().toList(getResult(operationArgs), operationArgs, tClass, keys);
    }

    public static <T> Result<T> parseObj(OperationArgs operationArgs, Class<T> tClass, String... keys) {
        return new RequestSingleData().toSingle(getResult(operationArgs), operationArgs, tClass, keys);
    }

    public static Result parse(OperationArgs operationArgs) {
        return new RequestNoData().noData(getResult(operationArgs), operationArgs);
    }

    public static <T> Result<Map<String, Object>> parseMap(OperationArgs operationArgs, Class<T> tClass, List<String> siblingKes,
                                                           String... keys) {
        return new RequestMapData().toMap(getResult(operationArgs), operationArgs, tClass, siblingKes, keys);
    }

    public static JSONObject parseJsonObject(OperationArgs operationArgs) {
        return JSONUtil.parseObj(Operation.ACTION_SUPPLIER.get().get(operationArgs.getMethod()).apply(operationArgs));
    }


    private static Result<Object> getResult(OperationArgs operationArgs) {
        String resultStr = null;
        try {
            resultStr = Operation.ACTION_SUPPLIER.get().get(operationArgs.getMethod()).apply(operationArgs);
        } catch (Exception e) {
            log.error("request url:{}---------------------error:{}", operationArgs.getUrl(), e.getMessage());
            return Result.failed(e.getMessage());
        }
        log.info("request:{},url:{},param:{},return resultStr:{}", operationArgs.getMethod(), operationArgs.getUrl(),
                operationArgs.getParams().isEmpty() ? operationArgs.getBody() : JSONUtil.toJsonStr(operationArgs.getParams()),
                Boolean.TRUE.equals(operationArgs.getIsPrintResultLog()) ? resultStr : "未设置打印");
        if (CharSequenceUtil.isBlank(resultStr)) {
            log.info("end----------------request url:{},param:{},resultStr return null", operationArgs.getUrl(),
                    operationArgs.getParams().isEmpty() ? operationArgs.getBody() : JSONUtil.toJsonStr(operationArgs.getParams()));
            return Result.failed("request resultStr is null");
        }
        JSONObject jsonObject = JSONUtil.parseObj(resultStr);
        Result<Object> result = new Result<>();
        //防止出现字符串"null"
        result.setData(jsonObject.isNull(operationArgs.getReturnDateField()) ? null :
                jsonObject.get(operationArgs.getReturnDateField()));
        result.setCode(jsonObject.getInt(operationArgs.getReturnCodeField()));
        result.setMessage(jsonObject.getStr(operationArgs.getReturnMessageField()));
        if (operationArgs.getReturnSuccessCode().intValue() != result.getCode().intValue()) {
            log.error("end----------------request url:{},param:{},error:{}", operationArgs.getUrl(),
                    operationArgs.getParams().isEmpty() ? operationArgs.getBody() : JSONUtil.toJsonStr(operationArgs.getParams()),
                    result.getMessage());
            return Result.failed(result.getMessage());
        }
        result.setCode(operationArgs.getBizReturnSuccessCode());
        return result;
    }


}
