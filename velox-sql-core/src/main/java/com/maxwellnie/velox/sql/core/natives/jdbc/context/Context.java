package com.maxwellnie.velox.sql.core.natives.jdbc.context;

import com.maxwellnie.velox.sql.core.annotation.RegisterMethod;
import com.maxwellnie.velox.sql.core.cache.Cache;
import com.maxwellnie.velox.sql.core.config.Configuration;
import com.maxwellnie.velox.sql.core.natives.exception.ClassTypeException;
import com.maxwellnie.velox.sql.core.natives.exception.EnvironmentInitException;
import com.maxwellnie.velox.sql.core.natives.exception.RegisterMethodException;
import com.maxwellnie.velox.sql.core.natives.jdbc.mapping.DefaultTypeParser;
import com.maxwellnie.velox.sql.core.natives.jdbc.mapping.TypeParser;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.TableInfo;
import com.maxwellnie.velox.sql.core.natives.jdbc.transaction.TransactionFactory;
import com.maxwellnie.velox.sql.core.natives.task.TaskQueue;
import com.maxwellnie.velox.sql.core.proxy.DaoImplFactory;
import com.maxwellnie.velox.sql.core.proxy.executor.MethodExecutor;
import com.maxwellnie.velox.sql.core.proxy.executor.MethodExecutorCycle;
import com.maxwellnie.velox.sql.core.proxy.executor.aspect.*;
import com.maxwellnie.velox.sql.core.utils.java.StringUtils;
import com.maxwellnie.velox.sql.core.utils.reflect.ReflectionUtils;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.TableInfoManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * velox-sql上下文
 *
 * @author Maxwell Nie
 */
public final class Context {
    private static final Logger logger = LoggerFactory.getLogger(Context.class);
    /**
     * 基础配置
     */
    private final Configuration configuration;
    /**
     * 开放接口工厂管理器
     */
    private final DaoImplFactoryManager daoImplManager;
    /**
     * 事务工厂
     *
     * @see TransactionFactory#produce(boolean, int)
     */
    private TransactionFactory transactionFactory;
    /**
     * 开放接口
     *
     * @see DaoImplFactory
     */
    private final Class<?> daoImplClazz;
    /**
     * 缓存类
     */
    private Class<? extends Cache> cacheClass;
    private TaskQueue taskQueue;
    /**
     * 事务隔离级别
     *
     * @see java.sql.Connection#setTransactionIsolation(int)
     */
    private final int level;
    /**
     * 表信息工具
     *
     * @see TableInfoManager
     * @see TableInfo
     */
    private TableInfoManager tableInfoManager = new TableInfoManager(){};
    /**
     * 方法映射管理器
     *
     * @see MethodMappedManager
     */
    private final MethodMappedManager methodMappedManager = new MethodMappedManager();
    /**
     * 类型解析器
     *
     * @see TypeParser
     */
    private TypeParser typeParser = new DefaultTypeParser();
    /**
     * 方法处理器
     *
     * @see MethodHandler
     */
    private Set<MethodHandler> methodHandlers = Collections.synchronizedSet(new TreeSet<>());
    {
        methodHandlers.add(new MethodExecutorCycle.CycleMethodExecutorHandler());
        methodHandlers.add(new CountMethodHandler());
        methodHandlers.add(new SelectPageMethodHandler());
        methodHandlers.add(new LastSqlMethodHandler());
    }
    public Context(TransactionFactory transactionFactory, Configuration configuration, TableInfoManager tableInfoManager) {
        this.configuration = configuration;
        this.level = configuration.getLevel();
        if (transactionFactory == null)
            throw new EnvironmentInitException("TransactionFactory must be not null");
        else
            this.transactionFactory = transactionFactory;
        if (tableInfoManager != null)
            this.tableInfoManager = tableInfoManager;
        if (configuration.getDaoImplClass() == null)
            throw new EnvironmentInitException("DaoImplClazz must be not null.");
        else {
            try {
                this.daoImplClazz = configuration.getDaoImplClass();
                ReflectionUtils.registerDaoImpl(daoImplClazz, new Object[]{configuration, methodMappedManager});
                this.daoImplManager = new DaoImplFactoryManager();
            } catch (ClassTypeException | RegisterMethodException e) {
                throw new EnvironmentInitException("Register dao class["+ configuration.getDaoImplClass()+"] failed", e.getCause());
            }
        }
        if (configuration.getDialect() == null){
            throw new EnvironmentInitException("Dialect must be not null.");
        }
        if (configuration.isCache()) {
            if (configuration.getCacheClass() == null)
                throw new EnvironmentInitException("Cache supporter must be not null.");
            try {
                cacheClass = configuration.getCacheClass();
            } catch (ClassCastException e) {
                throw new EnvironmentInitException("Not found cache class " + configuration.getCacheClass() + ".", e.getCause());
            }
        }
        if (configuration.getIsTaskQueue()){
            if (configuration.getTaskQueueClass() == null)
                throw new EnvironmentInitException("Task queue supporter must be not null.");
            try {
                taskQueue = ReflectionUtils.newInstance(configuration.getTaskQueueClass());
            }catch (Exception e) {
                throw new EnvironmentInitException("Not found task queue class " + configuration.getTaskQueueClass() + ".", e.getCause());
            }
        }

    }

