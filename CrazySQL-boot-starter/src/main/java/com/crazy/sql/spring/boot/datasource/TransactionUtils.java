package com.crazy.sql.spring.boot.datasource;

import com.crazy.sql.core.cahce.manager.CacheManager;
import com.crazy.sql.core.jdbc.EnableTransactionConnection;
import com.crazy.sql.spring.boot.utils.MetaObject;
import com.crazy.sql.spring.boot.utils.MetaObjectUtils;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.ConnectionHandle;
import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.jdbc.datasource.SimpleConnectionHandle;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;


public class TransactionUtils {
    public static Connection getConnection(DataSource dataSource, CacheManager cacheManager) throws CannotGetJdbcConnectionException {
        Connection connection= org.springframework.jdbc.datasource.DataSourceUtils.getConnection(dataSource);
        ConnectionHolder conHolder = (ConnectionHolder) TransactionSynchronizationManager.getResource(dataSource);
        if ((conHolder != null&& conHolder.isSynchronizedWithTransaction())&&conHolder.getConnection()==null) {
            MetaObject metaObject=MetaObjectUtils.getMetaObject(conHolder);
            setConnection(metaObject,new EnableTransactionConnection(connection,cacheManager));
        }
        return connection;
    }
    private static void setConnection(MetaObject metaObject, Connection connection) {
        Connection currentConnection= (Connection) metaObject.get("currentConnection");
        ConnectionHandle connectionHandle=(ConnectionHandle) metaObject.get("ConnectionHandle");
        if (currentConnection != null) {
            connectionHandle.releaseConnection(currentConnection);
            metaObject.set("currentConnection",null);
        }

        if (connection != null) {
            metaObject.set("connectionHandle",new SimpleConnectionHandle(connection));
        } else {
            metaObject.set("connectionHandle",null);
        }

    }

}
