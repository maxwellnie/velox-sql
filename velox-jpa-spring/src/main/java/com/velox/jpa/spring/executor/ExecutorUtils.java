package com.velox.jpa.spring.executor;

import com.maxwellnie.velox.jpa.core.jdbc.context.JdbcContextFactory;
import com.maxwellnie.velox.jpa.core.manager.MethodMappedManager;
import com.maxwellnie.velox.jpa.core.proxy.executor.Executor;
import com.maxwellnie.velox.jpa.core.utils.java.StringUtils;

import java.util.Map;

/**
 * @author Maxwell Nie
 */
public class ExecutorUtils {
    public static void proxyAllExecutor(JdbcContextFactory jdbcContextFactory){
        Map<String, Executor> executorMap=MethodMappedManager.getAllExecutorMap();
        for (String name: executorMap.keySet()){
            Executor executor=executorMap.get(name);
            if(!StringUtils.isNullOrEmpty(name)){
                Executor executorProxy=new ExecutorProxy(executor,jdbcContextFactory);
                MethodMappedManager.registeredMapped(name,executorProxy);
            }
        }
    }
}
