package io.github.move.bricks.chi.utils.request;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import io.github.move.bricks.chi.constants.LccConstants;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 请求返回map类型
 *
 * @author Liu Chunchi
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
        log.info("start----------------single format request:{},url:{},param:{}", operationArgs.getMethod(), operationArgs.getUrl(),
                Boolean.TRUE.equals(operationArgs.getIsPrintArgsLog()) ?
                        CharSequenceUtil.subPre(JSONUtil.toJsonStr(operationArgs.getParams()), operationArgs.getPrintLength()) : "");
        //返回为空
        if (Objects.isNull(cResult.getData())) {
            log.info("end and return empty----------------success request url:{},param:{}", operationArgs.getUrl(),
                    Boolean.TRUE.equals(operationArgs.getIsPrintArgsLog()) ?
                            CharSequenceUtil.subPre(JSONUtil.toJsonStr(operationArgs.getParams()), operationArgs.getPrintLength()) : "");
            return CResult.success();
        }
        //data数据
        String resultByLevelKey = JSONUtil.toJsonStr(cResult.getData());
        Map<String, Object> mapResult = Maps.newHashMap();

        if (!siblingKes.isEmpty()) {
            for (String key : siblingKes) {
                //data下对应key的字段取出放入map
                mapResult.put(key, JSONUtil.parseObj(resultByLevelKey).getStr(key));
            }
        }

        //keys为子集的key
        if (ArrayUtil.isNotEmpty(keys)) {
            for (String key : keys) {
                //嵌套查询对应key的字段,获取到最终结果放入resultByLevelKey,给下面类型转换使用
                resultByLevelKey = JSONUtil.parseObj(resultByLevelKey).getStr(key);
                if (CharSequenceUtil.isBlank(resultByLevelKey) || resultByLevelKey.startsWith("[]")) {
                    log.info("end and return empty----------------success post url:{},param:{}", operationArgs.getUrl(),
                            Boolean.TRUE.equals(operationArgs.getIsPrintArgsLog()) ?
                                    CharSequenceUtil.subPre(JSONUtil.toJsonStr(operationArgs.getParams()), operationArgs.getPrintLength()) : "");
                    return CResult.success();
                }
            }
        }
        log.info("end----------------success,post url:{},param:{}", operationArgs.getUrl(),
                Boolean.TRUE.equals(operationArgs.getIsPrintArgsLog()) ?
                        CharSequenceUtil.subPre(JSONUtil.toJsonStr(operationArgs.getParams()), operationArgs.getPrintLength()) : "");
        if (resultByLevelKey.startsWith("[")) {
            //返回结果为list
            mapResult.put(RETURN_TYPE_LIST, JSONUtil.toList(JSONUtil.parseArray(resultByLevelKey), ThreadLocal.class));
            return CResult.success(mapResult);
        }
        //返回单对象
        mapResult.put(RETURN_TYPE_SINGLE, JSONUtil.toBean(resultByLevelKey, tClass));
        return CResult.success(mapResult);
    }

    @Override
    public CResult<Map<String, Object>> toMap(CResult<Object> cResult, OperationArgs operationArgs, String... keys) {
        if (cResult.getCode().intValue() == LccConstants.FAIL.intValue()) {
            return CResult.failed(cResult.getMessage());
        }
        log.info("start----------------single format request:{},url:{},param:{}", operationArgs.getMethod(),
                operationArgs.getUrl(),
                Boolean.TRUE.equals(operationArgs.getIsPrintArgsLog()) ?
                        CharSequenceUtil.subPre(JSONUtil.toJsonStr(operationArgs.getParams()),
                                operationArgs.getPrintLength()) : "");
        //返回为空
        if (Objects.isNull(cResult.getData())) {
            log.info("end and return empty----------------success request url:{},param:{}", operationArgs.getUrl(),
                    Boolean.TRUE.equals(operationArgs.getIsPrintArgsLog()) ?
                            CharSequenceUtil.subPre(JSONUtil.toJsonStr(operationArgs.getParams()),
                                    operationArgs.getPrintLength()) : "");
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
