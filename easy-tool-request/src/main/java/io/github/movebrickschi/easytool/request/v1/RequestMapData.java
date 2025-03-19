package io.github.movebrickschi.easytool.request.v1;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import io.github.movebrickschi.easytool.core.constants.LccConstants;
import io.github.movebrickschi.easytool.request.core.CResult;
import io.github.movebrickschi.easytool.request.core.Operation;
import io.github.movebrickschi.easytool.request.core.LogFormatUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 请求返回map类型
 *
 * @author MoveBricks Chi
 * @version 1.0
 */
@Slf4j
public class RequestMapData implements Operation {

    @Override
    public <T> CResult<Map<String, Object>> toMap(CResult<Object> cResult, OperationArgs operationArgs,
                                                  Class<T> tClass, List<String> siblingKes,
                                                  String... keys) {
        if (cResult.getCode().intValue() == LccConstants.FAIL.intValue()) {
            return CResult.failed(cResult.getMessage());
        }
        log.info("start----------------single format v1:{},url:{},param:{}", operationArgs.getMethod(), operationArgs.getUrl(),
                LogFormatUtil.printSubPre(operationArgs.getIsPrintArgsLog(), operationArgs.getParams(),
                        operationArgs.getPrintLength()));
        //返回为空
        if (Objects.isNull(cResult.getData())) {
            log.info("end and return empty----------------success v1 url:{},param:{}", operationArgs.getUrl(),
                    LogFormatUtil.printSubPre(operationArgs.getIsPrintArgsLog(), operationArgs.getParams(),
                            operationArgs.getPrintLength()));
            return CResult.success();
        }
        //data数据
        String resultByLevelKey = JSONUtil.toJsonStr(cResult.getData());
        Map<String, Object> mapResult = Maps.newHashMap();

        //data下同级对应key的字段取出放入map
        if (!siblingKes.isEmpty()) {
            for (String key : siblingKes) {
                mapResult.put(key, JSONUtil.parseObj(resultByLevelKey).getStr(key));
            }
        }

        //keys为dada下嵌套子集的集合,查询最底层的一个key的内容
        if (ArrayUtil.isNotEmpty(keys)) {
            for (String key : keys) {
                //嵌套查询对应key的字段,获取到最终结果放入resultByLevelKey,给下面类型转换使用
                resultByLevelKey = JSONUtil.parseObj(resultByLevelKey).getStr(key);
                if (CharSequenceUtil.isBlank(resultByLevelKey) || resultByLevelKey.startsWith("[]")) {
                    log.info("end and return empty----------------success post url:{},param:{}", operationArgs.getUrl(),
                            LogFormatUtil.printSubPre(operationArgs.getIsPrintArgsLog(), operationArgs.getParams(),
                                    operationArgs.getPrintLength()));
                    return CResult.success();
                }
            }
        }
        log.info("end----------------success,post url:{},param:{}", operationArgs.getUrl(),
                LogFormatUtil.printSubPre(operationArgs.getIsPrintArgsLog(), operationArgs.getParams(),
                        operationArgs.getPrintLength()));
        if (resultByLevelKey.startsWith("[")) {
            //返回结果为list
            mapResult.put(RETURN_TYPE_LIST, JSONUtil.toList(JSONUtil.parseArray(resultByLevelKey), tClass));
            return CResult.success(mapResult);
        }
        if (JSONUtil.isTypeJSON(resultByLevelKey)) {
            //返回单对象
            mapResult.put(RETURN_TYPE_SINGLE, JSONUtil.toBean(resultByLevelKey, tClass));
        }
        return CResult.success(mapResult);
    }

    @Override
    public CResult<Map<String, Object>> toMap(CResult<Object> cResult, OperationArgs operationArgs, String... keys) {
        if (cResult.getCode().intValue() == LccConstants.FAIL.intValue()) {
            return CResult.failed(cResult.getMessage());
        }
        log.info("start----------------single format v1:{},url:{},param:{}", operationArgs.getMethod(),
                operationArgs.getUrl(),
                LogFormatUtil.printSubPre(operationArgs.getIsPrintArgsLog(), operationArgs.getParams(),
                        operationArgs.getPrintLength()));
        //返回为空
        if (Objects.isNull(cResult.getData())) {
            log.info("end and return empty----------------success v1 url:{},param:{}", operationArgs.getUrl(),
                    LogFormatUtil.printSubPre(operationArgs.getIsPrintArgsLog(), operationArgs.getParams(),
                            operationArgs.getPrintLength()));
            return CResult.success();
        }
        Map<String, Object> mapResult = Maps.newHashMap();
        //返回具体的key
        if (ArrayUtil.isNotEmpty(keys)) {
            for (String key : keys) {
                mapResult.put(key, JSONUtil.parseObj(cResult.getData()).getStr(key));
            }
            return CResult.success(mapResult);
        }
        //返回全部data
        mapResult.putAll(JSONUtil.parseObj(cResult.getData()));
        return CResult.success(mapResult);
    }
}
