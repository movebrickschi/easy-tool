package io.github.liuchunchiuse.utils.request;

import cn.hutool.json.JSONUtil;
import io.github.liuchunchiuse.constants.LccConstants;
import lombok.extern.slf4j.Slf4j;

/**
 * 请求没有数据
 * @author Liu Chunchi
 * @date 2023/8/30 19:27
 */
@Slf4j
public class RequestNoData implements Operation {
    @Override
    public Result noData(Result<Object> result, OperationArgs operationArgs) {
        if (result.getCode().intValue() == LccConstants.FAIL.intValue()) {
            return Result.failed(result.getMessage());
        }
        log.info("end----------------success,request url:{},param:{}", operationArgs.getUrl(),
                JSONUtil.toJsonStr(operationArgs.getParams()));
        return result;
    }


}
