package com.maxwellnie.velox.jpa.framework.utils;

import com.maxwellnie.velox.jpa.core.proxy.executor.wrapper.StatementWrapper;
import com.maxwellnie.velox.jpa.framework.exception.ExecutorException;

/**
 * @author Maxwell Nie
 */
public class ExecutorUtils {
    public static <T> T of(StatementWrapper statementWrapper, String key) throws ExecutorException {
        try {
            return (T) statementWrapper.getProperty(key);
        } catch (Throwable throwable) {
            throw new ExecutorException(key + " not found.", throwable);
        }
    }
}
