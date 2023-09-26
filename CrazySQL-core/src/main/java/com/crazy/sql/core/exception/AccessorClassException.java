package com.crazy.sql.core.exception;

public class AccessorClassException extends RuntimeException{
    public AccessorClassException() {
        super("Support accessor is not interface!");
    }
}
