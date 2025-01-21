package io.github.move.bricks.chi.utils.transaction;

import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;


/**
 * 手动事物工具
 *
 * @author MoveBricks Chi
 * @version 1.0
 */

@Component
@ConditionalOnMissingBean(TransactionUtils.class)
@ConditionalOnBean(PlatformTransactionManager.class)
public class TransactionUtils {


    @FunctionalInterface
    public interface TransactionalTask {
        void run();
    }

    //PlatformTransactionManager是Spring框架中用于管理事务的接口。它提供了开启、提交、回滚和验证事务状态等方法
    @Resource
    private final PlatformTransactionManager transactionManager;

    public TransactionUtils(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void runInTransaction(TransactionalTask task) {
        //DefaultTransactionDefinition是Spring框架中的一个默认事务定义类。它实现了//TransactionDefinition接口，提供了一些常用的事务属性和默认值。
        //们使用DefaultTransactionDefinition来创建一个新的事务定义对象，以便开启新的事务并获取对应的事务状态。
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            task.run();
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw e;
        }
    }

}
