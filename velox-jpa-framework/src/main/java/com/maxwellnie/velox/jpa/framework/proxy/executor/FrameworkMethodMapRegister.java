package com.maxwellnie.velox.jpa.framework.proxy.executor;

import com.maxwellnie.velox.jpa.core.annotation.RegisterMethod;
import com.maxwellnie.velox.jpa.core.exception.RegisterMethodException;
import com.maxwellnie.velox.jpa.core.manager.MethodMappedManager;
import com.maxwellnie.velox.jpa.core.proxy.executor.Executor;
import com.maxwellnie.velox.jpa.core.proxy.executor.MethodMapRegister;
import com.maxwellnie.velox.jpa.core.utils.java.StringUtils;
import com.maxwellnie.velox.jpa.framework.proxy.executor.cycle.ExecuteCycle;
import com.maxwellnie.velox.jpa.framework.proxy.executor.cycle.ExecutorDelegate;

import java.lang.reflect.Method;

/**
 * @author Maxwell Nie
 */
public class FrameworkMethodMapRegister implements MethodMapRegister {
    @Override
    public void registerDaoImpl(Class<?> clazz) {
        assert clazz != null;
        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(RegisterMethod.class)) {
                RegisterMethod registerMethod = method.getDeclaredAnnotation(RegisterMethod.class);
                if (registerMethod.value() != null) {
                    try {
                        Executor executor = registerMethod.value().newInstance();
                        ExecuteCycle executeCycle = null;
                        if (executor instanceof ExecuteCycle) {
                            executeCycle = (ExecuteCycle) executor;
                            MethodMappedManager.registeredMapped(StringUtils.getMethodDeclaredName(method), new ExecutorDelegate(executeCycle));
                        } else
                            throw new RegisterMethodException("The executor " + registerMethod.value() + " of method " + method + "  cannot be instantiated.Cause The Executor type is not be ExecuteCycle.");
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new RegisterMethodException("The executor " + registerMethod.value() + " of method " + method + "  cannot be instantiated.", e);
                    }
                }
            }
        }
    }
}
