package com.maxwellnie.velox.sql.core.natives.exception;

/**
 * @author Maxwell Nie
 */
public class TypeNotSupportedException extends RuntimeException {
    public TypeNotSupportedException() {
    }

    public TypeNotSupportedException(String message) {
        super(message);
    }

    public TypeNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }

    public TypeNotSupportedException(Throwable cause) {
        super(cause);
    }

    public TypeNotSupportedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
