package com.maxwellnie.velox.jpa.core.proxy;

import com.maxwellnie.velox.jpa.core.cahce.Cache;
import com.maxwellnie.velox.jpa.core.config.simple.VeloxJpaConfig;
import com.maxwellnie.velox.jpa.core.jdbc.table.TableInfo;
import com.maxwellnie.velox.jpa.core.exception.NotMappedMethodException;
import com.maxwellnie.velox.jpa.core.jdbc.context.JdbcContext;
import com.maxwellnie.velox.jpa.core.proxy.executor.Executor;
import com.maxwellnie.velox.jpa.core.utils.reflect.ReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 这是一个代理类，会处理被代理接口的方法，即使他不是我们的初始DaoImpl，我们将整个框架的很多结构都修改为可自定义的模式。<br/>
 * 你可以根据需求重新设置DaoImpl的功能。
 *
 * @author Maxwell Nie
 */
public class DaoImplInvokeHandler implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(DaoImplInvokeHandler.class);
    /**
     * JDK9+ introduces a novel method named "privateLookupIn" to handle PRIVATE and PROTECTED methods.
     */
    private static final Method highJavaVersionLookUpMethod;
    /**
     * JDK8 it is necessary to use a invisible constructor to instantiate the LookUp class to handle PRIVATE and PROTECTED methods.
     */
    private static final Constructor<MethodHandles.Lookup> java8LookupConstructor;

    //since 1.0
    static {
        //java9+
        Method privateLookupIn;
        try {
            privateLookupIn = MethodHandles.class.getMethod("privateLookupIn", Class.class, MethodHandles.Lookup.class);
        } catch (NoSuchMethodException e) {
            privateLookupIn = null;
        }
        highJavaVersionLookUpMethod = privateLookupIn;
        //java8
        Constructor<MethodHandles.Lookup> lookup = null;
        if (highJavaVersionLookUpMethod == null) {
            try {
                lookup = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
                lookup.setAccessible(true);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (Exception e) {
                lookup = null;
            }
        }
        java8LookupConstructor = lookup;
    }
    //since 1.0

    private TableInfo tableInfo;
    //since 1.0
    private JdbcContext jdbcContext;
    private Cache<Object, Object> cache;

    public DaoImplInvokeHandler(TableInfo tableInfo, JdbcContext jdbcContext, Cache<Object, Object> cache) {
        this.tableInfo = tableInfo;
        this.jdbcContext = jdbcContext;
        this.cache = cache;
        logger.debug("table info : " + tableInfo);
        logger.debug("enable cache : " + VeloxJpaConfig.getInstance().isCache());
    }

    public DaoImplInvokeHandler(TableInfo tableInfo, Cache<Object, Object> cache) {
        this.tableInfo = tableInfo;
        this.cache = cache;
    }

    //since 1.0
    private MethodHandle getHighJavaVersionMethodHandle(Method method)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        final Class<?> declaringClass = method.getDeclaringClass();
        return ((MethodHandles.Lookup) highJavaVersionLookUpMethod.invoke(null, declaringClass, MethodHandles.lookup())).findSpecial(
                declaringClass, method.getName(), MethodType.methodType(method.getReturnType(), method.getParameterTypes()),
                declaringClass);
    }

    //since 1.0
    private MethodHandle getJava8MethodHandle(Method method)
            throws IllegalAccessException, InstantiationException, InvocationTargetException {
        final Class<?> declaringClass = method.getDeclaringClass();
        return java8LookupConstructor.newInstance(declaringClass, MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED).unreflectSpecial(method, declaringClass);
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

    public JdbcContext getDaoImplSession() {
        return jdbcContext;
    }

    public void setDaoImplSession(JdbcContext jdbcContext) {
        this.jdbcContext = jdbcContext;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        logger.debug("Method - " + method.getName() + " invoke ");
        Executor executor = ReflectUtils.getMethodMapped(method);

        /**
         * 判断处理器是否被获取到，被获取到就开始执行，反之就判断是否Object的方法，是则执行代理类的对应方法，如果都不是则抛出异常
         * 如果设置了自动提交，那么每次执行完Executor都会更新缓存。
         */
        if (executor != null) {
            Object result = executor.execute(tableInfo, jdbcContext, cache, toString(), args);
            return result;
        } else if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        } else if (method.isDefault()) {
            try {
                return handleDefaultMethod(method, proxy, args);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw e;
            }
        } else
            throw new NotMappedMethodException("The method \"" + method.getReturnType() + " " + method.getName() + Arrays.toString(method.getParameterTypes()).replace("[", "(").replace("]", ")") + "\" did not find a Executor.");
    }

    private Object handleDefaultMethod(Method method, Object proxy, Object[] args) throws Throwable {
        MethodHandle methodHandle = getJava8MethodHandle(method);
        if (highJavaVersionLookUpMethod == null)
            return methodHandle.bindTo(proxy).invokeWithArguments(args);
        else
            return getHighJavaVersionMethodHandle(method).bindTo(proxy).invokeWithArguments(args);
    }

    @Override
    public String toString() {
        return super.toString() + "-" + tableInfo.getMappedClazz().getName() + "-DaoImpl";
    }
}
