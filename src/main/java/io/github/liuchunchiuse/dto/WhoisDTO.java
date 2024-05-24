package io.github.liuchunchiuse.dto;

import io.github.liuchunchiuse.vo.DomainWhoisInfoBaseVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Liu Chunchi
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
