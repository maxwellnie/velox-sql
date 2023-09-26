package com.crazy.sql.core.accessor.env;

import com.crazy.sql.core.cahce.Cache;
import com.crazy.sql.core.config.GlobalConfig;
import com.crazy.sql.core.exception.EnvironmentInitException;
import com.crazy.sql.core.jdbc.transaction.TransactionFactory;
import com.crazy.sql.core.proxy.AccessorFactory;
import com.crazy.sql.core.utils.reflect.ReflectUtils;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Accessor工作环境
 * @author Akiba no ichiichiyoha
 */
public class Environment {
    private final GlobalConfig globalConfig;
    private TransactionFactory transactionFactory;
    private DataSource dataSource;
    private final AccessorFactoryManager accessorManager;
    private Class<?> accessorClazz;

    public Environment(TransactionFactory transactionFactory,DataSource dataSource) {
        this.globalConfig=GlobalConfig.getInstance();
        if(transactionFactory==null)
            throw new EnvironmentInitException("TransactionFactory must be not null");
        else
            this.transactionFactory=transactionFactory;
        if(dataSource==null)
            throw new EnvironmentInitException("DataSource must be not null");
        else
            this.dataSource=dataSource;
        if(globalConfig.getAccessorClazz()==null)
            throw new EnvironmentInitException("accessorClazz must be not null");
        else
            this.accessorClazz=globalConfig.getAccessorClazz();
        this.accessorManager=new AccessorFactoryManager();
        if(globalConfig.getClazzArr()!=null){
            for (Class<?> clazz:globalConfig.getClazzArr()){
                addAccessor(clazz);
            }
        }else {
            throw new EnvironmentInitException("Empty class array in GlobalConfig");
        }
    }
    public TransactionFactory getTransactionFactory() {
        return transactionFactory;
    }

    public void setTransactionFactory(TransactionFactory transactionFactory) {
        this.transactionFactory = transactionFactory;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public AccessorFactoryManager getAccessorManager() {
        return accessorManager;
    }

    public Class<?> getAccessorClazz() {
        return accessorClazz;
    }

    public void setAccessorClazz(Class<?> accessorClazz) {
        this.accessorClazz = accessorClazz;
    }

    private void addAccessor(Class<?> clazz){
        accessorManager.registerAccessorFactory(clazz);
    }
    public AccessorFactory<?> getAccessor(Class<?> clazz){
        return accessorManager.getAccessorFactory(clazz);
    }
    private class AccessorFactoryManager {
        private final Map<Class, AccessorFactory> accessorMap= Collections.synchronizedMap(new LinkedHashMap<>());
        public AccessorFactory getAccessorFactory(Class clazz){
            return accessorMap.get(clazz);
        }
        public void registerAccessorFactory(Class clazz){
            if((clazz!=null)){
                Cache cache=null;
                if(globalConfig.isCache()&&globalConfig.getCacheClass()!=null){
                    try {
                        cache=globalConfig.getCacheClass().newInstance();
                    } catch (InstantiationException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }else
                    throw new EnvironmentInitException("Cache supporter must be null.");
                this.accessorMap.put(clazz,new AccessorFactory(accessorClazz,ReflectUtils.getTableInfo(clazz),cache));
            }
            else
                throw new RegisterAccessorFailedException("The accessor mapped class is null");
        }
        private class RegisterAccessorFailedException extends RuntimeException{
            public RegisterAccessorFailedException(String message) {
                super(message);
            }
        }
    }

}