    public Context(TransactionFactory transactionFactory, Configuration configuration) {
        this(transactionFactory, configuration, null);
    }

    public TransactionFactory getTransactionFactory() {
        return transactionFactory;
    }

    public void setTransactionFactory(TransactionFactory transactionFactory) {
        this.transactionFactory = transactionFactory;
    }
    public synchronized void setTypeParser(TypeParser typeParser) {
        this.typeParser = typeParser;
    }

    public DaoImplFactoryManager getDaoImplManager() {
        return daoImplManager;
    }


    public Class<?> getDaoImplClazz() {
        return daoImplClazz;
    }

    public int getLevel() {
        return level;
    }

    public TaskQueue getTaskQueue() {
        return taskQueue;
    }

    /**
     * 注册clazz对应的开放接口实例工厂
     *
     * @param clazz
     */
    public void addDaoImpl(Class<?> clazz) {
        lazyProxy();
        daoImplManager.registerDaoImplFactory(clazz);
        TableInfo tableInfo = TableInfoManager.getTableInfo(clazz);
        for (Method method : daoImplClazz.getMethods()) {
            if (method.isAnnotationPresent(RegisterMethod.class)) {
                RegisterMethod registerMethod = method.getDeclaredAnnotation(RegisterMethod.class);
                if (registerMethod.value() != null) {
                    tableInfo.registerReturnTypeMapping(StringUtils.getMethodDeclaredName(method) ,typeParser.parse(method.getReturnType(), tableInfo));
                }
            }
        }
    }
    /**
     * 懒代理
     */
    private void lazyProxy(){
        if(!methodMappedManager.isProxy)
            for (String key: methodMappedManager.methodMappedMap.keySet()){
                for (MethodHandler methodHandler : methodHandlers){
                    MethodExecutor methodExecutor = methodMappedManager.methodMappedMap.get(key);
                    if(methodExecutor == null)
                        continue;
                    if(methodHandler.getTargetMethodSignature() == MethodHandler.TargetMethodSignature.ANY || methodHandler.getTargetMethodSignature().key().equals(key)){
                        methodMappedManager.methodMappedMap.put(key, (MethodExecutor)Proxy.newProxyInstance(
                                Thread.currentThread().getContextClassLoader(),
                                ReflectionUtils.getAllInterfaces(methodExecutor.getClass()).toArray(new Class[0]),
                                new MethodsHandler(methodHandler,methodExecutor)
                        ));
                    }
                }
            }
        methodMappedManager.isProxy = true;
    }

    /**
     * 获取clazz对应的开放接口实例工厂
     *
     * @param clazz
     * @param <T>
     * @return 开放接口实例工厂
     */
    public <T> DaoImplFactory<T> getDaoImplFactory(Class<?> clazz) {
        return (DaoImplFactory<T>) daoImplManager.getDaoImplFactory(clazz);
    }

    public MethodMappedManager getMethodMappedManager() {
        return methodMappedManager;
    }

    public Set<MethodHandler> getMethodHandlers() {
        return methodHandlers;
    }

    public synchronized void setMethodHandlers(TreeSet<MethodHandler> methodHandlers) {
        this.methodHandlers = methodHandlers;
    }
    public synchronized void addMethodHandler(MethodHandler methodHandler){
        methodHandlers.add(methodHandler);
    }
    private class DaoImplFactoryManager {
        private final Map<Class<?>, DaoImplFactory<?>> daoImplMap = Collections.synchronizedMap(new LinkedHashMap<>());

        public DaoImplFactory<?> getDaoImplFactory(Class<?> clazz) {
            return daoImplMap.get(clazz);
        }

        public void registerDaoImplFactory(Class<?> clazz) {
            if ((clazz != null)) {
                Cache cache = null;
                if (configuration.isCache()) {
                    try {
                        cache = cacheClass.getConstructor().newInstance();
                    } catch (InstantiationException | NoSuchMethodException |
                             IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                this.daoImplMap.put(clazz, new DaoImplFactory<>(daoImplClazz, tableInfoManager.getTableInfo(clazz, configuration), cache));
            } else
                throw new RegisterDaoImplFailedException("The daoImpl mapped class is null");
        }

        private class RegisterDaoImplFailedException extends RuntimeException {
            public RegisterDaoImplFailedException(String message) {
                super(message);
            }
        }
    }
    /**
     * 方法映射管理器
     */
    public static class MethodMappedManager {
        private final Map<String, MethodExecutor> methodMappedMap = new LinkedHashMap<>();
        volatile boolean isProxy = false;
        /**
         * 获取被映射方法的处理器
         *
         * @param name
         * @return
         */
        public MethodExecutor getRegisteredMapped(String name) {
            return methodMappedMap.get(name);
        }

        /**
         * 注册被映射方法的处理器
         * @param name
         * @param methodExecutor
         */
        public synchronized void registeredMapped(String name, MethodExecutor methodExecutor) {
            methodMappedMap.put(name, methodExecutor);
        }
        public MethodExecutor getRegisteredMapped(Class<?> clazz) {
            for (String key: methodMappedMap.keySet()){
                if(clazz.isAssignableFrom(methodMappedMap.get(key).getClass()))
                    return methodMappedMap.get(key);
            }
            return null;
        }
    }
}
