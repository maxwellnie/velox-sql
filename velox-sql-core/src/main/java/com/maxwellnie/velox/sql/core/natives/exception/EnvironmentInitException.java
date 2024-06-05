package com.maxwellnie.velox.sql.core.natives.exception;

public class EnvironmentInitException extends RuntimeException {
    public EnvironmentInitException(String message) {
        super(message);
    }

    public EnvironmentInitException(String message, Throwable cause) {
        super(message, cause);
    }

    public EnvironmentInitException(Throwable cause) {
        super(cause);
    }

    public EnvironmentInitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public EnvironmentInitException() {
    }
}
