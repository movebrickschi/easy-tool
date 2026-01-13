package io.github.movebrickschi.easytool.core.exception;

/**
 * 未找到异常
 *
 * @author MoveBricks Chi
 * @since 1.0
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
