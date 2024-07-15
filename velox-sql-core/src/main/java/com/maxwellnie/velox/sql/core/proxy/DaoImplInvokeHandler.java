package com.maxwellnie.velox.sql.core.proxy;

import com.maxwellnie.velox.sql.core.cache.Cache;
import com.maxwellnie.velox.sql.core.natives.exception.NotMappedMethodException;
import com.maxwellnie.velox.sql.core.natives.jdbc.context.Context;
import com.maxwellnie.velox.sql.core.natives.jdbc.mapping.ReturnTypeMapping;
import com.maxwellnie.velox.sql.core.natives.jdbc.session.JdbcSession;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.TableInfo;
import com.maxwellnie.velox.sql.core.proxy.executor.MethodExecutor;
import com.maxwellnie.velox.sql.core.utils.java.StringUtils;
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
import java.util.Objects;

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
     *
     * @since 1.0
     */
    private static final Method highJavaVersionLookUpMethod;
    /**
     * JDK8 it is necessary to use an invisible constructor to instantiate the LookUp class to handle PRIVATE and PROTECTED methods.
     *
     * @since 1.0
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

    /**
     * @since 1.0
     */
    private TableInfo tableInfo;
    /**
     * @since 1.0
     */
    private JdbcSession jdbcSession;
    private Cache<Object, Object> cache;
    private Context.MethodMappedManager methodMappedManager;

    public DaoImplInvokeHandler(TableInfo tableInfo, JdbcSession jdbcSession, Cache<Object, Object> cache) {
        this.tableInfo = tableInfo;
        this.jdbcSession = jdbcSession;
        this.cache = cache;
        logger.debug("table info : " + tableInfo);
        logger.debug("enable cache : " + (cache != null));
    }

    public DaoImplInvokeHandler(TableInfo tableInfo, JdbcSession jdbcSession, Cache<Object, Object> cache, Context.MethodMappedManager methodMappedManager) {
        this.tableInfo = tableInfo;
        this.jdbcSession = jdbcSession;
        this.cache = cache;
        this.methodMappedManager = methodMappedManager;
        logger.debug("table info : " + tableInfo);
        logger.debug("enable cache : " + (cache != null));
    }

    /**
     * @param method
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @since 1.0
     */
    private MethodHandle getHighJavaVersionMethodHandle(Method method)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        final Class<?> declaringClass = method.getDeclaringClass();
        return ((MethodHandles.Lookup) highJavaVersionLookUpMethod.invoke(null, declaringClass, MethodHandles.lookup())).findSpecial(
                declaringClass, method.getName(), MethodType.methodType(method.getReturnType(), method.getParameterTypes()),
                declaringClass);
    }

    /**
     * @param method
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     * @since 1.0
     */
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

    public JdbcSession getDaoImplSession() {
        return jdbcSession;
    }

    public void setDaoImplSession(JdbcSession jdbcSession) {
        this.jdbcSession = jdbcSession;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        logger.debug("Method - " + method.getName() + " invoke ");
        String methodMappedKey = StringUtils.getMethodDeclaredName(method);
        MethodExecutor methodExecutor = methodMappedManager.getRegisteredMapped(methodMappedKey);
        ReturnTypeMapping returnTypeMapping = tableInfo.getReturnTypeMapping(methodMappedKey);
        /**
         * 判断处理器是否被获取到，被获取到就开始执行，反之就判断是否Object的方法，是则执行代理类的对应方法，如果都不是则抛出异常
         * 如果设置了自动提交，那么每次执行完Executor都会更新缓存。
         */
        if (methodExecutor != null) {
            return methodExecutor.execute(tableInfo, jdbcSession, cache, toString(), returnTypeMapping, args);
        } else if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        } else if (method.isDefault()) {
            try {
                return handleDefaultMethod(method, proxy, args);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw e;
            }
        } else
            throw new NotMappedMethodException("The method \"" + method.getReturnType() + " " + method.getName() + Arrays.toString(method.getParameterTypes()).replace("[", "(").replace("]", ")") + "\" did not find a MethodExecutor.");
    }

    private Object handleDefaultMethod(Method method, Object proxy, Object[] args) throws Throwable {
        MethodHandle methodHandle = getJava8MethodHandle(method);
        if (highJavaVersionLookUpMethod == null)
            return methodHandle.bindTo(proxy).invokeWithArguments(args);
        else
            return getHighJavaVersionMethodHandle(method).bindTo(proxy).invokeWithArguments(args);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableInfo, jdbcSession, cache);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DaoImplInvokeHandler that = (DaoImplInvokeHandler) o;
        return Objects.equals(tableInfo, that.tableInfo) && Objects.equals(jdbcSession, that.jdbcSession) && Objects.equals(cache, that.cache);
    }

    @Override
    public String toString() {
        return super.toString() + "&" + tableInfo.getMappedClazz().getName();
    }
}
