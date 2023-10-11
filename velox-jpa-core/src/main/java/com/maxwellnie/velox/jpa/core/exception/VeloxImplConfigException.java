package com.maxwellnie.velox.jpa.core.exception;

/**
 * @author Maxwell Nie
 */
public class VeloxImplConfigException extends RuntimeException {
    public VeloxImplConfigException() {
    }

    public VeloxImplConfigException(String message) {
        super(message);
    }

    public VeloxImplConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public VeloxImplConfigException(Throwable cause) {
        super(cause);
    }

    public VeloxImplConfigException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
