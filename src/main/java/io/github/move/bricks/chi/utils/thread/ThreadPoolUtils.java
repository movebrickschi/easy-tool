package io.github.move.bricks.chi.utils.thread;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 单例线程池工具类
 *
 * @author MoveBricks Chi
 * @version 1.0
 */
@Slf4j
public class ThreadPoolUtils {

    private static volatile ThreadPoolTaskExecutor INSTANCE;
    //核心线程数
    private static final int CORE_POOL_SIZE = 16;
    //最大线程数
    private static final int MAXIMUM_POOL_SIZE = 32;

    private ThreadPoolUtils() {
        INSTANCE = new ThreadPoolTaskExecutor();
        INSTANCE.setCorePoolSize(CORE_POOL_SIZE);
        INSTANCE.setMaxPoolSize(MAXIMUM_POOL_SIZE);
        INSTANCE.setQueueCapacity(1024);
        INSTANCE.setThreadNamePrefix("async-service-common");
        INSTANCE.setAwaitTerminationSeconds(60);
        INSTANCE.setWaitForTasksToCompleteOnShutdown(true);
        INSTANCE.initialize();
        log.info("PushThreadPoolUtils 线程池创建完成");
    }

    public static ThreadPoolTaskExecutor getInstance() {
        if (null == INSTANCE) {
            synchronized (ThreadPoolUtils.class) {
                if (INSTANCE == null) {
                    new ThreadPoolUtils();
                }
            }
        }
        return INSTANCE;
    }


}
