package com.crazy.sql.core.manager;

import com.crazy.sql.core.jdbc.transaction.TransactionFactory;
import com.crazy.sql.core.jdbc.transaction.impl.jdbc.JdbcTransactionFactory;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Akiba no ichiichiyoha
 */
public class TransactionManager {
    public static final String JDBC="JDBC";
    public static final String SPRING="SPRING";
    private static final Map<String, TransactionFactory> transactionFactoryMap= Collections.synchronizedMap(new LinkedHashMap<>());
    static {
        transactionFactoryMap.put(JDBC,new JdbcTransactionFactory());
    }
    public static TransactionFactory getTransactionFactory(String mode){
        return transactionFactoryMap.get(mode);
    }
    public static TransactionFactory registerTransactionFactory(String mode,TransactionFactory transactionFactory){
        return transactionFactoryMap.put(mode,transactionFactory);
    }
}
