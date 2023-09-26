package com.crazy.sql.core.jdbc.transaction;

import com.crazy.sql.core.jdbc.context.JdbcConnectionUser;

import java.sql.SQLException;

/**
 * Accessor的事务
 * @author Akiba no ichiichiyoha
 */
public interface Transaction extends JdbcConnectionUser {
    /**
     * 数据回滚
     * @throws SQLException
     */
    void rollback() throws SQLException;

    /**
     * 数据提交
     * @throws SQLException
     */
    void commit() throws SQLException;

    /**
     * 释放连接
     * @throws SQLException
     */
    void release() throws SQLException;
}
