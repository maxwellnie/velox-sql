package com.velox.jpa.spring.executor;

import com.maxwellnie.velox.jpa.core.jdbc.context.JdbcContextFactory;
import com.maxwellnie.velox.jpa.core.manager.MethodMappedManager;
import com.maxwellnie.velox.jpa.core.proxy.executor.Executor;
import com.maxwellnie.velox.jpa.core.utils.java.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author Maxwell Nie
 */
public class ExecutorUtils {
    private static final Logger logger= LoggerFactory.getLogger(ExecutorUtils.class);
    public static void proxyAllExecutor(JdbcContextFactory jdbcContextFactory){
        Map<String, Executor> executorMap=MethodMappedManager.getAllExecutorMap();
        for (String name: executorMap.keySet()){
            Executor executor=executorMap.get(name);
            if(!StringUtils.isNullOrEmpty(name)&&!(executor instanceof ExecutorProxy)){
                Executor executorProxy=new ExecutorProxy(executor,jdbcContextFactory);
                MethodMappedManager.registeredMapped(name,executorProxy);
            }else
                logger.warn("You try to register a repeat executorProxy,Because executorProxy named "+name+" is has been registered.");
        }
    }
}
