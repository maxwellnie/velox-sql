package com.maxwellnie.vleox.jpa.core.exception;

/**
 * @author Maxwell Nie
 */
public class CrazySqlConfigException extends RuntimeException {
    public CrazySqlConfigException() {
    }

    public CrazySqlConfigException(String message) {
        super(message);
    }

    public CrazySqlConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public CrazySqlConfigException(Throwable cause) {
        super(cause);
    }

    public CrazySqlConfigException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
