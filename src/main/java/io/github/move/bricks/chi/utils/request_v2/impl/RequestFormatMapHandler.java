package io.github.move.bricks.chi.utils.request_v2.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import io.github.move.bricks.chi.constants.LccConstants;
import io.github.move.bricks.chi.utils.request.CResult;
import io.github.move.bricks.chi.utils.request.Operation;
import io.github.move.bricks.chi.utils.request.OperationArgs;
import io.github.move.bricks.chi.utils.request_v2.AbstractGetResult;
import io.github.move.bricks.chi.utils.request_v2.RequestFormatApi;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 请求返回map类型
 *
 * @author MoveBricks Chi
 * @version 1.0
 * @since 2.1.0
 */
@Slf4j
public class RequestFormatMapHandler extends AbstractGetResult implements Serializable, RequestFormatApi {
    @Override
    public <T> CResult<Map<String, Object>> toMap(OperationArgs operationArgs,
                                                  Class<T> tClass, List<String> siblingKes,
                                                  Boolean siblingKesEnd,
                                                  String... keys) {
        CResult<Object> cResult = getResult(operationArgs);
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
        //data数据
        String resultByLevelKey = JSONUtil.toJsonStr(cResult.getData());
        Map<String, Object> mapResult = Maps.newHashMap();
        //data下同级对应key的字段取出放入map
        if (!siblingKes.isEmpty()) {
            for (String key : siblingKes) {
                //data下对应key的字段取出放入map
                String str = JSONUtil.parseObj(resultByLevelKey).getStr(key);
                if (JSONUtil.isTypeJSON(str)) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    // 设置属性命名策略为SNAKE_CASE_TO_CAMEL_CASE
                    objectMapper.setPropertyNamingStrategy(operationArgs.getPropertyNamingStrategy());
                    // 将JSON字符串读取为Map对象
                    try {
                        Map map = objectMapper.readValue(str, Map.class);
                        //再次序列化为json字符串
                        mapResult.put(key, objectMapper.writeValueAsString(map));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            if (Boolean.TRUE.equals(siblingKesEnd)) {
                return CResult.success(mapResult);
            }
        }

        //keys为dada下嵌套子集的集合,查询最底层的一个key的内容
        if (ArrayUtil.isNotEmpty(keys)) {
            for (String key : keys) {
                //嵌套查询对应key的字段,获取到最终结果放入resultByLevelKey,给下面类型转换使用
                resultByLevelKey = JSONUtil.parseObj(resultByLevelKey).getStr(key);
                if (CharSequenceUtil.isBlank(resultByLevelKey) || resultByLevelKey.startsWith("[]")) {
                    log.info("end and return empty----------------success post url:{},param:{}", operationArgs.getUrl(),
                            Boolean.TRUE.equals(operationArgs.getIsPrintArgsLog()) ?
                                    CharSequenceUtil.subPre(JSONUtil.toJsonStr(operationArgs.getParams()),
                                            operationArgs.getPrintLength()) : "");
                    return CResult.success();
                }
            }
        }
        log.info("end----------------success,post url:{},param:{}", operationArgs.getUrl(),
                Boolean.TRUE.equals(operationArgs.getIsPrintArgsLog()) ?
                        CharSequenceUtil.subPre(JSONUtil.toJsonStr(operationArgs.getParams()),
                                operationArgs.getPrintLength()) : "");
        //不是json,直接返回
        if (!JSONUtil.isTypeJSON(resultByLevelKey)) {
            mapResult.put(Operation.RETURN_TYPE_SINGLE, resultByLevelKey);
        }
        if (resultByLevelKey.startsWith("[")) {
            //返回结果为list
            mapResult.put(Operation.RETURN_TYPE_LIST, JSONUtil.toList(JSONUtil.parseArray(resultByLevelKey),
                    tClass));
            return CResult.success(mapResult);
        }
        //返回map
        if (tClass.equals(Object.class)) {
            mapResult.putAll(JSONUtil.parseObj(resultByLevelKey));
            return CResult.success(mapResult);
        }
        //返回单对象
        mapResult.put(Operation.RETURN_TYPE_SINGLE, JSONUtil.toBean(resultByLevelKey, tClass));
        return CResult.success(mapResult);
    }

}
