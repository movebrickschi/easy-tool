package io.github.movebrickschi.easytool.core.dto;

import io.github.movebrickschi.easytool.core.vo.DomainWhoisInfoBaseVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author MoveBricks Chi
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WhoisDTO {

    private String key;
    private List<String> value;
    private DomainWhoisInfoBaseVo reuslt;
}
