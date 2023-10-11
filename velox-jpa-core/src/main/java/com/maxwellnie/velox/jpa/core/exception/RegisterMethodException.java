package com.maxwellnie.velox.jpa.core.exception;

/**
 * @author Maxwell Nie
 */
public class RegisterMethodException extends RuntimeException {
    public RegisterMethodException(String message) {
        super(message);
    }

    public RegisterMethodException(String message, Throwable cause) {
        super(message, cause);
    }

    public RegisterMethodException(Throwable cause) {
        super(cause);
    }

    public RegisterMethodException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
