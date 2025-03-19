package io.github.movebrickschi.easytool.core.utils.thread;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.extern.slf4j.Slf4j;

/**
 * 多线程线程池共享变量拷贝工具类
 *
 * @author MoveBricks Chi
 * @version 1.0
 */
@Slf4j
public class TransmittableThreadLocalUtils {

    private static volatile TransmittableThreadLocal<Long> INSTANCE;

    private TransmittableThreadLocalUtils() {
        INSTANCE = new TransmittableThreadLocal<Long>();
        log.info("TransmittableThreadLocalUtils创建完成");
    }


    public static TransmittableThreadLocal<Long> getInstance() {
        if (null == INSTANCE) {
            synchronized (TransmittableThreadLocalUtils.class) {
                if (INSTANCE == null) {
                    new TransmittableThreadLocalUtils();
                }
            }
        }
        return INSTANCE;
    }


}
