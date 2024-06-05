package com.maxwellnie.velox.sql.core.natives.exception;

/**
 * @author Maxwell Nie
 */
public class TypeMappingException extends RuntimeException {
    public TypeMappingException() {
    }

    public TypeMappingException(String message) {
        super(message);
    }

    public TypeMappingException(String message, Throwable cause) {
        super(message, cause);
    }

    public TypeMappingException(Throwable cause) {
        super(cause);
    }

    public TypeMappingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
