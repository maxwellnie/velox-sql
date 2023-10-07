package com.maxwellnie.vleox.jpa.core.dao.support.env;

import com.maxwellnie.vleox.jpa.core.cahce.Cache;
import com.maxwellnie.vleox.jpa.core.config.BaseConfig;
import com.maxwellnie.vleox.jpa.core.exception.ClassTypeException;
import com.maxwellnie.vleox.jpa.core.exception.EnvironmentInitException;
import com.maxwellnie.vleox.jpa.core.jdbc.transaction.TransactionFactory;
import com.maxwellnie.vleox.jpa.core.proxy.DaoImplFactory;
import com.maxwellnie.vleox.jpa.core.utils.reflect.ReflectUtils;

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
    private final BaseConfig baseConfig;
    private final DaoImplFactoryManager daoImplManager;
    private TransactionFactory transactionFactory;
    private DataSource dataSource;
    private Class<?> daoImplClazz;

    public Environment(TransactionFactory transactionFactory, DataSource dataSource, BaseConfig baseConfig) {
        this.baseConfig = baseConfig;
        if (transactionFactory == null)
            throw new EnvironmentInitException("TransactionFactory must be not null");
        else
            this.transactionFactory = transactionFactory;
        if (dataSource == null)
            throw new EnvironmentInitException("DataSource must be not null");
        else
            this.dataSource = dataSource;
        if (baseConfig.getDaoImplClazz() == null)
            throw new EnvironmentInitException("daoImplClazz must be not null");
        else {
            try {
                this.daoImplClazz = baseConfig.getDaoImplClazz();
                if (daoImplClazz.isInterface())
                    ReflectUtils.registerDaoImpl(daoImplClazz);
                else
                    throw new ClassTypeException("The class " + daoImplClazz + " must be interface");
            } catch (Throwable throwable) {
                throw new EnvironmentInitException("daoImplClazz " + daoImplClazz + " cannot be register");
            }
        }
        this.daoImplManager = new DaoImplFactoryManager();
        if (baseConfig.getClazzArr() != null) {
            for (Class<?> clazz : baseConfig.getClazzArr()) {
                addDaoImpl(clazz);
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

    public void setDaoImplClazz(Class<?> daoImplClazz) {
        this.daoImplClazz = daoImplClazz;
    }

    private void addDaoImpl(Class<?> clazz) {
        daoImplManager.registerDaoImplFactory(clazz);
    }

    public DaoImplFactory<?> getDaoImplFactory(Class<?> clazz) {
        return daoImplManager.getDaoImplFactory(clazz);
    }

    private class DaoImplFactoryManager {
        private final Map<Class, DaoImplFactory> daoImplMap = Collections.synchronizedMap(new LinkedHashMap<>());

        public DaoImplFactory getDaoImplFactory(Class clazz) {
            return daoImplMap.get(clazz);
        }

        public void registerDaoImplFactory(Class clazz) {
            if ((clazz != null)) {
                Cache cache = null;
                if (baseConfig.isCache() && baseConfig.getCacheClass() != null) {
                    try {
                        cache = baseConfig.getCacheClass().newInstance();
                    } catch (InstantiationException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else
                    throw new EnvironmentInitException("Cache supporter must be null.");
                this.daoImplMap.put(clazz, new DaoImplFactory(daoImplClazz, ReflectUtils.getTableInfo(clazz), cache));
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
