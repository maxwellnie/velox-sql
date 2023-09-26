package com.crazy.sql.core.proxy;

import com.crazy.sql.core.accessor.Accessor;
import com.crazy.sql.core.cahce.Cache;
import com.crazy.sql.core.config.GlobalConfig;
import com.crazy.sql.core.exception.NotMappedMethodException;
import com.crazy.sql.core.proxy.executor.Executor;
import com.crazy.sql.core.jdbc.context.JdbcContext;
import com.crazy.sql.core.jdbc.table.TableInfo;
import com.crazy.sql.core.utils.reflect.ReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 这是一个代理类，会处理被代理接口的方法，即使他不是我们的初始Accessor，我们将整个框架的很多结构都修改为可自定义的模式。<br/>
 * 你可以根据需求重新设置Accessor的功能。
 * @author Akiba no ichiichiyoha
 */
public class AccessorInvokeHandler implements InvocationHandler {
    private static final Logger logger= LoggerFactory.getLogger(Accessor.class);
    private TableInfo tableInfo;
    private JdbcContext jdbcContext;
    private Cache<Object,Object> cache;
    public AccessorInvokeHandler(TableInfo tableInfo, JdbcContext jdbcContext, Cache<Object, Object> cache) {
        this.tableInfo = tableInfo;
        this.jdbcContext = jdbcContext;
        this.cache = cache;
        logger.debug("table info : "+tableInfo);
        logger.debug("enable cache : "+ GlobalConfig.getInstance().isCache());
    }

    public AccessorInvokeHandler(TableInfo tableInfo, Cache<Object, Object> cache) {
        this.tableInfo = tableInfo;
        this.cache=cache;
    }

    public Cache<Object, Object> getCache() {
        return cache;
    }

    public void setCache(Cache<Object, Object> cache) {
        this.cache = cache;
    }

    public TableInfo getTableInfo() {
        return tableInfo;
    }

    public void setTableInfo(TableInfo tableInfo) {
        this.tableInfo = tableInfo;
    }

    public JdbcContext getAccessorSession() {
        return jdbcContext;
    }

    public void setAccessorSession(JdbcContext jdbcContext) {
        this.jdbcContext = jdbcContext;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        logger.debug("Method - "+method.getName()+" invoke ");
        Executor executor=ReflectUtils.getMethodMapped(method);

        /**
         * 判断处理器是否被获取到，被获取到就开始执行，反之就判断是否Object的方法，是则执行代理类的对应方法，如果都不是则抛出异常
         * 如果设置了自动提交，那么每次执行完Executor都会更新缓存。
         */
        if(executor!=null) {
            Object result= executor.execute(tableInfo, jdbcContext, cache, toString(), args);
            if (jdbcContext.getAutoCommit())
                jdbcContext.commit();
            return result;
        } else if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }else if(method.isDefault()) {
            try {
                return handleDefaultMethod(method, proxy, args);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } else
            throw new NotMappedMethodException("The method \"" + method.getReturnType() + " " + method.getName() + Arrays.toString(method.getParameterTypes()).replace("[", "(").replace("]", ")") + "\" did not find a Executor.");

    }
    private Object handleDefaultMethod(Method method,Object proxy,Object[] args) throws Throwable {
        Constructor<MethodHandles.Lookup> constructor=MethodHandles.Lookup.class.getDeclaredConstructor(Class.class,int.class);
        constructor.setAccessible(true);
        return constructor.newInstance(method.getDeclaringClass(),MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED)
                .unreflectSpecial(method,method.getDeclaringClass())
                .bindTo(proxy)
                .invokeWithArguments(args);
    }
    @Override
    public String toString() {
        return "Accessor";
    }

}
