package io.github.movebrickschi.easytool.request.constants;



import io.github.movebrickschi.easytool.request.core.Operation;

import java.util.Arrays;
import java.util.List;

/**
 * 请求常量
 *
 * @author MoveBricks Chi
 * @version 1.0
 * @since 3.0.0
 */
public final class RequestConstants {

    /**
     * 表单请求方法
     */
    public static final List<Operation.Method> FORM_METHODS = Arrays.asList(Operation.Method.POST_FORM,
            Operation.Method.POST_FORM_WITH_HEADERS, Operation.Method.GET,
            Operation.Method.GET_HEADERS);
}
