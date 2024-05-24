package io.github.liuchunchiuse.utils.request;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import io.github.liuchunchiuse.constants.LccConstants;
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
    public <T> Result<Map<String, Object>> toMap(Result<Object> result, OperationArgs operationArgs, Class<T> tClass, List<String> siblingKes,
                                                 String... keys) {
        if (result.getCode().intValue() == LccConstants.FAIL.intValue()) {
            return Result.failed(result.getMessage());
        }
        log.info("start----------------single format request:{},url:{},param:{}", operationArgs.getMethod(), operationArgs.getUrl(),
                Boolean.TRUE.equals(operationArgs.getIsPrintArgsLog()) ?
                        CharSequenceUtil.subPre(JSONUtil.toJsonStr(operationArgs.getParams()), operationArgs.getPrintLength()) : "");
        //返回为空
        if (Objects.isNull(result.getData())) {
            log.info("end and return empty----------------success request url:{},param:{}", operationArgs.getUrl(),
                    Boolean.TRUE.equals(operationArgs.getIsPrintArgsLog()) ?
                            CharSequenceUtil.subPre(JSONUtil.toJsonStr(operationArgs.getParams()), operationArgs.getPrintLength()) : "");
            return Result.success();
        }
        String resultByLevelKey = JSONUtil.toJsonStr(result.getData());
        Map<String, Object> mapResult = Maps.newHashMap();

        if (!siblingKes.isEmpty()) {
            for (String key : siblingKes) {
                mapResult.put(key, JSONUtil.parseObj(resultByLevelKey).getStr(key));
            }
        }

        //keys为子集的key
        if (!Objects.isNull(keys)) {
            for (String key : keys) {
                resultByLevelKey = JSONUtil.parseObj(resultByLevelKey).getStr(key);
                if (CharSequenceUtil.isBlank(resultByLevelKey) || resultByLevelKey.startsWith("[]")) {
                    log.info("end and return empty----------------success post url:{},param:{}", operationArgs.getUrl(),
                            Boolean.TRUE.equals(operationArgs.getIsPrintArgsLog()) ?
                                    CharSequenceUtil.subPre(JSONUtil.toJsonStr(operationArgs.getParams()), operationArgs.getPrintLength()) : "");
                    return Result.success();
                }
            }
        }
        log.info("end----------------success,post url:{},param:{}", operationArgs.getUrl(),
                Boolean.TRUE.equals(operationArgs.getIsPrintArgsLog()) ?
                        CharSequenceUtil.subPre(JSONUtil.toJsonStr(operationArgs.getParams()), operationArgs.getPrintLength()) : "");
        if (resultByLevelKey.startsWith("[")) {
            //集合
            mapResult.put(RETURN_TYPE_LIST, JSONUtil.toList(JSONUtil.parseArray(resultByLevelKey), ThreadLocal.class));
            return Result.success(mapResult);
        }
        //单对象
        mapResult.put(RETURN_TYPE_SINGLE, JSONUtil.toBean(resultByLevelKey, tClass));
        return Result.success(mapResult);

    }
}
