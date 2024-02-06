
package com.lcc.tool.utils.request;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import com.lcc.tool.constants.LccConstants;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 请求返回单例类型
 *
 * @author Liu Chunchi
 * @date 2023/8/30 19:29
 */
@Slf4j
public class RequestSingleData implements Operation {
    @Override
    public <T> Result<T> toSingle(Result<Object> result, OperationArgs operationArgs, Class<T> tClass, String... keys) {
//        Result result = Operation.getResult(operationArgs, tClass, null, keys);
        if (result.getCode().intValue() == LccConstants.FAIL.intValue()) {
            return Result.failed(result.getMessage());
        }

        log.info("start----------------single format request:{},url:{},param:{}", operationArgs.getMethod(), operationArgs.getUrl(),
                JSONUtil.toJsonStr(operationArgs.getParams()));
        //返回为空
        if (Objects.isNull(result.getData())) {
            log.info("end and return empty----------------success request url:{},param:{}", operationArgs.getUrl(),
                    JSONUtil.toJsonStr(operationArgs.getParams()));
            return Result.success();
        }

        String resultByLevelKey = JSONUtil.toJsonStr(result.getData());
        //keys为子集的key
        if (!Objects.isNull(keys)) {
            for (String key : keys) {
                resultByLevelKey = JSONUtil.parseObj(resultByLevelKey).getStr(key);
                if (CharSequenceUtil.isBlank(resultByLevelKey) || resultByLevelKey.startsWith("[]")) {
                    log.info("end and return empty----------------success post url:{},param:{}", operationArgs.getUrl(),
                            JSONUtil.toJsonStr(operationArgs.getParams()));
                    return Result.success();
                }
            }
        }

        //基本数据类型或者string
        if (tClass.isPrimitive() || String.class.equals(tClass) || tClass.getGenericSuperclass().equals(Number.class)) {
            log.info("end----------------success,base type single request url:{},param:{},result:{}", operationArgs.getUrl(),
                    JSONUtil.toJsonStr(operationArgs.getParams()), result.getData());
            return Result.success((T) result.getData());
        }
        return Result.success(JSONUtil.toBean(resultByLevelKey, tClass));
    }
}
