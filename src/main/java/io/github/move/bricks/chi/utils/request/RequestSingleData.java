
package io.github.move.bricks.chi.utils.request;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import io.github.move.bricks.chi.constants.LccConstants;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 请求返回单例类型
 *
 * @author Liu Chunchi
 * @version 1.0
 */
@Slf4j
public class RequestSingleData implements Operation {
    @Override
    public <T> CResult<T> toSingle(CResult<Object> CResult, OperationArgs operationArgs, Class<T> tClass, String... keys) {
//        CResult CResult = Operation.getResult(operationArgs, tClass, null, keys);
        if (CResult.getCode().intValue() == LccConstants.FAIL.intValue()) {
            return CResult.failed(CResult.getMessage());
        }

        log.info("start----------------single format request:{},url:{},param:{}", operationArgs.getMethod(), operationArgs.getUrl(),
                Boolean.TRUE.equals(operationArgs.getIsPrintArgsLog()) ?
                        CharSequenceUtil.subPre(JSONUtil.toJsonStr(operationArgs.getParams()), operationArgs.getPrintLength()) : "");
        //返回为空
        if (Objects.isNull(CResult.getData())) {
            log.info("end and return empty----------------success request url:{},param:{}", operationArgs.getUrl(),
                    JSONUtil.toJsonStr(operationArgs.getParams()));
            return CResult.success();
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
                    return CResult.success();
                }
            }
        }

        //基本数据类型或者string
        if (tClass.isPrimitive() || String.class.equals(tClass) || tClass.getGenericSuperclass().equals(Number.class)) {
            log.info("end----------------success,base type single request url:{},param:{},CResult:{}", operationArgs.getUrl(),
                    Boolean.TRUE.equals(operationArgs.getIsPrintResultLog()) ?
                            CharSequenceUtil.subPre(JSONUtil.toJsonStr(CResult.getData()), operationArgs.getPrintLength()) : "");
            return CResult.success((T) CResult.getData());
        }
        return CResult.success(JSONUtil.toBean(resultByLevelKey, tClass));
    }
}
