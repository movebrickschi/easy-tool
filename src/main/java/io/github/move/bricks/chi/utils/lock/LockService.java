package io.github.move.bricks.chi.utils.lock;

import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 分布式锁封装方法
 *
 * @author MoveBricks Chi
 * @version 1.0
 */
@Component
@Slf4j
@ConditionalOnBean(RedissonClient.class)
public class LockService {

    @Resource
    private RedissonClient redissonClient;

    public <T> T executeWithLockThrows(String key, int waitTime, TimeUnit unit, SupplierThrow<T> supplier) throws Throwable {
        RLock lock = redissonClient.getLock(key);
        boolean lockSuccess = lock.tryLock(waitTime, unit);
        if (!lockSuccess) {
            throw new RuntimeException("请求太频繁了，请稍后再试哦~~");
        }
        try {
            return supplier.get();//执行锁内的代码逻辑
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }


    public void executeWithLockThrows(String key, int waitTime, TimeUnit unit, SupplierWithNoReturn supplier) throws Throwable {
        RLock lock = redissonClient.getLock(key);
        boolean lockSuccess = lock.tryLock(waitTime, unit);
        if (!lockSuccess) {
            throw new RuntimeException("请求太频繁了，请稍后再试哦~~");
        }
        try {
            supplier.apply();//执行锁内的代码逻辑
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @SneakyThrows
    public <T> T executeWithLock(String key, int waitTime, TimeUnit unit, Supplier<T> supplier) {
        return executeWithLockThrows(key, waitTime, unit, supplier::get);
    }

    @SneakyThrows
    public void executeWithLock(String key, int waitTime, TimeUnit unit, SupplierWithNoReturn supplier) {
        executeWithLockThrows(key, waitTime, unit, supplier);
    }

    public <T> T executeWithLock(String key, Supplier<T> supplier) {
        return executeWithLock(key, -1, TimeUnit.MILLISECONDS, supplier);
    }


    @FunctionalInterface
    public interface SupplierWithNoReturn {
        void apply();
    }


    @FunctionalInterface
    public interface SupplierThrow<T> {

        /**
         * Gets a result.
         *
         * @return a result
         */
        T get();
    }
}