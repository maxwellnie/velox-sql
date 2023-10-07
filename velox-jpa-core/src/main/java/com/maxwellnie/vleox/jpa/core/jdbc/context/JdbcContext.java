package com.maxwellnie.vleox.jpa.core.jdbc.context;

import com.maxwellnie.vleox.jpa.core.cahce.dirty.CacheDirtyManager;
import com.maxwellnie.vleox.jpa.core.jdbc.transaction.Transaction;

/**
 * 执行器操作数据库的必要环境
 *
 * @author Maxwell Nie
 */
public interface JdbcContext {
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
    CacheDirtyManager getDirtyManager();
}
