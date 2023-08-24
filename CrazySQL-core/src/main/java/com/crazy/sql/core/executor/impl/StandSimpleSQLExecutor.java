package com.crazy.sql.core.executor.impl;

import com.crazy.sql.core.cahce.Cache;
import com.crazy.sql.core.cahce.manager.CacheManager;
import com.crazy.sql.core.query.QueryWord;
import com.crazy.sql.core.utils.CacheUtils;
import com.crazy.sql.core.executor.SimpleSQLExecutor;
import com.crazy.sql.core.utils.SQLUtils;
import com.crazy.sql.core.utils.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 可回收数据库连接的SQLExecutor
 * @param <T>
 */
public class StandSimpleSQLExecutor<T> implements SimpleSQLExecutor<T> {
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
        if(primaryKeyValue>0&&getCacheManager()!=null){
            List<T> list=new ArrayList<>();
            list.add(t);
            sqlUtils.setPrimaryKeyValue(t,primaryKeyValue);
            CacheUtils.updateCache(getCacheManager(),sqlUtils,list);
        }
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
        return row;
    }
    @Override
    public T queryOne(T t) throws SQLException {
        T result=null;
        if(getCacheManager()!=null){
            String key=sqlUtils.getTableName()+"queryOne";
            if(getCacheManager().hasCache(key)){
                Cache<String,T> cache=getCacheManager().getCache(key);
                result= cache.get(sqlUtils.getPrimaryKeyValue(t));
                if(result==null){
                    result = sqlUtils.queryOne(t, getConnection());
                    cache.put(sqlUtils.getPrimaryKeyValue(t), result);
                }
            }else {
                result = sqlUtils.queryOne(t, getConnection());
                Cache<String, T> cache = getCacheManager().getCache(key);
                cache.put(sqlUtils.getPrimaryKeyValue(t), result);
            }
        }else
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
                resultList= new ArrayList<>(cache.values());
                if(resultList.size()==0){
                    resultList = sqlUtils.queryAll(getConnection());
                    resultList.forEach((x) -> {
                        cache.put(sqlUtils.getPrimaryKeyValue(x), x);
                    });
                }
            }else {
                resultList = sqlUtils.queryAll(getConnection());
                Cache<String, T> cache = getCacheManager().getCache(key);
                resultList.forEach((x) -> {
                    cache.put(sqlUtils.getPrimaryKeyValue(x), x);
                });
            }
        }else
            resultList=sqlUtils.queryAll(getConnection());
        return resultList;
    }

    @Override
    public List<T> queryByWords(QueryWord... queryWords) throws SQLException {
        List<T> resultList=null;
        if(getCacheManager()!=null){
            String key=sqlUtils.getTableName()+"queryByWords"+ StringUtils.queryWordsArrayToString(queryWords);
            if(getCacheManager().hasCache(key)){
                Cache<String,T> cache=getCacheManager().getCache(key);
                resultList= new ArrayList<>(cache.values());
                if(resultList.size()==0){
                    resultList = sqlUtils.queryByWords(getConnection(), queryWords);
                    resultList.forEach((x) -> {
                        cache.put(sqlUtils.getPrimaryKeyValue(x), x);
                    });
                }
            }else {
                resultList = sqlUtils.queryByWords(getConnection(), queryWords);
                Cache<String, T> cache = getCacheManager().getCache(key);
                resultList.forEach((x) -> {
                    cache.put(sqlUtils.getPrimaryKeyValue(x), x);
                });
            }
        }else
            resultList=sqlUtils.queryByWords(getConnection(),queryWords);
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
    public void setSQLUtils(SQLUtils<T> sqlUtils) {
        this.sqlUtils=sqlUtils;
    }

    public SQLUtils<T> getSqlUtils() {
        return sqlUtils;
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
