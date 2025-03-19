package io.github.movebrickschi.easytool.core.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * 域名whois信息(DomainWhoisInfo)Vo类
 *
 * @author MoveBricks Chi
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DomainWhoisInfoBaseVo implements Serializable {
    private static final long serialVersionUID = 822682713001394866L;

    /**
     * 域名
     */
    private String domain;
    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 注册商
     */
    private String registrar;
    /**
     * 域名状态：0正常，1过期
     */
    private Integer domainStatus;

    /**
     * 注册时间
     */
    private String registrationTime;
    /**
     * 到期时间
     */
    private String expirationTime;
    /**
     * dns列表
     */
    private List<String> dnsList;

    /**
     * 域名状态列表
     */
    private List<DomainStatusVO> domainStatusList;


}
