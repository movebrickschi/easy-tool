package io.github.move.bricks.chi.utils.loadbalance;

import java.util.List;

/**
 * 负载均衡工具类
 *
 * @author Liu Chunchi
 * @version 1.0
 */
public interface HttpLoadBalancer {


    /**
     * @param urls 包含权重的链接
     * @return 返回的链接
     */
    String chooseDynamic(String urls);

    /**
     * @param urls 包含权重的链接
     * @return 返回的链接
     */
    String chooseDynamic(List<String> urls);

}
