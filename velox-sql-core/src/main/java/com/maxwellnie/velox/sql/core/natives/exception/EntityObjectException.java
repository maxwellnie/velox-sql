package com.maxwellnie.velox.sql.core.natives.exception;

/**
 * @author Maxwell Nie
 */
public class EntityObjectException extends RuntimeException {
    public EntityObjectException(String message) {
        super(message);
    }

    public EntityObjectException(String message, Throwable cause) {
        super(message, cause);
    }
}
