package io.github.movebrickschi.easytool.core.utils.object;

import cn.hutool.core.text.CharSequenceUtil;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import io.github.movebrickschi.easytool.core.constants.NamingStrategyConstants;
import lombok.extern.slf4j.Slf4j;

/**
 * json 转换命名策略
 *
 * @author MoveBricks Chi
 * @version 1.0
 * @since 3.0.0
 */
@Slf4j
public final class ConvertNamingStrategy {
    private ConvertNamingStrategy() {
        //
    }

    public static PropertyNamingStrategy of(String currentNamingStrategy) {
        if (CharSequenceUtil.isBlank(currentNamingStrategy)) {
            log.info("not set naming strategy");
            return null;
        }
        return switch (currentNamingStrategy) {
            case NamingStrategyConstants.LOWER_CAMEL_CASE -> PropertyNamingStrategies.LOWER_CAMEL_CASE;
            case NamingStrategyConstants.UPPER_CAMEL_CASE -> PropertyNamingStrategies.UPPER_CAMEL_CASE;
            case NamingStrategyConstants.SNAKE_CASE -> PropertyNamingStrategies.SNAKE_CASE;
            case NamingStrategyConstants.UPPER_SNAKE_CASE -> PropertyNamingStrategies.UPPER_SNAKE_CASE;
            case NamingStrategyConstants.LOWER_CASE -> PropertyNamingStrategies.LOWER_CASE;
            case NamingStrategyConstants.KEBAB_CASE -> PropertyNamingStrategies.KEBAB_CASE;
            case NamingStrategyConstants.LOWER_DOT_CASE -> PropertyNamingStrategies.LOWER_DOT_CASE;
            default -> throw new IllegalStateException("Unexpected naming strategy: " + currentNamingStrategy);
        };

    }

}
