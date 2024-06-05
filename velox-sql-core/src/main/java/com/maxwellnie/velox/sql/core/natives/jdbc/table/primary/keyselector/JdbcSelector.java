package com.maxwellnie.velox.sql.core.natives.jdbc.table.primary.keyselector;

import com.maxwellnie.velox.sql.core.natives.jdbc.table.TableInfo;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.column.PrimaryInfo;
import com.maxwellnie.velox.sql.core.utils.jdbc.ResultSetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;

public class JdbcSelector implements KeySelector {
    private static final Logger logger = LoggerFactory.getLogger(JdbcSelector.class);


    @Override
    public void selectGeneratorKey(Object... params) {
        if (params == null || params.length != 3)
            return;
        Statement statement = (Statement) params[0];
        ResultSet resultSet;
        try {
            resultSet = statement.getGeneratedKeys();
        } catch (SQLException e) {
            return;
        }
        /**
         * fixed 'the empty resultSet cause NullPointerException' bug.
         */
        if (resultSet == null)
            return;
        PrimaryInfo primaryInfo = ((TableInfo) params[1]).getPkColumn();
        Collection<?> entities = (Collection<?>) params[2];
        try {
            Object[] autoPrimaryKey = ResultSetUtils.getAutoIncrementKey(resultSet);
            assert autoPrimaryKey.length != 0 : "autoPrimaryKey is empty";
            assert autoPrimaryKey.length == entities.size() : "autoPrimaryKey length is not equal to entities length";
            Iterator<?> iterator = entities.iterator();
            for (int i = 0; i < autoPrimaryKey.length; i++) {
                Object autoPrimaryKeyValue = autoPrimaryKey[i];
                primaryInfo.getColumnMappedField().set(iterator.next(), autoPrimaryKeyValue);
            }
        } catch (SQLException | AssertionError | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            logger.error(e.getMessage() + "\t\n" + e.getCause());
            throw new PrimaryKeySelectorException(e);
        }
    }

    private static class PrimaryKeySelectorException extends RuntimeException {
        public PrimaryKeySelectorException(Throwable e) {
            super(e);
        }

        public PrimaryKeySelectorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }

        public PrimaryKeySelectorException() {
        }

        public PrimaryKeySelectorException(String message) {
            super(message);
        }

        public PrimaryKeySelectorException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
