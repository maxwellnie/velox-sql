package com.maxwellnie.velox.sql.core.natives.jdbc.session;

import com.maxwellnie.velox.sql.core.cache.transactional.CacheTransactional;
import com.maxwellnie.velox.sql.core.natives.jdbc.transaction.Transaction;
import com.maxwellnie.velox.sql.core.natives.task.TaskQueue;

import java.io.Closeable;

/**
 * 会话对象，限定方法执行器的工作区间。
 *
 * @author Maxwell Nie
 */
public interface JdbcSession extends Closeable {
    /**
     * 获取自动提交
     *
     * @return
     */
    boolean getAutoCommit();

    /**
     * 设置自动提交
     *
     * @param flag
     */
    void setAutoCommit(boolean flag);

    /**
     * 关闭环境
     */
    void close();
    /**
     * 关闭环境
     */
    void close(boolean commit);
    /**
     * 提交操作
     */
    void commit();

    /**
     * 回滚操作
     */
    void rollback();

    /**
     * 获取事务对象
     *
     * @return
     */
    Transaction getTransaction();

    /**
     * 获取脏数据管理器
     *
     * @return
     */
    CacheTransactional getDirtyManager();

    /**
     * 是否被关闭
     *
     * @return
     */
    boolean isClosed();
    TaskQueue getTaskQueue();
}
