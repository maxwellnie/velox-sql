package com.maxwellnie.velox.sql.spring.support;

import com.maxwellnie.velox.sql.core.cache.Cache;
import com.maxwellnie.velox.sql.core.natives.exception.HandlerException;
import com.maxwellnie.velox.sql.core.natives.jdbc.session.JdbcSessionFactory;
import com.maxwellnie.velox.sql.core.natives.jdbc.session.JdbcSession;
import com.maxwellnie.velox.sql.core.natives.jdbc.mapping.ReturnTypeMapping;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.TableInfo;
import com.maxwellnie.velox.sql.core.proxy.executor.aspect.AbstractMethodHandler;
import com.maxwellnie.velox.sql.core.proxy.executor.aspect.SimpleInvocation;
import com.maxwellnie.velox.sql.core.proxy.executor.aspect.MethodHandler;
import com.maxwellnie.velox.sql.spring.resource.JdbcSessionUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * 为支持spring事务的execute方法处理器
 * @author Maxwell Nie
 */
public class SpringSupportExecuteMethodHandler extends AbstractMethodHandler {
    private JdbcSessionFactory jdbcSessionFactory;
    public SpringSupportExecuteMethodHandler(JdbcSessionFactory jdbcSessionFactory) {
        // 确定要代理的方法
        super(MethodHandler.SPRING_SUPPORT_INDEX, new MethodAspect[]{
                new MethodAspect(
                "execute", new Class<?>[]{
                        TableInfo.class,
                        JdbcSession.class,
                        Cache.class,
                        String.class,
                        ReturnTypeMapping.class,
                        Object[].class,
                    }
                )
        },TargetMethodSignature.ANY);
        this.jdbcSessionFactory = jdbcSessionFactory;
    }

    public JdbcSessionFactory getJdbcContextFactory() {
        return jdbcSessionFactory;
    }

    public void setJdbcContextFactory(JdbcSessionFactory jdbcSessionFactory) {
        this.jdbcSessionFactory = jdbcSessionFactory;
    }
    @Override
    public Object handle(SimpleInvocation simpleInvocation) {
        Object[] args = simpleInvocation.getArgs();
        // 1.获取表信息
        TableInfo tableInfo = (TableInfo) args[0];
        // 2.获取spring jdbc context
        JdbcSession springJdbcSession = JdbcSessionUtils.getJdbcContext(this.jdbcSessionFactory);
        // 3.获取 cache
        Cache cache = (Cache) args[2];
        // 4.获取 dao 实现类 hashcode
        String hashcode = (String) args[3];
        // 5.获取返回类型映射
        ReturnTypeMapping returnTypeMapping =  (ReturnTypeMapping) args[4];
        // 6.获取 dao 实现类方法参数
        Object[] methodArgs = (Object[]) args[5];
        Object result;
        try {
            // 6.执行 dao 实现类方法
            result = simpleInvocation.proceed(tableInfo, springJdbcSession, cache, hashcode , returnTypeMapping, methodArgs);
            if (!JdbcSessionUtils.isJdbcContextTransactional(springJdbcSession, this.jdbcSessionFactory))
                springJdbcSession.commit();
        } catch (Throwable throwable) {
            JdbcSessionUtils.releaseJdbcContext(springJdbcSession, this.jdbcSessionFactory);
            try {
                throw throwable;
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new HandlerException(e);
            }
        } finally {
            JdbcSessionUtils.releaseJdbcContext(springJdbcSession, this.jdbcSessionFactory);
        }
        return result;
    }

}
