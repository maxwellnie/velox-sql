package com.maxwellnie.vleox.jpa.core.exception;

public class DaoImplClassException extends RuntimeException {
    public DaoImplClassException() {
        super("Support daoImpl is not interface!");
    }
}
