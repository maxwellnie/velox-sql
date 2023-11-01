package com.maxwellnie.velox.jpa.core.utils.reflect;

import com.maxwellnie.velox.jpa.core.annotation.DaoImplDeclared;
import com.maxwellnie.velox.jpa.core.exception.ClassTypeException;
import com.maxwellnie.velox.jpa.core.exception.DaoImplClassException;
import com.maxwellnie.velox.jpa.core.exception.RegisterMethodException;
import com.maxwellnie.velox.jpa.core.manager.MethodMappedManager;
import com.maxwellnie.velox.jpa.core.proxy.executor.Executor;
import com.maxwellnie.velox.jpa.core.proxy.executor.MethodMapRegister;
import com.maxwellnie.velox.jpa.core.utils.java.StringUtils;

import java.lang.reflect.Method;

/**
 * @author Maxwell Nie
 */
public abstract class ReflectUtils {
    /**
     * 获取被映射方法的处理器
     *
     * @param method
     * @return
     */
    public static Executor getMethodMapped(Method method) {
        assert method != null;
        return MethodMappedManager.getRegisteredMapped(StringUtils.getMethodDeclaredName(method));
    }

    public static void registerDaoImpl(Class<?> clazz) throws ClassTypeException, RegisterMethodException {
        assert clazz != null : "DaoImplInterface must not be null!";
        if (clazz.isAnnotationPresent(DaoImplDeclared.class)) {
            DaoImplDeclared daoImplDeclared = clazz.getDeclaredAnnotation(DaoImplDeclared.class);
            if (daoImplDeclared.value() != null) {
                try {
                    MethodMapRegister methodMapRegister = daoImplDeclared.value().newInstance();
                    methodMapRegister.registerDaoImpl(clazz);
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RegisterMethodException(e);
                }
            } else
                throw new DaoImplClassException("Your supported DaoImplInterface not cover DaoImplDeclared annotation.");
        }
    }
}
