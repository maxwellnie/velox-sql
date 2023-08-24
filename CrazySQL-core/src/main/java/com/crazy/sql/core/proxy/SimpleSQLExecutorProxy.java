package com.crazy.sql.core.proxy;

import com.crazy.sql.core.cahce.Cache;
import com.crazy.sql.core.cahce.manager.CacheManager;
import com.crazy.sql.core.executor.SimpleSQLExecutor;
import com.crazy.sql.core.executor.impl.NotCallBackSimpleSQLExecutor;
import com.crazy.sql.core.executor.impl.StandSimpleSQLExecutor;
import com.crazy.sql.core.query.QueryWord;
import com.crazy.sql.core.utils.CacheUtils;
import com.crazy.sql.core.utils.DirtyDataUtils;
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
        try {
            rowCount=executor.insert(t);
            if(rowCount>0&&getCacheManager()!=null){
                List<T> list=new ArrayList<>();
                list.add(t);
                executor.getSqlUtils().setPrimaryKeyValue(t,rowCount);
                DirtyDataUtils.putInsertDirty(getConnection(),executor.getSqlUtils().getPrimaryKeyValue(t),list,rowCount,executor.getSqlUtils());
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return rowCount;
    }

    @Override
    public int update(T t){
        int rowCount=0;
        try {
            rowCount=executor.update(t);
            if(rowCount>0&&getCacheManager()!=null){
                List<T> list=new ArrayList<>();
                list.add(t);
                executor.getSqlUtils().setPrimaryKeyValue(t,rowCount);
                DirtyDataUtils.putUpdateDirty(getConnection(),executor.getSqlUtils().getPrimaryKeyValue(t),list,rowCount,executor.getSqlUtils());
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return rowCount;
    }

    @Override
    public int delete(T t){
        int rowCount=0;
        try {
            rowCount=executor.delete(t);
            if(rowCount>0&&getCacheManager()!=null){
                List<String> list=new ArrayList<>();
                list.add(executor.getSqlUtils().getPrimaryKeyValue(t));
                executor.getSqlUtils().setPrimaryKeyValue(t,rowCount);
                DirtyDataUtils.putDeleteDirty(getConnection(),executor.getSqlUtils().getPrimaryKeyValue(t),list,rowCount,executor.getSqlUtils());
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return rowCount;
    }

    @Override
    public T queryOne(T t) throws SQLException {
        T result = null;
        if(getCacheManager()!=null){
            String key=executor.getSqlUtils().getTableName()+"queryOne";
            if(getCacheManager().hasCache(key)){
                Cache<String,T> cache=getCacheManager().getCache(key);
                result= cache.get(executor.getSqlUtils().getPrimaryKeyValue(t));
                if(result==null) {
                    result = executor.queryOne(t);
                    cache.put(executor.getSqlUtils().getPrimaryKeyValue(t), result);
                }
            }else {
                result = executor.queryOne(t);
                Cache<String,T> cache=getCacheManager().getCache(key);
                cache.put(executor.getSqlUtils().getPrimaryKeyValue(t), result);
            }

        }else
            result=executor.queryOne(t);
        return result;
    }

    @Override
    public List<T> queryAll() throws SQLException {
        List<T> resultList=null;
        if(getCacheManager()!=null){
            String key=executor.getSqlUtils().getTableName()+"queryAll";
            if(getCacheManager().hasCache(key)){
                Cache<String,T> cache=getCacheManager().getCache(key);
                resultList= new ArrayList<>(cache.values());
                if(resultList.size()==0) {
                    resultList=executor.queryAll();
                    resultList.forEach((x)->{
                        cache.put(executor.getSqlUtils().getPrimaryKeyValue(x),x);
                    });
                }
            }else {
                resultList=executor.queryAll();
                Cache<String,T> cache=getCacheManager().getCache(key);
                resultList.forEach((x)->{
                    cache.put(executor.getSqlUtils().getPrimaryKeyValue(x),x);
                });
            }
        }else
            resultList=executor.queryAll();
        return resultList;
    }

    @Override
    public List<T> queryByWords(QueryWord... queryWords) throws SQLException {
        List<T> resultList=null;
        if(getCacheManager()!=null){
            String key=executor.getSqlUtils().getTableName()+"queryByWords"+ StringUtils.queryWordsArrayToString(queryWords);
            if(getCacheManager().hasCache(key)){
                Cache<String,T> cache=getCacheManager().getCache(key);
                resultList= new ArrayList<>(cache.values());
            }else {
                resultList = executor.queryByWords(queryWords);
                Cache<String, T> cache = getCacheManager().getCache(key);
                resultList.forEach((x) -> {
                    cache.put(executor.getSqlUtils().getPrimaryKeyValue(x), x);
                });
            }
        }else
            resultList=executor.queryByWords(queryWords);
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
