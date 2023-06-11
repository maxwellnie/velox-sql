package com.crazy.sql.core.exception;

public class TableIdNotBoundException extends RuntimeException{
    public TableIdNotBoundException() {
        super("There are no binding entity primary key fields");
    }
}
