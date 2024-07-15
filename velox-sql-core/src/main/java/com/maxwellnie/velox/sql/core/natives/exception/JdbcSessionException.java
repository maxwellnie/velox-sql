package com.maxwellnie.velox.sql.core.natives.exception;

/**
 * @author Maxwell Nie
 */
public class JdbcSessionException extends RuntimeException {
    public JdbcSessionException() {
    }

    public JdbcSessionException(String message) {
        super(message);
    }

    public JdbcSessionException(String message, Throwable cause) {
        super(message, cause);
    }

    public JdbcSessionException(Throwable cause) {
        super(cause);
    }

    public JdbcSessionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
