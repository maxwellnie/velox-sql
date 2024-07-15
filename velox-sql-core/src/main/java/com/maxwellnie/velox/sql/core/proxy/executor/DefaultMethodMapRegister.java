package com.maxwellnie.velox.sql.core.proxy.executor;

import com.maxwellnie.velox.sql.core.annotation.RegisterMethod;
import com.maxwellnie.velox.sql.core.natives.exception.RegisterMethodException;
import com.maxwellnie.velox.sql.core.natives.jdbc.context.Context;
import com.maxwellnie.velox.sql.core.utils.java.StringUtils;
import com.maxwellnie.velox.sql.core.utils.reflect.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 默认的方法映射注册器
 *
 * @author Maxwell Nie
 */
public class DefaultMethodMapRegister implements MethodMapRegister {
    @Override
    public void registerDaoImpl(Class<?> clazz, Object[] args) {
        assert clazz != null;
        for (Method method : ReflectionUtils.getInterfaceAllDeclaredMethods(clazz)) {
            if (method.isAnnotationPresent(RegisterMethod.class) && !method.isDefault()) {
                RegisterMethod registerMethod = method.getDeclaredAnnotation(RegisterMethod.class);
                if (registerMethod.value() != null) {
                    try {
                        Context.MethodMappedManager methodMappedManager = (Context.MethodMappedManager) args[1];
                        MethodExecutor methodExecutor = registerMethod.value().getConstructor().newInstance();
                        methodExecutor.setMethodMappedManager(methodMappedManager);
                        methodMappedManager.registeredMapped(StringUtils.getMethodDeclaredName(method), methodExecutor);
                    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                             InvocationTargetException e) {
                        throw new RegisterMethodException("The executor " + registerMethod.value() + " of method " + method + "  cannot be instantiated.", e);
                    }
                }
            }
        }
    }
}
