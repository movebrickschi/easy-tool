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
    public <T> CResult<List<T>> toList(CResult<Object> CResult, OperationArgs operationArgs, Class<T> tClass, String... keys) {
        if (CResult.getCode().intValue() == LccConstants.FAIL.intValue()) {
            return CResult.failed(CResult.getMessage());
        }
        log.info("start----------------single format request:{},url:{},param:{}", operationArgs.getMethod(), operationArgs.getUrl(),
                Boolean.TRUE.equals(operationArgs.getIsPrintArgsLog()) ?
                        CharSequenceUtil.subPre(JSONUtil.toJsonStr(operationArgs.getParams()), operationArgs.getPrintLength()) : "");
        //返回为空
        if (Objects.isNull(CResult.getData()) || String.valueOf(CResult.getData()).startsWith("[]")) {
            log.info("end and return empty----------------success request url:{},param:{}", operationArgs.getUrl(),
                    Boolean.TRUE.equals(operationArgs.getIsPrintArgsLog()) ?
                            CharSequenceUtil.subPre(JSONUtil.toJsonStr(operationArgs.getParams()), operationArgs.getPrintLength()) : "");
            return CResult.success(Collections.emptyList());
        }
        String resultByLevelKey = JSONUtil.toJsonStr(CResult.getData());
        //keys为子集的key
        if (!Objects.isNull(keys)) {
            for (String key : keys) {
                resultByLevelKey = JSONUtil.parseObj(resultByLevelKey).getStr(key);
                if (CharSequenceUtil.isBlank(resultByLevelKey) || resultByLevelKey.startsWith("[]")) {
                    log.info("end and return empty----------------success post url:{},param:{}", operationArgs.getUrl(),
                            Boolean.TRUE.equals(operationArgs.getIsPrintArgsLog()) ?
                                    CharSequenceUtil.subPre(JSONUtil.toJsonStr(operationArgs.getParams()), operationArgs.getPrintLength()) : "");
                    return CResult.success(Collections.emptyList());
                }
            }
        }

        return CResult.success(JSONUtil.toList(JSONUtil.parseArray(resultByLevelKey), tClass));
    }

}
