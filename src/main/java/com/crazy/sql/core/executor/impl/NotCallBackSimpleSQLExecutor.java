package com.crazy.sql.core.executor.impl;

import com.crazy.sql.core.cahce.manager.CacheManager;
import com.crazy.sql.core.executor.SimpleSQLExecutor;
import com.crazy.sql.core.pool.ConnectionPool;
import com.crazy.sql.core.query.QueryWord;
import com.crazy.sql.core.utils.SQLUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 不会自动回收连接的SQLExecutor
 * @param <T>
 */
public class NotCallBackSimpleSQLExecutor<T> implements SimpleSQLExecutor<T> {
    private SQLUtils<T> sqlUtils;
    private Connection connection;
    private CacheManager cacheManager;
    public int insert(T t) throws SQLException {
        PreparedStatement preparedStatement= sqlUtils.insert(t,connection);
        int primaryKeyValue=0;
        preparedStatement.executeUpdate();
        ResultSet rs= preparedStatement.getGeneratedKeys();
        while (rs.next()){
            primaryKeyValue=rs.getInt(1);
        }
        return primaryKeyValue;
    }
    public int update(T t) throws SQLException {
        PreparedStatement preparedStatement= sqlUtils.update(t,connection);
        return preparedStatement.executeUpdate();
    }
    public int delete(T t) throws SQLException {
        PreparedStatement preparedStatement= sqlUtils.delete(t,connection);
        return preparedStatement.executeUpdate();
    }
    public T queryOne(T t) throws SQLException {
        return sqlUtils.queryOne(t,connection);
    }
    public List<T> queryAll() throws SQLException {
        return sqlUtils.queryAll(connection);
    }

    @Override
    public List<T> queryByWords(QueryWord... queryWords) throws SQLException {
        return sqlUtils.queryByWords(connection,queryWords);
    }
    public void setConnection(Connection connection) {
        this.connection = connection;
    }
    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void setSQLUtils(SQLUtils<T> sqlUtils) {
        this.sqlUtils=sqlUtils;
    }

    @Override
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager=cacheManager;
    }

    public SQLUtils<T> getSqlUtils() {
        return sqlUtils;
    }

    @Override
    public CacheManager getCacheManager() {
        return cacheManager;
    }
}
