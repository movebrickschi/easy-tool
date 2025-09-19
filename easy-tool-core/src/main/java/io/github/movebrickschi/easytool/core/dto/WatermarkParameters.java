package io.github.movebrickschi.easytool.core.dto;

import io.github.movebrickschi.easytool.core.enums.PositionEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 水印参数
 *
 * @author MoveBricks Chi
 * @since 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class WatermarkParameters implements Serializable {
    private static final long serialVersionUID = 440804206374041656L;

    /*水印文字*/
    private String text;
    /*水印位置*/
    @Builder.Default
    private String position = PositionEnum.BOTTOM_RIGHT.getPosition();
    /*水印透明度*/
    @Builder.Default
    private Integer alpha = 128;
    /*水印文字大小*/
    @Builder.Default
    private Integer size = 40;
    /**
     * 字体名称,不传则从系统支持的字体中选择
     */
    private String fontName;

    /**
     * 旋转角度，仅适用于pdf水印
     * 默认是30度
     */
    @Builder.Default
    private float rotation = 30f;

    /**
     * 水印间隔字体的倍数,默认是字体的6倍
     */
    @Builder.Default
    private float multiplier = 6.0f;

    public Integer getAlpha() {
        if (alpha < 0 || alpha > 255) {
            // 超出范围时使用默认值
            alpha = 128;
        }
        return alpha;
    }

    public Integer getSize() {
        // 验证文字大小范围
        if (size < 10 || size > 200) {
            // 超出范围时使用默认值
            size = 40;
        }
        return size;
    }
}
