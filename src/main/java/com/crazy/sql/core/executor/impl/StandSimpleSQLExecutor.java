package com.crazy.sql.core.executor.impl;

import com.crazy.sql.core.cahce.Cache;
import com.crazy.sql.core.cahce.manager.CacheManager;
import com.crazy.sql.core.executor.SimpleSQLExecutor;
import com.crazy.sql.core.pool.ConnectionPool;
import com.crazy.sql.core.query.QueryWord;
import com.crazy.sql.core.utils.CacheUtils;
import com.crazy.sql.core.utils.SQLUtils;
import com.crazy.sql.core.utils.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 可回收数据库连接的SQLExecutor
 * @param <T>
 */
public class StandSimpleSQLExecutor<T> implements SimpleSQLExecutor<T> {
    private SQLUtils<T> sqlUtils;
    private Connection connection;
    private ConnectionPool pool;
    private CacheManager cacheManager;
    public int insert(T t) throws SQLException {
        PreparedStatement preparedStatement= sqlUtils.insert(t,connection);
        int primaryKeyValue=0;
        preparedStatement.executeUpdate();
        ResultSet rs= preparedStatement.getGeneratedKeys();
        while (rs.next()){
            primaryKeyValue=rs.getInt(1);
        }
        if(primaryKeyValue>0&&getCacheManager()!=null){
            List<T> list=new ArrayList<>();
            list.add(t);
            sqlUtils.setPrimaryKeyValue(t,primaryKeyValue);
            CacheUtils.updateCache(getCacheManager(),sqlUtils,list);
        }
        getPool().callBack(getConnection());
        return primaryKeyValue;
    }
    public int update(T t) throws SQLException {
        PreparedStatement preparedStatement= sqlUtils.update(t,connection);
        int row= preparedStatement.executeUpdate();
        if(row>0&&getCacheManager()!=null){
            List<T> list=new ArrayList<>();
            list.add(t);
            CacheUtils.updateCache(getCacheManager(),sqlUtils,list);
        }
        getPool().callBack(getConnection());
        return row;
    }
    public int delete(T t) throws SQLException {
        PreparedStatement preparedStatement= sqlUtils.delete(t,connection);
        int row= preparedStatement.executeUpdate();
        if(row>0&&getCacheManager()!=null){
            List<String> list=new ArrayList<>();
            list.add(sqlUtils.getPrimaryKeyValue(t));
            CacheUtils.deleteCache(getCacheManager(),sqlUtils,list);
        }
        getPool().callBack(getConnection());
        return row;
    }
    @Override
    public T queryOne(T t) throws SQLException {
        T result=null;
        if(getCacheManager()!=null){
            String key=sqlUtils.getTableName()+"queryOne";
            if(getCacheManager().hasCache(key)){
                getPool().callBack(getConnection());
                Cache<String,T> cache=getCacheManager().getCache(key);
                return cache.get(sqlUtils.getPrimaryKeyValue(t));
            }
            result=sqlUtils.queryOne(t,getConnection());
            Cache<String,T> cache=getCacheManager().getCache(key);
            cache.put(sqlUtils.getPrimaryKeyValue(t),t);
            getPool().callBack(getConnection());
        }
        result=sqlUtils.queryOne(t,connection);
        return result;
    }

    @Override
    public List<T> queryAll() throws SQLException {
        List<T> resultList=null;
        if(getCacheManager()!=null){
            String key=sqlUtils.getTableName()+"queryAll";
            if(getCacheManager().hasCache(key)){
                getPool().callBack(getConnection());
                Cache<String,T> cache=getCacheManager().getCache(key);
                return new ArrayList<>(cache.values());
            }
            resultList=sqlUtils.queryAll(getConnection());
            Cache<String,T> cache=getCacheManager().getCache(key);
            resultList.forEach((x)->{
                cache.put(sqlUtils.getPrimaryKeyValue(x),x);
            });
        }
        resultList=sqlUtils.queryAll(getConnection());
        getPool().callBack(getConnection());
        return resultList;
    }

    @Override
    public List<T> queryByWords(QueryWord... queryWords) throws SQLException {
        List<T> resultList=null;
        if(getCacheManager()!=null){
            String key=sqlUtils.getTableName()+"queryByWords"+ StringUtils.queryWordsArrayToString(queryWords);
            if(getCacheManager().hasCache(key)){
                getPool().callBack(getConnection());
                Cache<String,T> cache=getCacheManager().getCache(key);
                return new ArrayList<>(cache.values());
            }
            resultList=sqlUtils.queryByWords(getConnection(),queryWords);
            Cache<String,T> cache=getCacheManager().getCache(key);
            resultList.forEach((x)->{
                cache.put(sqlUtils.getPrimaryKeyValue(x),x);
            });
        }
        resultList=sqlUtils.queryByWords(getConnection(),queryWords);
        getPool().callBack(getConnection());
        return resultList;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void setPool(ConnectionPool pool) {
        this.pool=pool;
    }

    @Override
    public ConnectionPool getPool() {
        return pool;
    }

    @Override
    public void setSQLUtils(SQLUtils<T> sqlUtils) {
        this.sqlUtils=sqlUtils;
    }

    @Override
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager=cacheManager;
    }

    @Override
    public CacheManager getCacheManager() {
        return cacheManager;
    }

}
