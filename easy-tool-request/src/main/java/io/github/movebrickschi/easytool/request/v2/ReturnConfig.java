package io.github.movebrickschi.easytool.request.v2;

import io.github.movebrickschi.easytool.core.constants.LccConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 返回配置
 *
 * @author MoveBricks Chi
 * @version 1.0
 * @since 2.1.11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReturnConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1816452701159515651L;
    /**
     * 设置状态码默认返回的字段，默认为code
     */
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
    private int returnSuccessCode = LccConstants.SuccessEnum.SUCCESS_2_HUNDRED.getCode();

    /**
     * 业务需要返回的状态码
     */
    @Builder.Default
    private int bizReturnSuccessCode = LccConstants.SuccessEnum.SUCCESS_ZERO.getCode();

    public ReturnConfig(int returnSuccessCode) {
        this.returnSuccessCode = returnSuccessCode;
    }
}
