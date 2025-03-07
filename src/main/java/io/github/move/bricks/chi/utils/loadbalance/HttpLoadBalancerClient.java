package io.github.move.bricks.chi.utils.loadbalance;

import cn.hutool.core.text.CharSequenceUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 描述:
 *
 * @author MoveBricks Chi
 * @version 1.0
 */
@AllArgsConstructor
public class HttpLoadBalancerClient implements HttpLoadBalancer {

    public static final String PREFIX_FOR_HTTPS = "https://";

    public static final String PREFIX_FOR_HTTP = "http://";

    private static synchronized String loadBalance(String... domains) {
        WeightRandomLoadBalancer.ADDRESS_MAP.clear();
        String prefix = null;
        if (domains.length == 1) {
            String domain = domains[0];
            prefix = prefixOf(domain).getPrefix();
            domain = prefixOf(domain).getDomain();
            domain = domain.contains("/") ? domain.split("/")[0] : domain;
            return prefix + domain;
        }
        for (String domain : domains) {
            prefix = prefixOf(domain).getPrefix();
            domain = prefixOf(domain).getDomain();
            String[] split = domain.split("/");
            WeightRandomLoadBalancer.ADDRESS_MAP.put(split[0], Integer.valueOf(split[1]));
        }
        return prefix + WeightRandomLoadBalancer.weightRandom();
    }

    private static LoadBalancerClientHolder prefixOf(String domain) {
        if (domain.startsWith(PREFIX_FOR_HTTPS)) {
            return LoadBalancerClientHolder.builder()
                    .domain(domain.replace(PREFIX_FOR_HTTPS, ""))
                    .prefix(PREFIX_FOR_HTTPS)
                    .build();
        }
        if (domain.startsWith(PREFIX_FOR_HTTP)) {
            return LoadBalancerClientHolder.builder()
                    .domain(domain.replace(PREFIX_FOR_HTTP, ""))
                    .prefix(PREFIX_FOR_HTTP)
                    .build();
        }
        return LoadBalancerClientHolder.builder()
                .domain(domain)
                .prefix(PREFIX_FOR_HTTP)
                .build();
    }

    @Override
    public String chooseDynamic(List<String> urls) {
        return loadBalance(urls.toArray(String[]::new));
    }

    @Override
    public String chooseDynamic(String urls) {
        if (CharSequenceUtil.isBlank(urls)) {
            return null;
        }
        return loadBalance(urls.split(","));
    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class LoadBalancerClientHolder {
        private String prefix;
        private String domain;
    }
}
