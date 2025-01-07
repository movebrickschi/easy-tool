package io.github.move.bricks.chi.utils.request;

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
 * @version 1.0
 */
@Slf4j
public final class RequestUtil {
    private RequestUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static <T> CResult<List<T>> parseArray(OperationArgs operationArgs, Class<T> tClass, String... keys) {
        return new RequestArrayListData().toList(getResult(operationArgs), operationArgs, tClass, keys);
    }

    public static <T> CResult<T> parseObj(OperationArgs operationArgs, Class<T> tClass, String... keys) {
        return new RequestSingleData().toSingle(getResult(operationArgs), operationArgs, tClass, keys);
    }

    public static CResult parse(OperationArgs operationArgs) {
        return new RequestNoData().noData(getResult(operationArgs), operationArgs);
    }

    public static <T> CResult<Map<String, Object>> parseMap(OperationArgs operationArgs, Class<T> tClass, List<String> siblingKes,
                                                            String... keys) {
        return new RequestMapData().toMap(getResult(operationArgs), operationArgs, tClass, siblingKes, keys);
    }

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
