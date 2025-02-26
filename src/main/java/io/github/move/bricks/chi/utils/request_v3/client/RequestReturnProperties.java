package io.github.move.bricks.chi.utils.request_v3.client;

import io.github.move.bricks.chi.constants.LccConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * RequestReturnProperties
 *
 * @author Liu Chunchi
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RequestReturnProperties implements Serializable {

    @Builder.Default
    private String returnCodeField = "code";
    /**
     * 设置返回值默认返回的字段，默认为data
     */
    @Builder.Default
    private String returnDataField = "data";
    /**
     * 设置消息默认返回的字段，默认为message
     */
    @Builder.Default
    private String returnMessageField = "message";

    /**
     * 第三方正确返回的状态码,默认200
     */
    @Builder.Default
    private Integer returnSuccessCode = LccConstants.SuccessEnum.SUCCESS_2_HUNDRED.getCode();

    /**
     * 业务需要返回的状态码
     */
    @Builder.Default
    private Integer bizReturnSuccessCode = LccConstants.SuccessEnum.SUCCESS_ZERO.getCode();
}
