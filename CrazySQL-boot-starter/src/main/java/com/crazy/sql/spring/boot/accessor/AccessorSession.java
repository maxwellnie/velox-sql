package com.crazy.sql.spring.boot.accessor;

import com.crazy.sql.core.Accessor;
import com.crazy.sql.core.cahce.manager.CacheManager;
import com.crazy.sql.core.exception.ConnectionPoolBusyException;
import com.crazy.sql.core.exception.SQLExecutorBuildException;
import com.crazy.sql.core.executor.SimpleSQLExecutor;
import com.crazy.sql.core.query.QueryWord;
import com.crazy.sql.core.utils.SQLUtils;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

public class AccessorSession<T> extends Accessor<T>{
    private SimpleSQLExecutor<T> executor;
    private DataSource pool;
    private SQLUtils<T> sqlUtils;
    private CacheManager cacheManager;
    public AccessorSession(Accessor<T> accessor){
        this.pool= accessor.getPool();
        this.cacheManager=accessor.getCacheManager();
        this.executor= accessor.getExecutor();
        this.sqlUtils=accessor.getSqlUtils();
    }


    public SimpleSQLExecutor<T> getExecutor() {
        if(sqlUtils==null){
            throw new SQLExecutorBuildException("not set SQLUtils!");
        }else if(pool==null){
            throw new SQLExecutorBuildException("doesn't have connectionPool");
        }
        try {
            executor.setConnection(DataSourceUtils.getConnection(pool));
            executor.setSQLUtils(sqlUtils);
            executor.setCacheManager(cacheManager);
        } catch (ConnectionPoolBusyException e) {
            e.printStackTrace();
        }
        return executor;
    }

    public void setExecutor(SimpleSQLExecutor<T> executor) {
        this.executor = executor;
    }
    public DataSource getPool() {
        return pool;
    }

    public void setPool(DataSource pool) {
        this.pool = pool;
    }

    public SQLUtils<T> getSqlUtils() {
        return sqlUtils;
    }

    public void setSqlUtils(SQLUtils<T> sqlUtils) {
        this.sqlUtils = sqlUtils;
    }

    @Override
    public int insert(T t) throws SQLException {
        return getExecutor().insert(t);
    }

    @Override
    public int update(T t) throws SQLException {
        return getExecutor().update(t);
    }

    @Override
    public int delete(T t) throws SQLException {
        return getExecutor().delete(t);
    }

    @Override
    public T queryOne(T t) throws SQLException {
        return getExecutor().queryOne(t);
    }

    @Override
    public List<T> queryAll() throws SQLException {
        return getExecutor().queryAll();
    }
    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
    @Override
    public List<T> queryByWords(QueryWord... queryWords) throws SQLException {
        return getExecutor().queryByWords(queryWords);
    }
}
