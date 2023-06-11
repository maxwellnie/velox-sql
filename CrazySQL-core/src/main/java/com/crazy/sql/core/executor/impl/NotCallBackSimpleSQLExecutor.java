package com.crazy.sql.core.executor.impl;

import com.crazy.sql.core.cahce.Cache;
import com.crazy.sql.core.cahce.manager.CacheManager;
import com.crazy.sql.core.executor.SimpleSQLExecutor;
import com.crazy.sql.core.jdbc.AutoCallBackConnection;
import com.crazy.sql.core.query.QueryWord;
import com.crazy.sql.core.utils.CacheUtils;
import com.crazy.sql.core.utils.DirtyUtils;
import com.crazy.sql.core.utils.SQLUtils;
import com.crazy.sql.core.utils.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 不会自动回收连接的SQLExecutor
 * @param <T>
 */
public class NotCallBackSimpleSQLExecutor<T> implements SimpleSQLExecutor<T> {
    private SQLUtils<T> sqlUtils;
    private AutoCallBackConnection connection;
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
            DirtyUtils.putInsertDirty(connection,primaryKeyValue,t);
        }
        return primaryKeyValue;
    }
    public int update(T t) throws SQLException {
        PreparedStatement preparedStatement= sqlUtils.update(t,connection);
        int row= preparedStatement.executeUpdate();
        if(row>0&&getCacheManager()!=null){
            int primaryKeyValue=Integer.parseInt(sqlUtils.getPrimaryKeyValue(t));
            if(primaryKeyValue>0&&getCacheManager()!=null){
                DirtyUtils.putUpdateDirty(connection,primaryKeyValue,t);
            }
        }
        return row;
    }
    public int delete(T t) throws SQLException {
        PreparedStatement preparedStatement= sqlUtils.delete(t,connection);
        int row= preparedStatement.executeUpdate();
        if(row>0&&getCacheManager()!=null){
            int primaryKeyValue=Integer.parseInt(sqlUtils.getPrimaryKeyValue(t));
            if(primaryKeyValue>0&&getCacheManager()!=null){
                DirtyUtils.putDeleteDirty(connection,primaryKeyValue,t);
            }
        }
        return row;
    }
    @Override
    public T queryOne(T t) throws SQLException {
        T result=null;
        if(getCacheManager()!=null){
            String key=sqlUtils.getTableName()+"queryOne";
            if(getCacheManager().hasCache(key)){
                Cache<String,T> cache=getCacheManager().getCache(key);
                return cache.get(sqlUtils.getPrimaryKeyValue(t));
            }
            result=sqlUtils.queryOne(t,getConnection());
            Cache<String,T> cache=getCacheManager().getCache(key);
            cache.put(sqlUtils.getPrimaryKeyValue(t),t);
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
        return resultList;
    }

    @Override
    public List<T> queryByWords(QueryWord... queryWords) throws SQLException {
        List<T> resultList;
        if(getCacheManager()!=null){
            String key=sqlUtils.getTableName()+"queryByWords"+ StringUtils.queryWordsArrayToString(queryWords);
            if(getCacheManager().hasCache(key)){
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
        return resultList;
    }
    public void setConnection(Connection connection) {
        this.connection = (AutoCallBackConnection) connection;
        ((AutoCallBackConnection) connection).setCacheManager(getCacheManager());
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
