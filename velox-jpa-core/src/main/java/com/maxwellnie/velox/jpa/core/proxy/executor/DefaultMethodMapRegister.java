package com.maxwellnie.velox.jpa.core.proxy.executor;

import com.maxwellnie.velox.jpa.core.annotation.RegisterMethod;
import com.maxwellnie.velox.jpa.core.exception.RegisterMethodException;
import com.maxwellnie.velox.jpa.core.manager.MethodMappedManager;
import com.maxwellnie.velox.jpa.core.utils.java.StringUtils;

import java.lang.reflect.Method;

/**
 * @author Maxwell Nie
 */
public class DefaultMethodMapRegister implements MethodMapRegister {
    @Override
    public void registerDaoImpl(Class<?> clazz) {
        assert clazz != null;
        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(RegisterMethod.class)) {
                RegisterMethod registerMethod = method.getDeclaredAnnotation(RegisterMethod.class);
                if (registerMethod.value() != null) {
                    try {
                        MethodMappedManager.registeredMapped(StringUtils.getMethodDeclaredName(method), registerMethod.value().newInstance());
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new RegisterMethodException("The executor " + registerMethod.value() + " of method " + method + "  cannot be instantiated.", e);
                    }
                }
            }
        }
    }
}
