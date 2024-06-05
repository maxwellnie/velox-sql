package com.maxwellnie.velox.sql.core.natives.exception;

/**
 * @author Maxwell Nie
 */
public class OpenStatementException extends RuntimeException {
    public OpenStatementException() {
    }

    public OpenStatementException(String message) {
        super(message);
    }

    public OpenStatementException(String message, Throwable cause) {
        super(message, cause);
    }

    public OpenStatementException(Throwable cause) {
        super(cause);
    }

    public OpenStatementException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
