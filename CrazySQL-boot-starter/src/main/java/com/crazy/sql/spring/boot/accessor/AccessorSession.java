package com.crazy.sql.spring.boot.accessor;

import com.crazy.sql.core.Accessor;
import com.crazy.sql.core.cahce.manager.CacheManager;
import com.crazy.sql.core.exception.ConnectionPoolBusyException;
import com.crazy.sql.core.exception.SQLExecutorBuildException;
import com.crazy.sql.core.executor.SimpleSQLExecutor;
import com.crazy.sql.core.query.QueryWord;
import com.crazy.sql.core.utils.SQLUtils;
import com.crazy.sql.spring.boot.datasource.TransactionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

/**
 * 聚合的实体类，代理了SimpleSQLExecutor，用于实现数据库操作功能
 * @param <T>
 */
public class AccessorSession<T> {
    private static Logger logger= LoggerFactory.getLogger(AccessorSession.class);
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
            executor.setConnection(TransactionUtils.getConnection(pool,getCacheManager()));
            executor.setSQLUtils(sqlUtils);
            executor.setCacheManager(cacheManager);
        } catch (ConnectionPoolBusyException e) {
            e.printStackTrace();
        }
        logger.info(executor.toString());
        logger.info(executor.getConnection().toString());
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

    public int insert(T t) throws SQLException {
        return getExecutor().insert(t);
    }

    public int update(T t) throws SQLException {
        return getExecutor().update(t);
    }

    public int delete(T t) throws SQLException {
        return getExecutor().delete(t);
    }

    public T queryOne(T t) throws SQLException {
        return getExecutor().queryOne(t);
    }

    public List<T> queryAll() throws SQLException {
        return getExecutor().queryAll();
    }
    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
    public List<T> queryByWords(QueryWord... queryWords) throws SQLException {
        return getExecutor().queryByWords(queryWords);
    }
}