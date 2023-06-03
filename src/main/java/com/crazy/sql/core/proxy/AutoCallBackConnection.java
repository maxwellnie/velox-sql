package com.crazy.sql.core.proxy;

import com.crazy.sql.core.pool.ConnectionPool;

import java.sql.Connection;

public class AutoCallBackConnection {
    private Connection connection;
    private ConnectionPool pool;

    @Override
    protected void finalize() throws Throwable {
        pool.callBack(connection);
        super.finalize();
    }
}
