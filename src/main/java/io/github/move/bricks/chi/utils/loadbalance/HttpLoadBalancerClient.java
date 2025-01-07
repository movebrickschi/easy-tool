package io.github.move.bricks.chi.utils.loadbalance;

import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 描述:
 *
 * @author Liu Chunchi
 * @version 1.0
 */
@AllArgsConstructor
public class HttpLoadBalancerClient implements HttpLoadBalancer {

    public static final String PREFIX = "http://";

    private static synchronized String loadBalance(String... domains) {
        WeightRandomLoadBalancer.addressMap.clear();
        if (domains.length == 1) {
            return PREFIX + (domains[0].contains("/") ? domains[0].split("/")[0] : domains[0]);
        }
        for (String domain : domains) {
            String[] split = domain.split("/");
            WeightRandomLoadBalancer.addressMap.put(split[0], Integer.valueOf(split[1]));
        }
        return PREFIX + WeightRandomLoadBalancer.weightRandom();
    }

    @Override
    public String chooseDynamic(List<String> urls) {
        return loadBalance(urls.toArray(String[]::new));
    }

    @Override
    public String chooseDynamic(String urls) {
        return loadBalance(urls.split(","));
    }
}
