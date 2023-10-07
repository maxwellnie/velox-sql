package com.maxwellnie.vleox.jpa.core.proxy;

import com.maxwellnie.vleox.jpa.core.cahce.Cache;
import com.maxwellnie.vleox.jpa.core.exception.DaoImplClassException;
import com.maxwellnie.vleox.jpa.core.jdbc.context.JdbcContext;
import com.maxwellnie.vleox.jpa.core.jdbc.table.TableInfo;

import java.lang.reflect.Proxy;

/**
 * @author Maxwell Nie
 */
public class DaoImplFactory<T> {
    private Class<T> daoInterfaceClass;
    private TableInfo tableInfo;
    private Cache cache;

    public DaoImplFactory(Class<T> daoInterfaceClass, TableInfo tableInfo, Cache cache) {
        this.daoInterfaceClass = daoInterfaceClass;
        this.tableInfo = tableInfo;
        this.cache = cache;
    }

    public T produce(JdbcContext jdbcContext) {
        if (daoInterfaceClass.isInterface()) {
            return (T) Proxy.newProxyInstance(daoInterfaceClass.getClassLoader(), new Class[]{daoInterfaceClass},
                    new DaoImplInvokeHandler(tableInfo, jdbcContext, cache));
        } else
            throw new DaoImplClassException();
    }
}
