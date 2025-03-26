package io.github.movebrickschi.easytool.request.v2;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.base.Stopwatch;
import io.github.movebrickschi.easytool.core.constants.LccConstants;
import io.github.movebrickschi.easytool.core.utils.object.ObjectConvertUtil;
import io.github.movebrickschi.easytool.request.constants.RequestConstants;
import io.github.movebrickschi.easytool.request.core.*;
import io.github.movebrickschi.easytool.request.v1.OperationArgs;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 获取结果抽象类
 *
 * @author MoveBricks Chi
 * @version 1.0
 * @since 3.0.0
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
                    () -> ObjectConvertUtil.writeWithNamingStrategy(param,
                            operationArgs.getWritePropertyNamingStrategy(),
                            operationArgs.getIgnoreFields())));
        } else if (MapUtil.isNotEmpty(operationArgs.getParams())) {
            operationArgs.setBody(JSONUtil.toJsonStr(operationArgs.getParams()));
        }
        try {
            RequestParams requestParams = BeanUtil.copyProperties(operationArgs, RequestParams.class);
            resultStr = Operation.ACTION_SUPPLIER.get().get(operationArgs.getMethod()).apply(requestParams);
        } catch (Exception e) {
            log.error("url:{},error:{}", operationArgs.getUrl(), e.getMessage());
            return CResult.failed(e.getMessage());
        }
        log.info("\n==>method:{}\n==>url:{}\n==>param:{}\n==>return:{}", operationArgs.getMethod(),
                operationArgs.getUrl(), LogFormatUtil.subPre(operationArgs.getBody(),
                        operationArgs.getPrintLength()),
                LogFormatUtil.printSubPre(operationArgs.getIsPrintResultLog(), resultStr,
                        operationArgs.getPrintLength()));
        if (CharSequenceUtil.isBlank(resultStr)) {
            log.info("end \n==>url:{}\n==>param:{}\n==>return null", operationArgs.getUrl(),
                    LogFormatUtil.subPre(operationArgs.getBody(),
                            operationArgs.getPrintLength()));
            return CResult.failed("v1 resultStr is null");
        }
        JSONObject jsonObject = JSONUtil.parseObj(resultStr);
        CResult<Object> CResult = new CResult<>();
        //防止出现字符串"null"
        CResult.setData(jsonObject.isNull(operationArgs.getReturnDataField()) ? null :
                jsonObject.get(operationArgs.getReturnDataField()));
        CResult.setCode(jsonObject.getInt(operationArgs.getReturnCodeField()));
        CResult.setMessage(jsonObject.getStr(operationArgs.getReturnMessageField()));
        if (operationArgs.getReturnSuccessCode().intValue() != CResult.getCode().intValue()) {
            log.error("end \n==>url:{}\n==>param:{}\n==>error:{}", operationArgs.getUrl(),
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


    @Override
    public CResult<Object> getResult(OperationArgsV2 operationArgsV2) {
        CResult<ComboResult> resultString = getResultString(operationArgsV2);
        if (LccConstants.FAIL.equals(resultString.getCode())) {
            return CResult.failed(resultString.getMessage());
        }
        String resultStr = resultString.getData().getResult();
        String bodyForLog = resultString.getData().getBody();
        if (CharSequenceUtil.isBlank(resultStr)) {
            log.info("\nmethod:{}\n==>url:{}\n==>param:{}\n==>return null", operationArgsV2.getMethod(),
                    operationArgsV2.getUrl(),
                    LogFormatUtil.subPre(bodyForLog,
                            operationArgsV2.getLogConfig().getPrintLength()));
            return CResult.failed("result is null");
        }
        JSONObject jsonObject = JSONUtil.parseObj(resultStr);
        CResult<Object> CResult = new CResult<>();
        //防止出现字符串"null"
        CResult.setData(jsonObject.isNull(operationArgsV2.getReturnConfig().getReturnDataField()) ? null :
                jsonObject.get(operationArgsV2.getReturnConfig().getReturnDataField()));
        CResult.setCode(jsonObject.getInt(operationArgsV2.getReturnConfig().getReturnCodeField()));
        CResult.setMessage(jsonObject.getStr(operationArgsV2.getReturnConfig().getReturnMessageField()));
        if (operationArgsV2.getReturnConfig().getReturnSuccessCode() != CResult.getCode()) {
            log.error("\nmethod==>:{}\n==>url:{}\n==>param:{}\n==>error:{}", operationArgsV2.getMethod(),
                    operationArgsV2.getUrl(),
                    LogFormatUtil.subPre(bodyForLog, operationArgsV2.getLogConfig().getPrintLength()),
                    CResult.getMessage());
            return CResult.failed(CResult.getMessage());
        }
        CResult.setCode(operationArgsV2.getReturnConfig().getBizReturnSuccessCode());
        return CResult;
    }

    @Override
    public CResult<ComboResult> getResultString(OperationArgsV2 operationArgsV2) {
        String resultStr = null;
        Object param = operationArgsV2.getParam();
        RequestParams requestParams = BeanUtil.copyProperties(operationArgsV2, RequestParams.class);
        String bodyForLog = null;
        // type is Map
        if (RequestConstants.FORM_METHODS.contains(requestParams.getMethod()) && Objects.nonNull(param)) {
            if (param instanceof Map<?, ?>) {
                requestParams.setMapParams((Map<String, Object>) param);
            } else {
                Map map = ObjectConvertUtil.convertWithNamingStrategy(param, Map.class,
                        operationArgsV2.getWriteConvertConfig().getIsIncludeNull(),
                        operationArgsV2.getWriteConvertConfig().getNamingStrategy());
                requestParams.setMapParams((Map<String, Object>) map);
            }
            bodyForLog = JSONUtil.toJsonStr(requestParams.getMapParams());
        } else {
            //json type
            requestParams.setBody(ObjectConvertUtil.customConvertToString(param, () ->
                    ObjectConvertUtil.writeWithNamingStrategy(param,
                            operationArgsV2.getWriteConvertConfig().getNamingStrategy(),
                            operationArgsV2.getWriteConvertConfig().getIsIncludeNull(),
                            operationArgsV2.getWriteConvertConfig().getIgnoreFields())));
            bodyForLog = requestParams.getBody();
        }
        Stopwatch watch = Stopwatch.createStarted();
        log.info("😰request starting...");
        try {
            resultStr = Operation.ACTION_SUPPLIER.get().get(operationArgsV2.getMethod()).apply(requestParams);
        } catch (Exception e) {
            log.error("\n==>method:{}\n==>url:{}\nparam:{}\n==>error:{}", operationArgsV2.getMethod(),
                    operationArgsV2.getUrl(), LogFormatUtil.subPre(bodyForLog,
                            operationArgsV2.getLogConfig().getPrintLength()), e.getMessage());
            return CResult.failed(e.getMessage());
        }
        watch.stop();
        int timeCost = (int) watch.elapsed(TimeUnit.MILLISECONDS);
        log.info("\n==>method:{}\n==>url:{}\n==>param:{}\n==>return:{}", operationArgsV2.getMethod(),
                operationArgsV2.getUrl(), LogFormatUtil.subPre(bodyForLog,
                        operationArgsV2.getLogConfig().getPrintLength()),
                LogFormatUtil.printSubPre(operationArgsV2.getLogConfig().getIsPrintResultLog(), resultStr,
                        operationArgsV2.getLogConfig().getPrintLength()));
        log.info("🙂request success,cost time:{}ms", timeCost);
        return CResult.success(ComboResult.builder(bodyForLog, resultStr));
    }

    @Override
    public CResult<?> switchResult(OperationArgsV2 operationArgsV2) {
        CResult<?> cResult = null;
        if (Objects.isNull(operationArgsV2.getReturnConfig())) {
            CResult<ComboResult> resultString = getResultString(operationArgsV2);
            if (resultString.getCode().intValue() == LccConstants.SUCCESS.intValue()) {
                cResult = new CResult<>(LccConstants.SUCCESS, null, resultString.getData().getResult());
            }
        } else {
            cResult = getResult(operationArgsV2);
        }
        if (cResult.getCode().intValue() == LccConstants.FAIL.intValue()) {
            return CResult.failed(cResult.getMessage());
        }
        return cResult;
    }

    public void logRequestStartFormat(OperationArgsV2 operationArgs, String className) {
        log.info("{} start format...", className);
    }

    public void logRequestEnd(String className) {
        log.info("{} end format...", className);
    }

    public void logEmptyResponse(OperationArgsV2 operationArgs) {
        log.info("end and return empty\n==>method url:{}", operationArgs.getUrl());
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
