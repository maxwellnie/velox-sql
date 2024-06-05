package com.maxwellnie.velox.sql.core.utils.common;

import com.maxwellnie.velox.sql.core.natives.jdbc.statement.StatementWrapper;
import com.maxwellnie.velox.sql.core.natives.exception.ExecutorException;

/**
 * @author Maxwell Nie
 */
public class MetaWrapperUtils {
    public static <T> T of(StatementWrapper statementWrapper, String key) throws ExecutorException {
        try {
            return (T) statementWrapper.getProperty(key);
        } catch (Throwable throwable) {
            throw new ExecutorException(key + " not found.", throwable);
        }
    }
}
