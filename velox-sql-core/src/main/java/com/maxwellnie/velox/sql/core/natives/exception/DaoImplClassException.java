package com.maxwellnie.velox.sql.core.natives.exception;

public class DaoImplClassException extends RuntimeException {
    public DaoImplClassException() {
        super("Support daoImpl is not interface!");
    }

    public DaoImplClassException(String message) {
        super(message);
    }

    public DaoImplClassException(String message, Throwable cause) {
        super(message, cause);
    }

    public DaoImplClassException(Throwable cause) {
        super(cause);
    }

    public DaoImplClassException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
