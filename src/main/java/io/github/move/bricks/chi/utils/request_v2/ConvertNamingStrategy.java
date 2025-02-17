package io.github.move.bricks.chi.utils.request_v2;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

/**
 * json 转换命名策略
 *
 * @author MoveBricks Chi
 * @version 1.0
 * @since 2.1.0
 */
public final class ConvertNamingStrategy {

    public static PropertyNamingStrategy of(String currentNamingStrategy) {
        return switch (currentNamingStrategy) {
            case NamingStrategyConstants.LOWER_CAMEL_CASE -> PropertyNamingStrategies.LOWER_CAMEL_CASE;
            case NamingStrategyConstants.UPPER_CAMEL_CASE -> PropertyNamingStrategies.UPPER_CAMEL_CASE;
            case NamingStrategyConstants.SNAKE_CASE -> PropertyNamingStrategies.SNAKE_CASE;
            case NamingStrategyConstants.UPPER_SNAKE_CASE -> PropertyNamingStrategies.UPPER_SNAKE_CASE;
            case NamingStrategyConstants.LOWER_CASE -> PropertyNamingStrategies.LOWER_CASE;
            case NamingStrategyConstants.KEBAB_CASE -> PropertyNamingStrategies.KEBAB_CASE;
            case NamingStrategyConstants.LOWER_DOT_CASE -> PropertyNamingStrategies.LOWER_DOT_CASE;
            default -> throw new IllegalStateException("Unexpected value: " + currentNamingStrategy);
        };

    }

}
