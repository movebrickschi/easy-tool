package io.github.move.bricks.chi.utils.request;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import io.github.move.bricks.chi.constants.LccConstants;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 返回集合类型
 *
 * @author Liu Chunchi
 * @version 1.0
 */
@Slf4j
public class RequestArrayListData implements Operation {

    @Override
    public <T> Result<List<T>> toList(Result<Object> result, OperationArgs operationArgs, Class<T> tClass, String... keys) {
        if (result.getCode().intValue() == LccConstants.FAIL.intValue()) {
            return Result.failed(result.getMessage());
        }
        log.info("start----------------single format request:{},url:{},param:{}", operationArgs.getMethod(), operationArgs.getUrl(),
                Boolean.TRUE.equals(operationArgs.getIsPrintArgsLog()) ?
                        CharSequenceUtil.subPre(JSONUtil.toJsonStr(operationArgs.getParams()), operationArgs.getPrintLength()) : "");
        //返回为空
        if (Objects.isNull(result.getData()) || String.valueOf(result.getData()).startsWith("[]")) {
            log.info("end and return empty----------------success request url:{},param:{}", operationArgs.getUrl(),
                    Boolean.TRUE.equals(operationArgs.getIsPrintArgsLog()) ?
                            CharSequenceUtil.subPre(JSONUtil.toJsonStr(operationArgs.getParams()), operationArgs.getPrintLength()) : "");
            return Result.success(Collections.emptyList());
        }
        String resultByLevelKey = JSONUtil.toJsonStr(result.getData());
        //keys为子集的key
        if (!Objects.isNull(keys)) {
            for (String key : keys) {
                resultByLevelKey = JSONUtil.parseObj(resultByLevelKey).getStr(key);
                if (CharSequenceUtil.isBlank(resultByLevelKey) || resultByLevelKey.startsWith("[]")) {
                    log.info("end and return empty----------------success post url:{},param:{}", operationArgs.getUrl(),
                            Boolean.TRUE.equals(operationArgs.getIsPrintArgsLog()) ?
                                    CharSequenceUtil.subPre(JSONUtil.toJsonStr(operationArgs.getParams()), operationArgs.getPrintLength()) : "");
                    return Result.success(Collections.emptyList());
                }
            }
        }

        return Result.success(JSONUtil.toList(JSONUtil.parseArray(resultByLevelKey), tClass));
    }

}
