package com.maxwellnie.velox.sql.core.proxy.executor.aspect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 代理对象调用方法的钩子
 * @author Maxwell Nie
 */
public class SimpleInvocation {
    Object target;
    Object proxy;
    Object[] args;
    Method targetMethod;

    public SimpleInvocation(Object target, Object proxy, Object[] args, Method targetMethod) {
        this.target = target;
        this.proxy = proxy;
        this.args = args;
        this.targetMethod = targetMethod;
    }

    /**
     * 目标对象
     * @return Object
     */
    public Object getTarget() {
        return target;
    }
    /**
     * 目标方法参数
     * @return Object[]
     */
    public Object[] getArgs() {
        return args;
    }
    /**
     * 目标方法
     * @return Method
     */
    public Method getTargetMethod() {
        return targetMethod;
    }

    public Object getProxy() {
        return proxy;
    }

    /**
     * 调用目标方法
     * @return Object
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public Object proceed() throws InvocationTargetException, IllegalAccessException {
        return this.targetMethod.invoke(target, args);
    }
    /**
     * 调用目标方法
     * @param args
     * @return Object
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public Object proceed(Object... args) throws InvocationTargetException, IllegalAccessException {
        return this.targetMethod.invoke(target, args);
    }
}
