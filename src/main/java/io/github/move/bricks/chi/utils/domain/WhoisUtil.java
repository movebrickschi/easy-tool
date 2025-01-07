package io.github.move.bricks.chi.utils.domain;

import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.move.bricks.chi.dto.WhoisDTO;
import io.github.move.bricks.chi.vo.DomainWhoisInfoBaseVo;
import io.github.move.bricks.chi.enums.DomainWhoisEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.whois.WhoisClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 获取域名信息工具
 * @author Liu Chunchi
 * @version 1.0
 */
@Slf4j
public class WhoisUtil {

    protected static final List<String> COM_DOMAIN_PROPERTIES = Lists.newArrayList();

    protected static final List<String> CN_DOMAIN_PROPERTIES = Lists.newArrayList();

    protected static final Map<String, List<String>> MAP_PROPERTIES = Maps.newHashMap();

    protected static final Map<String, String> WHOIS_SEVER = Maps.newHashMap();

    protected static final String SPLIT_SYMBOL = ": ";


    static {
        COM_DOMAIN_PROPERTIES.add("Updated Date");
        COM_DOMAIN_PROPERTIES.add("Creation Date");
        COM_DOMAIN_PROPERTIES.add("Registry Expiry Date");
        COM_DOMAIN_PROPERTIES.add("Registrar");
        COM_DOMAIN_PROPERTIES.add("Domain Status");
        COM_DOMAIN_PROPERTIES.add("Name Server");

        CN_DOMAIN_PROPERTIES.add("Domain Status");
        CN_DOMAIN_PROPERTIES.add("Registrant");
        CN_DOMAIN_PROPERTIES.add("Sponsoring Registrar");
        CN_DOMAIN_PROPERTIES.add("Name Server");
        CN_DOMAIN_PROPERTIES.add("Registration Time");
        CN_DOMAIN_PROPERTIES.add("Expiration Time");

        MAP_PROPERTIES.put("cn", CN_DOMAIN_PROPERTIES);
        MAP_PROPERTIES.put("com", COM_DOMAIN_PROPERTIES);


        WHOIS_SEVER.put("cn", "whois.cnnic.cn");
        WHOIS_SEVER.put("com", "whois.verisign-grs.com");

    }


    /**
     * 获取域名详细信息
     * @param domain 域名
     * @return vo.io.github.move.bricks.chi.DomainWhoisInfoBaseVo
     */
    public static DomainWhoisInfoBaseVo getDomainInfo(String domain) {
        String domainSubfix = domain.split("\\.")[1];

        DomainWhoisInfoBaseVo reuslt = new DomainWhoisInfoBaseVo();
        WhoisClient whois = new WhoisClient();

        try {
            whois.connect(WHOIS_SEVER.get(domainSubfix));
            String query = whois.query(domain);
            log.info(query);
            String[] split = query.split("\n");
            Map<String, List<String>> map = new HashMap<>();
            String key;
            String value;
            for (String s : split) {
                key = s.split(SPLIT_SYMBOL)[0].trim();
                if (!MAP_PROPERTIES.get(domainSubfix).contains(key)) {
                    continue;
                }
                value = s.split(SPLIT_SYMBOL)[1].trim();
                if (null == map.get(key)) {
                    List<String> list = Lists.newArrayList();
                    list.add(value);
                    map.put(key, list);
                } else {
                    map.get(key).add(value);
                }
            }
            map.forEach((k, v) -> formatData.get().get(domainSubfix).accept(new WhoisDTO(k, v, reuslt)));
            whois.disconnect();
        } catch (IOException e) {
            log.error("Error I/O exception: " + e.getMessage());
        }
        return reuslt;
    }


    static Consumer<WhoisDTO> formatByCom = WhoisUtil::formatCom;
    static Consumer<WhoisDTO> formatByCn = WhoisUtil::formatCn;

    static Supplier<Map<String, Consumer<WhoisDTO>>> formatData = () -> {
        Map<String, Consumer<WhoisDTO>> map = Maps.newHashMap();
        map.put("cn", formatByCn);
        map.put("com", formatByCom);
        return map;
    };

    /**
     * 格式化com的域名
     * @param whoisDTO 域名参数
     */
    private static void formatCom(WhoisDTO whoisDTO) {
        switch (whoisDTO.getKey()) {
            case "Creation Date":
                whoisDTO.getReuslt().setRegistrationTime(DateUtil.parse(whoisDTO.getValue().get(0)).toString());
                break;
            case "Registry Expiry Date":
                whoisDTO.getReuslt().setExpirationTime(DateUtil.parse(whoisDTO.getValue().get(0)).toString());
                break;
            case "Registrar":
                whoisDTO.getReuslt().setRegistrar(whoisDTO.getValue().get(0));
                break;
            case "Domain Status":
                whoisDTO.getReuslt().setDomainStatusList(DomainWhoisEnum.DomainStatus.getListByCode(whoisDTO.getValue().get(0)));
                break;
            case "Name Server":
                whoisDTO.getReuslt().setDnsList(whoisDTO.getValue());
                break;
            default:
                break;
        }
    }

    /**
     * 格式化cn的域名
     * @param whoisDTO 域名参数
     */
    private static void formatCn(WhoisDTO whoisDTO) {
        switch (whoisDTO.getKey()) {
            case "Registrant":
                whoisDTO.getReuslt().setCompanyName(whoisDTO.getValue().get(0));
                break;
            case "Registration Time":
                whoisDTO.getReuslt().setRegistrationTime(DateUtil.parse(whoisDTO.getValue().get(0)).toString());
                break;
            case "Expiration Time":
                whoisDTO.getReuslt().setExpirationTime(DateUtil.parse(whoisDTO.getValue().get(0)).toString());
                break;
            case "Sponsoring Registrar":
                whoisDTO.getReuslt().setRegistrar(whoisDTO.getValue().get(0));
                break;
            case "Domain Status":
                whoisDTO.getReuslt().setDomainStatusList(DomainWhoisEnum.DomainStatus.getListByCode(whoisDTO.getValue().get(0)));
                break;
            case "Name Server":
                whoisDTO.getReuslt().setDnsList(whoisDTO.getValue());
                break;
            default:
                break;
        }
    }

}
