package com.crazy.sql.core.proxy.executor;

import com.crazy.sql.core.cahce.Cache;
import com.crazy.sql.core.jdbc.context.JdbcContext;
import com.crazy.sql.core.jdbc.table.TableInfo;

/**
 * 方法执行器，执行被代理的方法
 * @author Akiba no ichiichiyoha
 */
public interface Executor {
     /**
      * 执行被代理的方法
      * @param tableInfo
      * @param context
      * @param cache
      * @param accessorHashCode
      * @param args
      * @return
      */
     Object execute(TableInfo tableInfo, JdbcContext context, Cache<Object,Object> cache, String accessorHashCode, Object[] args);
}
