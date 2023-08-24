package com.crazy.sql.core.executor.impl;

import com.crazy.sql.core.cahce.Cache;
import com.crazy.sql.core.cahce.manager.CacheManager;
import com.crazy.sql.core.executor.SimpleSQLExecutor;
import com.crazy.sql.core.jdbc.AutoCallBackConnection;
import com.crazy.sql.core.pool.impl.SimpleConnectionPool;
import com.crazy.sql.core.query.QueryWord;
import com.crazy.sql.core.utils.SQLUtils;
import com.crazy.sql.core.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotCallBackSimpleSQLExecutor<T> implements SimpleSQLExecutor<T> {
    private SQLUtils<T> sqlUtils;
    private Connection connection;
    private CacheManager cacheManager;

    public int insert(T t) throws SQLException {
        PreparedStatement preparedStatement = sqlUtils.insert(t, connection);
        int primaryKeyValue = 0;
        preparedStatement.executeUpdate();
        ResultSet rs = preparedStatement.getGeneratedKeys();
        while (rs.next()) {
            primaryKeyValue = rs.getInt(1);
        }
        return primaryKeyValue;
    }

    public int update(T t) throws SQLException {
        PreparedStatement preparedStatement = sqlUtils.update(t, connection);
        return preparedStatement.executeUpdate();
    }

    public int delete(T t) throws SQLException {
        PreparedStatement preparedStatement = sqlUtils.delete(t, connection);
        return preparedStatement.executeUpdate();
    }

    @Override
    public T queryOne(T t) throws SQLException {
        T result = null;
        result = sqlUtils.queryOne(t, connection);
        return result;
    }

    @Override
    public List<T> queryAll() throws SQLException {
        List<T> resultList = null;
        resultList = sqlUtils.queryAll(getConnection());
        return resultList;
    }

    @Override
    public List<T> queryByWords(QueryWord... queryWords) throws SQLException {
        List<T> resultList;
        resultList = sqlUtils.queryByWords(getConnection(), queryWords);
        return resultList;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void setSQLUtils(SQLUtils<T> sqlUtils) {
        this.sqlUtils = sqlUtils;
    }

    public SQLUtils<T> getSqlUtils() {
        return sqlUtils;
    }

    @Override
    public CacheManager getCacheManager() {
        return cacheManager;
    }

    @Override
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
}
