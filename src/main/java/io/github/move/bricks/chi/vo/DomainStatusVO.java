package io.github.move.bricks.chi.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 域名状态vo
 *
 * @author MoveBricks Chi
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DomainStatusVO implements Serializable {

    private static final long serialVersionUID = -3259244361006581077L;
    /**
     * 域名状态：0正常，1过期
     */
    private int code;
    /**
     * 域名状态原始数据
     */
    private String codeStr;
    /**
     * 描述
     */
    private String name;
    /**
     * 详细描述
     */
    private String desc;
}
