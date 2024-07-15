package com.maxwellnie.velox.sql.core.natives.exception;

/**
 * @author Maxwell Nie
 */
public class MultipleDataSourceException extends RuntimeException {
    public MultipleDataSourceException() {
    }

    public MultipleDataSourceException(String message) {
        super(message);
    }

    public MultipleDataSourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public MultipleDataSourceException(Throwable cause) {
        super(cause);
    }

    public MultipleDataSourceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
