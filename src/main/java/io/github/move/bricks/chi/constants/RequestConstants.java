package io.github.move.bricks.chi.constants;

import io.github.move.bricks.chi.utils.request.Operation;

import java.util.Arrays;
import java.util.List;

/**
 * 请求常量
 *
 * @author MoveBricks Chi
 * @version 1.0
 * @since 2.1.11
 */
public final class RequestConstants {

    /**
     * 表单请求方法
     */
    public static final List<Operation.Method> FORM_METHODS = Arrays.asList(Operation.Method.POST_FORM,
            Operation.Method.POST_FORM_WITH_HEADERS, Operation.Method.GET,
            Operation.Method.GET_HEADERS);
}
