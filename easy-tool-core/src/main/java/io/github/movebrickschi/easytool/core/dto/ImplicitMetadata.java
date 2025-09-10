package io.github.movebrickschi.easytool.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 隐式元数据参数
 *
 * @author MoveBricks Chi
 * @since 1.0
 */
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class ImplicitMetadata implements Serializable {


    private static final long serialVersionUID = -8756741772475949512L;
    /**
     * 标识内容属性：1=“属于AI生成合成”，2=“可能为AI生成合成”，3=“疑似为AI生成合成”
     */
    @JsonProperty("Label")
    @Builder.Default
    private String label = "1";
    /**
     * 生成合成服务提供者编码
     * 和producerInfo互斥，两者选其一,优先使用contentProducer
     */
    @JsonProperty("ContentProducer")
    private String contentProducer;

    /**
     * 和contentProducer互斥，两者选其一,优先使用contentProducer
     * 使用此属性，会根据内置属性自动生成编码
     */
    @JsonIgnore
    private ProducerInfo producerInfo;

    /**
     * 元数据key
     */
    @JsonIgnore
    private String key;

    /**
     * 你企业对这条视频内容的唯一编号（如UUID或内部ID）
     */
    @JsonProperty("ProduceID")
    private String produceId;
    /**
     * 可选，用于数字签名或完整性校验，如无特殊要求可留空
     */
    @JsonProperty("ReservedCode1")
    private String reservedCode1;
    /**
     * 内容传播服务提供者编码（如你自己传播，则与ContentProducer一致）
     */
    @JsonProperty("ContentPropagator")
    private String contentPropagator;
    /**
     * 内容传播编号（如你自己传播，则与ProduceID一致）
     */
    @JsonProperty("PropagateID")
    private String propagateId;
    /**
     * 可选，用于数字签名或完整性校验，如无特殊要求可留空
     */
    @JsonProperty("ReservedCode2")
    private String reservedCode2;

    /**
     * 服务提供者编码的提供者信息
     *
     * @author MoveBricks Chi
     * @since 1.0
     */
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class ProducerInfo implements Serializable {

        private static final long serialVersionUID = -6344744505679263615L;
        /**
         * 第1-2位,编码规则版本
         */
        @Builder.Default
        private String version = "00";
        /**
         * 第3位,主体类型（组织=1,个人=2）,
         */
        @Builder.Default
        private String subjectType = "1";
        /**
         * 第4位绑定方式
         * 企业：1代表统一社会信用代码
         * 个人：1代表身份证号码
         */
        @Builder.Default
        private String bindingWay = "1";
        /**
         * 第5-22位,统一社会信用代码或身份证号码
         */
        private String subjectCode;
        /**
         * 第23-27位,服务扩展码,不设置默认00000
         */
        @Builder.Default
        private String serviceExtensionCode = "00000";

    }

}
