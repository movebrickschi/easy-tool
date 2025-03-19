package io.github.movebrickschi.easytool.request.v1;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import io.github.movebrickschi.easytool.core.constants.LccConstants;
import io.github.movebrickschi.easytool.request.v2.LogFormatUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 返回集合类型
 *
 * @author MoveBricks Chi
 * @version 1.0
 */
@Slf4j
public class RequestArrayListData implements Operation {

    @Override
    public <T> CResult<List<T>> toList(CResult<Object> cResult, OperationArgs operationArgs, Class<T> tClass, String... keys) {
        if (cResult.getCode().intValue() == LccConstants.FAIL.intValue()) {
            return CResult.failed(cResult.getMessage());
        }
        log.info("start----------------single format v1:{},url:{},param:{}", operationArgs.getMethod(), operationArgs.getUrl(),
                LogFormatUtil.printSubPre(operationArgs.getIsPrintArgsLog(), operationArgs.getParams(),
                        operationArgs.getPrintLength()));
        //返回为空
        if (Objects.isNull(cResult.getData()) || String.valueOf(cResult.getData()).startsWith("[]")) {
            log.info("end and return empty----------------success v1 url:{},param:{}", operationArgs.getUrl(),
                    LogFormatUtil.printSubPre(operationArgs.getIsPrintArgsLog(), operationArgs.getParams(),
                            operationArgs.getPrintLength()));
            return CResult.success(Collections.emptyList());
        }
        String resultByLevelKey = JSONUtil.toJsonStr(cResult.getData());
        //keys为子集的key
        if (ArrayUtil.isNotEmpty(keys)) {
            for (String key : keys) {
                resultByLevelKey = JSONUtil.parseObj(resultByLevelKey).getStr(key);
                if (CharSequenceUtil.isBlank(resultByLevelKey) || resultByLevelKey.startsWith("[]")) {
                    log.info("end and return empty----------------success post url:{},param:{}", operationArgs.getUrl(),
                            LogFormatUtil.printSubPre(operationArgs.getIsPrintArgsLog(), operationArgs.getParams(),
                                    operationArgs.getPrintLength()));
                    return CResult.success(Collections.emptyList());
                }
            }
        }

        return CResult.success(JSONUtil.toList(JSONUtil.parseArray(resultByLevelKey), tClass));
    }

}
