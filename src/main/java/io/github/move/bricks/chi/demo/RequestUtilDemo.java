package io.github.move.bricks.chi.demo;

import cn.hutool.core.io.resource.InputStreamResource;
import io.github.move.bricks.chi.constants.LccConstants;
import io.github.move.bricks.chi.utils.request.*;
import io.github.move.bricks.chi.utils.request_v2.OperationArgsV2;
import io.github.move.bricks.chi.utils.request_v2.ReturnConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * 请求工具类使用示例
 *
 * @author MoveBricks Chi
 * @version 1.0
 */
@Slf4j
public class RequestUtilDemo {

    /**
     * MultipartFile方式文件上传
     * 必须以inputStream流上传
     * @param file 文件
     */
    public void uploadFile(MultipartFile file) {
        CResult<Map<String, Object>> uploadResult = RequestUtil.parseMap(OperationArgsV2.builder()
                .param(Map.of("key", getInputStream(file)))
                .method(Operation.Method.POST_FORM)
                .application(Operation.Application.MULTIPART_FORM_DATA)
                .returnConfig(ReturnConfig.builder()
                        .returnSuccessCode(LccConstants.SuccessEnum.SUCCESS_ZERO.getCode())
                        .build())
                .url("url")
                .build());
    }


    /**
     * 获取文件流
     * @param file 文件
     * @return 文件流
     */
    private InputStreamResource getInputStream(MultipartFile file) {
        InputStreamResource isr = null;
        try {
            isr = new InputStreamResource(file.getInputStream(), file.getOriginalFilename());
        } catch (IOException e) {
            log.info("文件流转换异常:{}", e.getMessage());
        }
        return isr;
    }
}
