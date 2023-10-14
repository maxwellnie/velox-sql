package com.maxwellnie.velox.jpa.core.proxy;

import com.maxwellnie.velox.jpa.core.exception.DaoImplClassException;
import com.maxwellnie.velox.jpa.core.jdbc.table.TableInfo;
import com.maxwellnie.velox.jpa.core.cahce.Cache;
import com.maxwellnie.velox.jpa.core.jdbc.context.JdbcContext;

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

    public Class<T> getDaoInterfaceClass() {
        return daoInterfaceClass;
    }

    public<B extends T> T produce(JdbcContext jdbcContext) {
        if (daoInterfaceClass.isInterface()) {
            return (B) Proxy.newProxyInstance(daoInterfaceClass.getClassLoader(), new Class[]{daoInterfaceClass},
                    new DaoImplInvokeHandler(tableInfo, jdbcContext, cache));
        } else
            throw new DaoImplClassException();
    }
}
