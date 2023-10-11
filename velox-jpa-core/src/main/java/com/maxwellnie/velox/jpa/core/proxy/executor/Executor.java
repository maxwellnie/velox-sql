package com.maxwellnie.velox.jpa.core.proxy.executor;

import com.maxwellnie.velox.jpa.core.cahce.Cache;
import com.maxwellnie.velox.jpa.core.jdbc.context.JdbcContext;
import com.maxwellnie.velox.jpa.core.jdbc.table.TableInfo;

/**
 * 方法执行器，执行被代理的方法
 *
 * @author Maxwell Nie
 */
public interface Executor {
    /**
     * 执行被代理的方法
     *
     * @param tableInfo
     * @param context
     * @param cache
     * @param daoImplHashCode
     * @param args
     * @return 操作结果
     */
    Object execute(TableInfo tableInfo, JdbcContext context, Cache<Object, Object> cache, String daoImplHashCode, Object[] args);
}
