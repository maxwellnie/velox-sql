package com.maxwellnie.velox.sql.core.proxy.executor;

import com.maxwellnie.velox.sql.core.natives.exception.RegisterMethodException;

/**
 * 方法和执行器映射的注册接口
 * @author Maxwell Nie
 */
public interface MethodMapRegister {
    /**
     * 注册dao实现类
     * @param clazz dao实现类
     * @param args
     * @throws RegisterMethodException 注册失败
     */
    void registerDaoImpl(Class<?> clazz, Object[] args);
}
