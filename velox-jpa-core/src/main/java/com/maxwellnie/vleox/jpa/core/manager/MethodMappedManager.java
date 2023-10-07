package com.maxwellnie.vleox.jpa.core.manager;

import com.maxwellnie.vleox.jpa.core.proxy.executor.Executor;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DaoImpl方法映射管理器，注册映射和获取映射<br/>
 * 注册时需要一个完整的方法名：example[class java.lang.Object, class java.lang.String]作为key，这个方法的处理器Executor作为value
 *
 * @author Maxwell Nie
 */
public class MethodMappedManager {
    private static final Map<String, Executor> methodMappedMap = Collections.synchronizedMap(new LinkedHashMap<>());

    /**
     * 获取被映射方法的处理器
     *
     * @param name
     * @return
     */
    public static Executor getRegisteredMapped(String name) {
        return methodMappedMap.get(name);
    }

    /**
     * 注册被映射方法的处理器
     *
     * @param name
     * @param executor
     */
    public static void registeredMapped(String name, Executor executor) {
        methodMappedMap.put(name, executor);
    }
}
