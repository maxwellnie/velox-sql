package com.maxwellnie.velox.jpa.core.exception;

/**
 * 连接池繁忙异常
 */
public class ConnectionPoolBusyException extends RuntimeException {
    public ConnectionPoolBusyException() {
        super("The connection pool is busy; there are no free connections!");
    }
}
