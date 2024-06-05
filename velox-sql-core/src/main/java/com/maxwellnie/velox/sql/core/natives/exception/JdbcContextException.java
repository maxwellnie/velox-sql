package com.maxwellnie.velox.sql.core.natives.exception;

/**
 * @author Maxwell Nie
 */
public class JdbcContextException extends RuntimeException {
    public JdbcContextException() {
    }

    public JdbcContextException(String message) {
        super(message);
    }

    public JdbcContextException(String message, Throwable cause) {
        super(message, cause);
    }

    public JdbcContextException(Throwable cause) {
        super(cause);
    }

    public JdbcContextException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
