package com.maxwellnie.velox.sql.core.natives.exception;

/**
 * @author Maxwell Nie
 */
public class JDBCConnectionException extends RuntimeException {
    public JDBCConnectionException() {
    }

    public JDBCConnectionException(String message) {
        super(message);
    }

    public JDBCConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public JDBCConnectionException(Throwable cause) {
        super(cause);
    }

    public JDBCConnectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
