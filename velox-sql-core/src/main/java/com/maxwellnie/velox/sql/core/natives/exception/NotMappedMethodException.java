package com.maxwellnie.velox.sql.core.natives.exception;

/**
 * @author Maxwell Nie
 */
public class NotMappedMethodException extends RuntimeException {
    public NotMappedMethodException(String message) {
        super(message);
    }

    public NotMappedMethodException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotMappedMethodException(Throwable cause) {
        super(cause);
    }

    public NotMappedMethodException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public NotMappedMethodException() {
    }
}
