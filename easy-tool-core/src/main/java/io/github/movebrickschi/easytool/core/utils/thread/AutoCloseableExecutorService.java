package io.github.movebrickschi.easytool.core.utils.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * ry-with-resources 语句中自动关闭线程池
 * 必须等待所有任务执行完毕才会去自动关闭，如下示例必须等待CompletableFuture执行完毕
 * 使用示例：
 * <code>
 *     try (AutoCloseableExecutorService namedExecutor = new AutoCloseableExecutorService(
 *         Executors.newCachedThreadPool(new NamedThreadFactory("MyCustomThread")))) {
 *     CompletableFuture.runAsync(() -> {
 *         System.out.println("Running on thread: " + Thread.currentThread().getName());
 *     }, namedExecutor);
 *     // 线程池会在 try-with-resources 块结束时自动关闭
 * }
 * </code>
 *
 * @author MoveBricks Chi
 * @since 1.0
 */
@Slf4j
public class AutoCloseableExecutorService implements ExecutorService, AutoCloseable {
    private final ExecutorService executorService;

    public AutoCloseableExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    /**
     * 首先调用shutdown()平滑关闭线程池，不再接受新任务
     * 等待最多60秒让已提交任务执行完毕
     * 如果超时未完成，调用shutdownNow()强制中断所有正在执行的任务
     * 再等待60秒看是否能正常关闭
     * 如果仍未能关闭，记录错误日志
     * 如果等待过程中被中断，也调用shutdownNow()并恢复中断状态
     */
    @Override
    public void close() {
        shutdown();
        try {
            if (!awaitTermination(60, TimeUnit.SECONDS)) {
                log.info("停止所有正在执行的任务");
                shutdownNow();
                if (!awaitTermination(60, TimeUnit.SECONDS)) {
                    log.error("线程池未能正常关闭");
                }
            }
        } catch (InterruptedException e) {
            shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    // 委托所有 ExecutorService 方法到内部的 executorService
    @Override
    public void shutdown() {
        executorService.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return executorService.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return executorService.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return executorService.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return executorService.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return executorService.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return executorService.submit(task, result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return executorService.submit(task);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return executorService.invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return executorService.invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return executorService.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return executorService.invokeAny(tasks, timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        executorService.execute(command);
    }
}
