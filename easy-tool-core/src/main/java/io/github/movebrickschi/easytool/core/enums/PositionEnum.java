package io.github.movebrickschi.easytool.core.enums;

import io.github.movebrickschi.easytool.core.exception.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 方位信息枚举
 *
 * @author MoveBricks Chi
 * @since 1.0
 */
@Getter
@AllArgsConstructor
public enum PositionEnum {

    TOP_LEFT("top-left", "左上"),
    TOP_RIGHT("top-right", "右上"),
    BOTTOM_LEFT("bottom-left", "左下"),
    BOTTOM_RIGHT("bottom-right", "右下"),
    CENTER("center", "居中"),
    ;

    private final String position;
    private final String desc;

    public static PositionEnum getPosition(String position) {
        return Arrays.stream(PositionEnum.values()).filter(item -> item.getPosition().equals(position))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("未知位置！"));
    }


}
