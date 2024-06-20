package com.maxwellnie.velox.sql.core.natives.jdbc.transaction;

import com.maxwellnie.velox.sql.core.natives.jdbc.session.ConnectionHolder;
import com.maxwellnie.velox.sql.core.natives.resource.ResourceHolder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * DaoImpl的事务
 *
 * @author Maxwell Nie
 */
public interface Transaction extends ConnectionHolder, ResourceHolder<DataSource> {
    /**
     * 数据回滚
     *
     * @throws SQLException
     */
    void rollback() throws SQLException;

    /**
     * 数据提交
     *
     * @throws SQLException
     */
    void commit() throws SQLException;

    /**
     * 释放连接
     *
     * @throws SQLException
     */
    void release() throws SQLException;
}
