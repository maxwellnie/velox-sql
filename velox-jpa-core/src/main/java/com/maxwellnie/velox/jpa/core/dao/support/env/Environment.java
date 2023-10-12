package com.maxwellnie.velox.jpa.core.dao.support.env;

import com.maxwellnie.velox.jpa.core.config.BaseConfig;
import com.maxwellnie.velox.jpa.core.proxy.DaoImplFactory;
import com.maxwellnie.velox.jpa.core.cahce.Cache;
import com.maxwellnie.velox.jpa.core.exception.EnvironmentInitException;
import com.maxwellnie.velox.jpa.core.jdbc.transaction.TransactionFactory;
import com.maxwellnie.velox.jpa.core.utils.java.StringUtils;
import com.maxwellnie.velox.jpa.core.utils.reflect.TableIfoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DaoImpl工作环境
 *
 * @author Maxwell Nie
 */
public class Environment {
    private static final Logger logger= LoggerFactory.getLogger(Environment.class);
    private final BaseConfig baseConfig;
    private final DaoImplFactoryManager daoImplManager;
    private TransactionFactory transactionFactory;
    private DataSource dataSource;
    private Class<?> daoImplClazz;
    private int level;

    public Environment(TransactionFactory transactionFactory, DataSource dataSource, BaseConfig baseConfig) {
        this.baseConfig = baseConfig;
        this.level=baseConfig.getLevel();
        if (transactionFactory == null)
            throw new EnvironmentInitException("TransactionFactory must be not null");
        else
            this.transactionFactory = transactionFactory;
        if (dataSource == null)
            throw new EnvironmentInitException("DataSource must be not null");
        else
            this.dataSource = dataSource;
        if(StringUtils.isNullOrEmpty(BaseConfig.getDaoImplClassName()))
            throw new EnvironmentInitException("DaoImplClazz must be not null.");
        else {
            try {
                this.daoImplClazz=Class.forName(BaseConfig.getDaoImplClassName());
                TableIfoUtils.registerDaoImpl(this.daoImplClazz);
                this.daoImplManager=new DaoImplFactoryManager();
            } catch (ClassNotFoundException e) {
                throw new EnvironmentInitException("Not found class "+ BaseConfig.getDaoImplClassName()+".",e.getCause());
            }
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


    public DaoImplFactoryManager getDaoImplManager() {
        return daoImplManager;
    }


    public Class<?> getDaoImplClazz() {
        return daoImplClazz;
    }

    public int getLevel() {
        return level;
    }

    public void addDaoImpl(Class<?> clazz) {
        daoImplManager.registerDaoImplFactory(clazz);
    }

    public <T> DaoImplFactory<T> getDaoImplFactory(Class<?> clazz) {
        return daoImplManager.getDaoImplFactory(clazz);
    }

    private class DaoImplFactoryManager {
        private final Map<Class<?>, DaoImplFactory<?>> daoImplMap = Collections.synchronizedMap(new LinkedHashMap<>());

        public <T> DaoImplFactory<T> getDaoImplFactory(Class<?> clazz) {
            return (DaoImplFactory<T>) daoImplMap.get(clazz);
        }
        public void registerDaoImplFactory(Class<?> clazz) {
            if ((clazz != null)) {
                Cache cache = null;
                if (baseConfig.isCache() && baseConfig.getCacheClass() != null) {
                    try {
                        cache = baseConfig.getCacheClass().newInstance();
                    } catch (InstantiationException e) {
                        logger.error(e.getMessage()+"\t\n"+e.getCause());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else
                    throw new EnvironmentInitException("Cache supporter must be null.");
                this.daoImplMap.put(clazz, new DaoImplFactory(daoImplClazz, TableIfoUtils.getTableInfo(clazz,baseConfig), cache));
            } else
                throw new RegisterDaoImplFailedException("The daoImpl mapped class is null");
        }

        private class RegisterDaoImplFailedException extends RuntimeException {
            public RegisterDaoImplFailedException(String message) {
                super(message);
            }
        }
    }

}
