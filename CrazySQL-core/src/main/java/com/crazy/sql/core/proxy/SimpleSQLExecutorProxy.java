package com.crazy.sql.core.proxy;

import com.crazy.sql.core.executor.impl.NotCallBackSimpleSQLExecutor;
import com.crazy.sql.core.cahce.Cache;
import com.crazy.sql.core.cahce.manager.CacheManager;
import com.crazy.sql.core.executor.SimpleSQLExecutor;
import com.crazy.sql.core.query.QueryWord;
import com.crazy.sql.core.utils.CacheUtils;
import com.crazy.sql.core.utils.SQLUtils;
import com.crazy.sql.core.utils.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用代理模式来实现数据库事务
 * @param <T>
 */
public class SimpleSQLExecutorProxy<T> implements SimpleSQLExecutor<T> {
    private NotCallBackSimpleSQLExecutor<T> executor;

    public SimpleSQLExecutorProxy(NotCallBackSimpleSQLExecutor<T> executor) {
        this.executor = executor;
    }

    @Override
    public int insert(T t) {
        int rowCount=0;
        Connection execConnection=getConnection();
        try {
            execConnection.setAutoCommit(false);
            rowCount=executor.insert(t);
            execConnection.commit();
            if(rowCount>0&&getCacheManager()!=null){
                List<T> list=new ArrayList<>();
                list.add(t);
                executor.getSqlUtils().setPrimaryKeyValue(t,rowCount);
                CacheUtils.updateCache(getCacheManager(),executor.getSqlUtils(),list);
            }
        }catch (SQLException e){
            try {
                execConnection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }finally {
            try {
                execConnection.setAutoCommit(true);
                getConnection().close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return rowCount;
    }

    @Override
    public int update(T t){
        int rowCount=0;
        Connection execConnection=getConnection();
        try {
            execConnection.setAutoCommit(false);
            rowCount=executor.update(t);
            execConnection.commit();
            if(rowCount>0&&getCacheManager()!=null){
                List<T> list=new ArrayList<>();
                list.add(t);
                executor.getSqlUtils().setPrimaryKeyValue(t,rowCount);
                CacheUtils.updateCache(getCacheManager(),executor.getSqlUtils(),list);
            }
        }catch (SQLException e){
            try {
                execConnection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }finally {
            try {
                execConnection.setAutoCommit(true);
                getConnection().close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return rowCount;
    }

    @Override
    public int delete(T t){
        int rowCount=0;
        Connection execConnection=getConnection();
        try {
            execConnection.setAutoCommit(false);
            rowCount=executor.delete(t);
            execConnection.commit();
            if(rowCount>0&&getCacheManager()!=null){
                List<String> list=new ArrayList<>();
                list.add(executor.getSqlUtils().getPrimaryKeyValue(t));
                executor.getSqlUtils().setPrimaryKeyValue(t,rowCount);
                CacheUtils.deleteCache(getCacheManager(),executor.getSqlUtils(),list);
            }
        }catch (SQLException e){
            try {
                execConnection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }finally {
            try {
                execConnection.setAutoCommit(true);
                getConnection().close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return rowCount;
    }

    @Override
    public T queryOne(T t) throws SQLException {
        T result=null;
        if(getCacheManager()!=null){
            String key=executor.getSqlUtils().getTableName()+"queryOne";
            if(getCacheManager().hasCache(key)){
                getConnection().close();
                Cache<String,T> cache=getCacheManager().getCache(key);
                return cache.get(executor.getSqlUtils().getPrimaryKeyValue(t));
            }
            result=executor.queryOne(t);
            Cache<String,T> cache=getCacheManager().getCache(key);
            cache.put(executor.getSqlUtils().getPrimaryKeyValue(t),t);
            getConnection().close();
        }
        result=executor.queryOne(t);
        return result;
    }

    @Override
    public List<T> queryAll() throws SQLException {
        List<T> resultList=null;
        if(getCacheManager()!=null){
            String key=executor.getSqlUtils().getTableName()+"queryAll";
            if(getCacheManager().hasCache(key)){
                getConnection().close();
                Cache<String,T> cache=getCacheManager().getCache(key);
                return new ArrayList<>(cache.values());
            }
            resultList=executor.queryAll();
            Cache<String,T> cache=getCacheManager().getCache(key);
            resultList.forEach((x)->{
                cache.put(executor.getSqlUtils().getPrimaryKeyValue(x),x);
            });
        }
        resultList=executor.queryAll();
        getConnection().close();
        return resultList;
    }

    @Override
    public List<T> queryByWords(QueryWord... queryWords) throws SQLException {
        List<T> resultList=null;
        if(getCacheManager()!=null){
            String key=executor.getSqlUtils().getTableName()+"queryByWords"+ StringUtils.queryWordsArrayToString(queryWords);
            if(getCacheManager().hasCache(key)){
                getConnection().close();
                Cache<String,T> cache=getCacheManager().getCache(key);
                return new ArrayList<>(cache.values());
            }
            resultList=executor.queryByWords(queryWords);
            Cache<String,T> cache=getCacheManager().getCache(key);
            resultList.forEach((x)->{
                cache.put(executor.getSqlUtils().getPrimaryKeyValue(x),x);
            });
        }
        resultList=executor.queryByWords(queryWords);
        getConnection().close();
        return resultList;
    }

    @Override
    public void setConnection(Connection connection) {
        this.executor.setConnection(connection);
    }

    @Override
    public Connection getConnection() {
        return this.executor.getConnection();
    }

    @Override
    public void setSQLUtils(SQLUtils<T> sqlUtils) {
        this.executor.setSQLUtils(sqlUtils);
    }

    @Override
    public void setCacheManager(CacheManager cacheManager) {
        this.executor.setCacheManager(cacheManager);
    }

    @Override
    public CacheManager getCacheManager() {
        return this.executor.getCacheManager();
    }
}
