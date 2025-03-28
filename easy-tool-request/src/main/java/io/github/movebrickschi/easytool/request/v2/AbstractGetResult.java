package io.github.movebrickschi.easytool.request.v2;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.github.movebrickschi.easytool.core.utils.object.ObjectConvertUtil;
import io.github.movebrickschi.easytool.request.constants.RequestConstants;
import io.github.movebrickschi.easytool.request.core.CResult;
import io.github.movebrickschi.easytool.request.core.LogFormatUtil;
import io.github.movebrickschi.easytool.request.core.Operation;
import io.github.movebrickschi.easytool.request.core.RequestParams;
import io.github.movebrickschi.easytool.request.v1.OperationArgs;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;

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
            log.error("v1 url:{},error:{}", operationArgs.getUrl(), e.getMessage());
            return CResult.failed(e.getMessage());
        }
        log.info("\n==>v1:{}\n==>url:{}\n==>param:{}\n==>return:{}", operationArgs.getMethod(),
                operationArgs.getUrl(), LogFormatUtil.subPre(operationArgs.getBody(),
                        operationArgs.getPrintLength()),
                LogFormatUtil.printSubPre(operationArgs.getIsPrintResultLog(), resultStr,
                        operationArgs.getPrintLength()));
        if (CharSequenceUtil.isBlank(resultStr)) {
            log.info("end v1 \n==>url:{}\n==>param:{}\n==>return null", operationArgs.getUrl(),
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
            log.error("end v1 \n==>url:{}\n==>param:{}\n==>error:{}", operationArgs.getUrl(),
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
    public CResult<Object> getResult(OperationArgsV2 operationArgs) {
        String resultStr = null;
        Object param = operationArgs.getParam();
        RequestParams requestParams = BeanUtil.copyProperties(operationArgs, RequestParams.class);
        String bodyForLog = null;
        // type is Map
        if (RequestConstants.FORM_METHODS.contains(requestParams.getMethod()) && Objects.nonNull(param)) {
            if (param instanceof Map<?, ?>) {
                requestParams.setMapParams((Map<String, Object>) param);
            } else {
                Map map = ObjectConvertUtil.convertWithNamingStrategy(param, Map.class,
                        operationArgs.getWriteConvertConfig().getIsIncludeNull(),
                        operationArgs.getWriteConvertConfig().getNamingStrategy());
                requestParams.setMapParams((Map<String, Object>) map);
            }
            bodyForLog = JSONUtil.toJsonStr(requestParams.getMapParams());
        } else {
            //json type
            requestParams.setBody(ObjectConvertUtil.customConvertToString(param, () ->
                    ObjectConvertUtil.writeWithNamingStrategy(param,
                            operationArgs.getWriteConvertConfig().getNamingStrategy(),
                            operationArgs.getWriteConvertConfig().getIsIncludeNull(),
                            operationArgs.getWriteConvertConfig().getIgnoreFields())));
            bodyForLog = requestParams.getBody();
        }
        try {
            resultStr = Operation.ACTION_SUPPLIER.get().get(operationArgs.getMethod()).apply(requestParams);
        } catch (Exception e) {
            log.error("\n==>v1:{}\n==>url:{}\nparam:{}\n==>error:{}", operationArgs.getMethod(),
                    operationArgs.getUrl(), LogFormatUtil.subPre(bodyForLog,
                            operationArgs.getLogConfig().getPrintLength()), e.getMessage());
            return CResult.failed(e.getMessage());
        }
        log.info("\n==>v1:{}\n==>url:{}\n==>param:{}\n==>return:{}", operationArgs.getMethod(),
                operationArgs.getUrl(), LogFormatUtil.subPre(bodyForLog,
                        operationArgs.getLogConfig().getPrintLength()),
                LogFormatUtil.printSubPre(operationArgs.getLogConfig().getIsPrintResultLog(), resultStr,
                        operationArgs.getLogConfig().getPrintLength()));
        if (CharSequenceUtil.isBlank(resultStr)) {
            log.info("\nv1:{}\n==>url:{}\n==>param:{}\n==>return null", operationArgs.getMethod(),
                    operationArgs.getUrl(),
                    LogFormatUtil.subPre(bodyForLog,
                            operationArgs.getLogConfig().getPrintLength()));
            return CResult.failed("v1 result is null");
        }
        JSONObject jsonObject = JSONUtil.parseObj(resultStr);
        CResult<Object> CResult = new CResult<>();
        //防止出现字符串"null"
        CResult.setData(jsonObject.isNull(operationArgs.getReturnConfig().getReturnDataField()) ? null :
                jsonObject.get(operationArgs.getReturnConfig().getReturnDataField()));
        CResult.setCode(jsonObject.getInt(operationArgs.getReturnConfig().getReturnCodeField()));
        CResult.setMessage(jsonObject.getStr(operationArgs.getReturnConfig().getReturnMessageField()));
        if (operationArgs.getReturnConfig().getReturnSuccessCode() != CResult.getCode()) {
            log.error("\nv1==>:{}\n==>url:{}\n==>param:{}\n==>error:{}", operationArgs.getMethod(),
                    operationArgs.getUrl(),
                    LogFormatUtil.subPre(bodyForLog, operationArgs.getLogConfig().getPrintLength()),
                    CResult.getMessage());
            return CResult.failed(CResult.getMessage());
        }
        CResult.setCode(operationArgs.getReturnConfig().getBizReturnSuccessCode());
        return CResult;
    }


    public void logRequest(OperationArgsV2 operationArgs, String className) {
        log.info("{} start format...\n==>v1:{}\n==>url:{}", className, operationArgs.getMethod(),
                operationArgs.getUrl());
    }

    public void logEmptyResponse(OperationArgsV2 operationArgs) {
        log.info("end and return empty\n==>v1 url:{}", operationArgs.getUrl());
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
