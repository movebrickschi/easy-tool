package io.github.movebrickschi.easytool.core.utils.loadbalance;

import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 负载均衡策略
 *
 * @author MoveBricks Chi
 * @version 1.0
 */
@Configuration
public class WeightRandomLoadBalancer {

    //定义服务器列表及服务器权重值
    protected static final Map<String, Integer> ADDRESS_MAP = new ConcurrentHashMap<>();

    //记录服务器权重总和
    public static String weightRandom() {
        AtomicInteger totalWeight = new AtomicInteger(0);
        //获取服务器数量
        int serverCount = ADDRESS_MAP.size();
        //如果没有可用的服务器返回null
        if (serverCount == 0) {
            return null;
        }
        //在此处为避免多线程并发操作造成错误，在方法内部进行锁操作
        synchronized (ADDRESS_MAP) {
            //计算服务器权重总和
            for (Map.Entry<String, Integer> entry : ADDRESS_MAP.entrySet()) {
                totalWeight.addAndGet(entry.getValue());
            }
            //生成一个随机数
            int randomWeight = new Random().nextInt(totalWeight.get());
            //遍历服务器列表，根据服务器权重值选择对应地址
            for (Map.Entry<String, Integer> entry : ADDRESS_MAP.entrySet()) {
                String serverAddress = entry.getKey();
                Integer weight = entry.getValue();
                randomWeight -= weight;
                if (randomWeight < 0) {
                    return serverAddress;
                }
            }
        }
        //默认返回null
        return null;
    }


}
