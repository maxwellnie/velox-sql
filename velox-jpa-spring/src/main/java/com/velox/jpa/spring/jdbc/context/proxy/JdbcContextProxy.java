package com.velox.jpa.spring.jdbc.context.proxy;

import com.maxwellnie.vleox.jpa.core.jdbc.context.JdbcContext;
import com.maxwellnie.vleox.jpa.core.jdbc.context.JdbcContextFactory;
import com.velox.jpa.spring.resource.JdbcContextUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * JdbcContext 的代理类
 */
public class JdbcContextProxy implements InvocationHandler {
    private JdbcContextFactory contextFactory;

    private JdbcContextProxy(JdbcContextFactory contextFactory) {
        this.contextFactory = contextFactory;
    }

    @SuppressWarnings("unchecked")
    public static JdbcContext getContext(JdbcContextFactory contextFactory) {
        return (JdbcContext) Proxy.newProxyInstance(
                JdbcContext.class.getClassLoader(),
                new Class[]{JdbcContext.class},
                new JdbcContextProxy(contextFactory)
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        JdbcContext jdbcContext = JdbcContextUtils.getJdbcContext(this.contextFactory);
        Object result;
        try {
            // 调用目标对象的方法
            result = method.invoke(jdbcContext, args);
        } finally {
            if (jdbcContext != null)
                JdbcContextUtils.releaseJdbcContext(jdbcContext, this.contextFactory);
        }
        return result;
    }
}
