package io.github.move.bricks.chi.utils.request;

import io.github.move.bricks.chi.constants.LccConstants;

import java.io.Serializable;

/**
 * 返回配置
 *
 * @author MoveBricks Chi
 * @version 1.0
 * @since 1.0
 */
public class ReturnConfig implements Serializable {

    /**
     * 设置状态码默认返回的字段，默认为code
     */
    private String returnCodeField = "code";
    /**
     * 设置返回值默认返回的字段，默认为data
     */
    private String returnDataField = "data";
    /**
     * 设置消息默认返回的字段，默认为message
     */
    private String returnMessageField = "message";

    /**
     * 第三方正确返回的状态码,默认200
     */
    private Integer returnSuccessCode = LccConstants.SuccessEnum.SUCCESS_2_HUNDRED.getCode();

    /**
     * 业务需要返回的状态码
     */
    private Integer bizReturnSuccessCode = LccConstants.SuccessEnum.SUCCESS_ZERO.getCode();

    public ReturnConfig() {
    }

    public ReturnConfig(Integer bizReturnSuccessCode, String returnCodeField, String returnDataField,
                        String returnMessageField, Integer returnSuccessCode) {
        this.bizReturnSuccessCode = bizReturnSuccessCode;
        this.returnCodeField = returnCodeField;
        this.returnDataField = returnDataField;
        this.returnMessageField = returnMessageField;
        this.returnSuccessCode = returnSuccessCode;
    }

    public Integer getBizReturnSuccessCode() {
        return bizReturnSuccessCode;
    }

    public void setBizReturnSuccessCode(Integer bizReturnSuccessCode) {
        this.bizReturnSuccessCode = bizReturnSuccessCode;
    }

    public String getReturnCodeField() {
        return returnCodeField;
    }

    public void setReturnCodeField(String returnCodeField) {
        this.returnCodeField = returnCodeField;
    }

    public String getReturnDataField() {
        return returnDataField;
    }

    public void setReturnDataField(String returnDataField) {
        this.returnDataField = returnDataField;
    }

    public String getReturnMessageField() {
        return returnMessageField;
    }

    public void setReturnMessageField(String returnMessageField) {
        this.returnMessageField = returnMessageField;
    }

    public Integer getReturnSuccessCode() {
        return returnSuccessCode;
    }

    public void setReturnSuccessCode(Integer returnSuccessCode) {
        this.returnSuccessCode = returnSuccessCode;
    }
}
