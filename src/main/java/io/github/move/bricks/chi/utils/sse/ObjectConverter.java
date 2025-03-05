package io.github.move.bricks.chi.utils.sse;

import io.github.move.bricks.chi.constants.NamingStrategyConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 参数转换器
 *
 * @author MoveBricks Chi
 * @version 1.0
 * @since 2.1.4
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ObjectConverter {

    /**
     * 参数值
     * @since 2.1.4
     */
    private Object object;
    /**
     * 用于传入参数对象，例如将字段caseIdList转换为case_id_list
     * {@link NamingStrategyConstants}
     * @since 2.1.4
     */
    private String writePropertyNamingStrategy = null;
    /**
     * 忽略字段，将指定字段排移除
     * 使用此功能，必须在对应实体类上使用@{@link com.fasterxml.jackson.annotation.JsonFilter}
     * @since 2.1.4
     */
    private String[] ignoreFields = null;
}