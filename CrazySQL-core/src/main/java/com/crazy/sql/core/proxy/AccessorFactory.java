package com.crazy.sql.core.proxy;

import com.crazy.sql.core.cahce.Cache;
import com.crazy.sql.core.exception.AccessorClassException;
import com.crazy.sql.core.jdbc.context.JdbcContext;
import com.crazy.sql.core.jdbc.table.TableInfo;

import java.lang.reflect.Proxy;

/**
 * @author Akiba no ichiichiyoha
 */
public class AccessorFactory<T> {
    private Class<T> accessor;
    private TableInfo tableInfo;
    private Cache cache;

    public AccessorFactory(Class<T> accessor, TableInfo tableInfo, Cache cache) {
        this.accessor = accessor;
        this.tableInfo = tableInfo;
        this.cache=cache;
    }
    public T produce(JdbcContext jdbcContext) {
        if(accessor.isInterface()){
            return (T) Proxy.newProxyInstance(accessor.getClassLoader(),new Class[]{accessor},
                    new AccessorInvokeHandler(tableInfo,jdbcContext,cache));
        }else
            throw new AccessorClassException();
    }
}
