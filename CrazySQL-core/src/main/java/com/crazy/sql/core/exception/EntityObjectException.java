package com.crazy.sql.core.exception;

/**
 * @author Akiba no ichiichiyoha
 */
public class EntityObjectException extends RuntimeException{
    public EntityObjectException(String message) {
        super(message);
    }

    public EntityObjectException(String message, Throwable cause) {
        super(message, cause);
    }
}
