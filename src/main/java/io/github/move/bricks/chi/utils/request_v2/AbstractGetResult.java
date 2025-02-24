package io.github.move.bricks.chi.utils.request_v2;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.github.move.bricks.chi.utils.object.ObjectConvertUtil;
import io.github.move.bricks.chi.utils.request.CResult;
import io.github.move.bricks.chi.utils.request.Operation;
import io.github.move.bricks.chi.utils.request.OperationArgs;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 获取结果抽象类
 *
 * @author MoveBricks Chi
 * @version 1.0
 * @since 2.1.0
 */
@Slf4j
public abstract class AbstractGetResult implements GetResult {
    @Override
    public CResult<Object> getResult(OperationArgs operationArgs) {
        String resultStr = null;
        Object param = operationArgs.getParam();
        //param优先级最高
        if (Objects.nonNull(param)) {
            operationArgs.setBody(ObjectConvertUtil.customConvertToString(param,
                    obj -> ObjectConvertUtil.writeWithNamingStrategy(param,
                            operationArgs.getWritePropertyNamingStrategy(),
                            operationArgs.getIgnoreFields())));
        } else if (MapUtil.isNotEmpty(operationArgs.getParams())) {
            operationArgs.setBody(JSONUtil.toJsonStr(operationArgs.getParams()));
        }
        try {
            resultStr = Operation.ACTION_SUPPLIER.get().get(operationArgs.getMethod()).apply(operationArgs);
        } catch (Exception e) {
            log.error("request url:{}---------------------error:{}", operationArgs.getUrl(), e.getMessage());
            return CResult.failed(e.getMessage());
        }
        log.info("\n==>request:{}\n==>url:{}\n==>param:{}\n==>return:{}", operationArgs.getMethod(),
                operationArgs.getUrl(), LogFormatUtil.subPre(operationArgs.getBody(),
                        operationArgs.getPrintLength()),
                LogFormatUtil.printSubPre(operationArgs.getIsPrintResultLog(), resultStr,
                        operationArgs.getPrintLength()));
        if (CharSequenceUtil.isBlank(resultStr)) {
            log.info("end----------------request \n==>url:{}\n==>param:{}\n==>return null", operationArgs.getUrl(),
                    LogFormatUtil.subPre(operationArgs.getBody(),
                            operationArgs.getPrintLength()));
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
            log.error("end----------------request \n==>url:{}\n==>param:{}\n==>error:{}", operationArgs.getUrl(),
                    operationArgs.getParams().isEmpty() ? LogFormatUtil.subPre(operationArgs.getBody(),
                            operationArgs.getPrintLength()) :
                            LogFormatUtil.subPre(JSONUtil.toJsonStr(operationArgs.getParams()),
                                    operationArgs.getPrintLength()),
                    CResult.getMessage());
            return CResult.failed(CResult.getMessage());
        }
        CResult.setCode(operationArgs.getBizReturnSuccessCode());
        return CResult;
    }


    public void logRequest(OperationArgs operationArgs, String className) {
        log.info("{} start format----------------\n==>request:{}\n==>url:{}\n==>param:{}", className,
                operationArgs.getMethod(),
                operationArgs.getUrl(),
                Boolean.TRUE.equals(operationArgs.getIsPrintArgsLog()) ?
                        LogFormatUtil.subPre(operationArgs.getBody(),
                                operationArgs.getPrintLength()) : "");
    }

    public boolean isEmptyData(Object data) {
        return Objects.isNull(data) || String.valueOf(data).startsWith("[]");
    }

    public void logEmptyResponse(OperationArgs operationArgs) {
        log.info("end and return empty----------------success\n==>request url:{}\n==>param:{}", operationArgs.getUrl(),
                Boolean.TRUE.equals(operationArgs.getIsPrintArgsLog()) ?
                        LogFormatUtil.subPre(operationArgs.getBody(),
                                operationArgs.getPrintLength()) : "");
    }

    public String getNestedValue(String resultByLevelKey, String... keys) {
        for (String key : keys) {
            resultByLevelKey = JSONUtil.parseObj(resultByLevelKey).getStr(key);
            if (CharSequenceUtil.isBlank(resultByLevelKey) || resultByLevelKey.startsWith("[]")) {
                return resultByLevelKey;
            }
        }
        return resultByLevelKey;
    }

}
