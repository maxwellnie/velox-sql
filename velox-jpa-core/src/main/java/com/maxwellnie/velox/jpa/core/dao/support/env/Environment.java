package com.maxwellnie.velox.jpa.core.dao.support.env;

import com.maxwellnie.velox.jpa.core.cahce.Cache;
import com.maxwellnie.velox.jpa.core.config.BaseConfig;
import com.maxwellnie.velox.jpa.core.exception.ClassTypeException;
import com.maxwellnie.velox.jpa.core.exception.EnvironmentInitException;
import com.maxwellnie.velox.jpa.core.exception.RegisterMethodException;
import com.maxwellnie.velox.jpa.core.jdbc.transaction.TransactionFactory;
import com.maxwellnie.velox.jpa.core.proxy.DaoImplFactory;
import com.maxwellnie.velox.jpa.core.utils.java.StringUtils;
import com.maxwellnie.velox.jpa.core.utils.reflect.ReflectUtils;
import com.maxwellnie.velox.jpa.core.utils.reflect.TableInfoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DaoImpl生产环境
 *
 * @author Maxwell Nie
 */
public class Environment {
    private static final Logger logger = LoggerFactory.getLogger(Environment.class);
    /**
     * 基础配置
     */
    private final BaseConfig baseConfig;
    /**
     * 开放接口工厂管理器
     */
    private final DaoImplFactoryManager daoImplManager;
    /**
     * 事务工厂
     *
     * @see TransactionFactory#produce(DataSource, boolean, int)
     */
    private TransactionFactory transactionFactory;
    /**
     * 数据源
     *
     * @see TransactionFactory#produce(DataSource, boolean, int)
     */
    private DataSource dataSource;
    /**
     * 开放接口
     *
     * @see DaoImplFactory
     */
    private Class<?> daoImplClazz;
    /**
     * 缓存类
     */
    private Class<? extends Cache> cacheClass;
    /**
     * 事务隔离级别
     *
     * @see java.sql.Connection
     */
    private int level;
    /**
     * 表信息工具
     *
     * @see TableInfoUtils
     * @see com.maxwellnie.velox.jpa.core.jdbc.table.TableInfo
     */
    private TableInfoUtils tableInfoUtils = new TableInfoUtils() {
    };

    public Environment(TransactionFactory transactionFactory, DataSource dataSource, BaseConfig baseConfig, TableInfoUtils tableInfoUtils) {
        this.baseConfig = baseConfig;
        this.level = baseConfig.getLevel();
        if (transactionFactory == null)
            throw new EnvironmentInitException("TransactionFactory must be not null");
        else
            this.transactionFactory = transactionFactory;
        if (dataSource == null)
            throw new EnvironmentInitException("DataSource must be not null");
        else
            this.dataSource = dataSource;
        if (tableInfoUtils != null)
            this.tableInfoUtils = tableInfoUtils;
        if (StringUtils.isNullOrEmpty(BaseConfig.getDaoImplClassName()))
            throw new EnvironmentInitException("DaoImplClazz must be not null.");
        else {
            try {
                this.daoImplClazz = Class.forName(BaseConfig.getDaoImplClassName());
                ReflectUtils.registerDaoImpl(daoImplClazz);
                this.daoImplManager = new DaoImplFactoryManager();
            } catch (ClassNotFoundException | ClassTypeException | RegisterMethodException e) {
                throw new EnvironmentInitException("Not found class " + BaseConfig.getDaoImplClassName() + ".", e.getCause());
            }
        }
        if (baseConfig.isCache() && !StringUtils.isNullOrEmpty(baseConfig.getCacheClassName())) {
            try {
                cacheClass = (Class<? extends Cache>) Class.forName(baseConfig.getCacheClassName());
            } catch (ClassNotFoundException | ClassCastException e) {
                e.printStackTrace();
            }
        } else
            throw new EnvironmentInitException("Cache supporter must be null.");
    }

    public Environment(TransactionFactory transactionFactory, DataSource dataSource, BaseConfig baseConfig) {
        this(transactionFactory, dataSource, baseConfig, null);
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

    /**
     * 注册clazz对应的开放接口实例工厂
     *
     * @param clazz
     */
    public void addDaoImpl(Class<?> clazz) {
        daoImplManager.registerDaoImplFactory(clazz);
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

    private class DaoImplFactoryManager {
        private final Map<Class<?>, DaoImplFactory<?>> daoImplMap = Collections.synchronizedMap(new LinkedHashMap<>());

        public DaoImplFactory<?> getDaoImplFactory(Class<?> clazz) {
            return daoImplMap.get(clazz);
        }

        public void registerDaoImplFactory(Class<?> clazz) {
            if ((clazz != null)) {
                Cache cache = null;
                if (baseConfig.isCache()) {
                    try {
                        cache = cacheClass.getConstructor().newInstance();
                    } catch (InstantiationException | NoSuchMethodException |
                             IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } else
                    throw new EnvironmentInitException("Cache supporter must be null.");
                this.daoImplMap.put(clazz, new DaoImplFactory(daoImplClazz, tableInfoUtils.getTableInfo(clazz, baseConfig), cache));
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
