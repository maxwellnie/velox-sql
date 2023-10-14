package com.maxwellnie.velox.jpa.core.manager;

import com.maxwellnie.velox.jpa.core.jdbc.transaction.TransactionFactory;
import com.maxwellnie.velox.jpa.core.jdbc.transaction.impl.jdbc.JdbcTransactionFactory;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Maxwell Nie
 * @deprecated 我们已经做好淘汰掉这个管理器的准备了，将会在2.x淘汰这个管理器。
 */
@Deprecated
public class TransactionManager {
    public final String JDBC = "JDBC";
    public final String SPRING = "SPRING";
    private final Map<String, TransactionFactory> transactionFactoryMap = Collections.synchronizedMap(new LinkedHashMap<>());

    {
        transactionFactoryMap.put(JDBC, new JdbcTransactionFactory());
    }

    public TransactionFactory getTransactionFactory(String mode) {
        return transactionFactoryMap.get(mode);
    }

    public TransactionFactory registerTransactionFactory(String mode, TransactionFactory transactionFactory) {
        return transactionFactoryMap.put(mode, transactionFactory);
    }
}
