package com.maxwellnie.velox.sql.core.proxy.executor.aspect;

import com.maxwellnie.velox.sql.core.natives.exception.MethodNotFoundException;
import com.maxwellnie.velox.sql.core.natives.exception.NotMappedMethodException;
import com.maxwellnie.velox.sql.core.utils.reflect.ReflectionUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @author Maxwell Nie
 */
public class MethodsHandler implements InvocationHandler {
    private MethodHandler methodHandler;
    private Object target;
    private HashMap<String, Method> methodAspectMap = new HashMap<>();

    public MethodsHandler(MethodHandler methodHandler, Object target) {
        this.methodHandler = methodHandler;
        if (methodHandler.getMethodAspects()!= null) {
            if (methodHandler.getMethodAspects() == MethodHandler.MethodAspect.ANY_FLAG)
                methodAspectMap = null;
            for (MethodHandler.MethodAspect methodAspect : methodHandler.getMethodAspects()) {
                try {
                    Method method = ReflectionUtils.getInterfaceDeclaredMethod(target.getClass(), methodAspect.getName(), methodAspect.getArgs());
                    if(method == null)
                        throw new MethodNotFoundException(methodAspect.getName()+"("+Arrays.toString(methodAspect.getArgs())+")"+"method not found.");
                    methodAspectMap.put(methodAspect.getName(), method);
                } catch (NoSuchMethodException e) {
                    throw new NotMappedMethodException(e);
                }
            }
        }
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (methodHandler!= null) {
            if (methodAspectMap == null){
                return methodHandler.handle(new SimpleInvocation(target, proxy, args, method));
            }
            if(methodAspectMap.containsKey(method.getName()) && methodAspectMap.get(method.getName()).equals(method)){
                return methodHandler.handle(new SimpleInvocation(target, proxy, args, method));
            }
        }
        return method.invoke(target, args);
    }
}
