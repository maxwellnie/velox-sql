package com.maxwellnie.velox.sql.core.natives.exception;

/**
 * @author Maxwell Nie
 */
public class ClassTypeException extends Exception {
    public ClassTypeException() {
    }

    public ClassTypeException(String message) {
        super(message);
    }

    public ClassTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClassTypeException(Throwable cause) {
        super(cause);
    }

    public ClassTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
