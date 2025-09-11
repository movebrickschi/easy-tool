package io.github.movebrickschi.easytool.core.utils.thread;

import cn.hutool.core.text.CharSequenceUtil;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程命名创建工厂
 *
 * @author moviebrickschi
 */
public class NamedThreadFactory implements ThreadFactory {
    /**
     * 线程前缀
     */
    private final String prefix;
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    public NamedThreadFactory(String prefix) {
        if (CharSequenceUtil.isBlank(prefix)) {
            this.prefix = "custom";
        } else {
            this.prefix = prefix;
        }
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, prefix + "-thread-" + threadNumber.getAndIncrement());
    }
}
