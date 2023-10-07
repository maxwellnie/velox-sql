package com.maxwellnie.vleox.jpa.core.exception;

/**
 * @author Maxwell Nie
 */
public class NotMappedFieldException extends RuntimeException {
    public NotMappedFieldException(String message) {
        super(message);
    }
}
