package com.maxwellnie.velox.sql.core.natives.exception;

/**
 * @author Maxwell Nie
 */
public class MethodNotSupportException extends RuntimeException {
    public MethodNotSupportException(String message) {
        super(message);
    }

    public MethodNotSupportException(String message, Throwable cause) {
        super(message, cause);
    }

    public MethodNotSupportException(Throwable cause) {
        super(cause);
    }

    public MethodNotSupportException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public MethodNotSupportException() {
    }
}
