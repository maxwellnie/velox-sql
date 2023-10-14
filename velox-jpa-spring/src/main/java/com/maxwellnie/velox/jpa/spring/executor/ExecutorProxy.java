package com.maxwellnie.velox.jpa.spring.executor;

import com.maxwellnie.velox.jpa.core.cahce.Cache;
import com.maxwellnie.velox.jpa.core.jdbc.context.JdbcContext;
import com.maxwellnie.velox.jpa.core.jdbc.context.JdbcContextFactory;
import com.maxwellnie.velox.jpa.core.jdbc.table.TableInfo;
import com.maxwellnie.velox.jpa.core.proxy.executor.Executor;
import com.maxwellnie.velox.jpa.spring.resource.JdbcContextUtils;

/**
 * @author Maxwell Nie
 */
public class ExecutorProxy implements Executor {
    private Executor executor;
    private JdbcContextFactory jdbcContextFactory;

    public ExecutorProxy(Executor executor, JdbcContextFactory jdbcContextFactory) {
        this.executor = executor;
        this.jdbcContextFactory = jdbcContextFactory;
    }

    public JdbcContextFactory getJdbcContextFactory() {
        return jdbcContextFactory;
    }

    public void setJdbcContextFactory(JdbcContextFactory jdbcContextFactory) {
        this.jdbcContextFactory = jdbcContextFactory;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    @Override
    public Object execute(TableInfo tableInfo, JdbcContext context, Cache<Object, Object> cache, String daoImplHashCode, Object[] args) {
        Object result;
        JdbcContext springJdbcContext= JdbcContextUtils.getJdbcContext(this.jdbcContextFactory);
        try{
            result=executor.execute(tableInfo,springJdbcContext,cache,daoImplHashCode,args);
            if(!JdbcContextUtils.isJdbcContextTransactional(springJdbcContext,this.jdbcContextFactory))
                springJdbcContext.commit();
        }catch (Throwable throwable){
            JdbcContextUtils.releaseJdbcContext(springJdbcContext,this.jdbcContextFactory);
            throw throwable;
        }finally {
            JdbcContextUtils.releaseJdbcContext(springJdbcContext,this.jdbcContextFactory);
        }
        return result;
    }
}
