package com.crazysql.spring.resource;

import com.crazy.sql.core.jdbc.context.JdbcContext;
import org.springframework.transaction.support.ResourceHolder;
import org.springframework.transaction.support.ResourceHolderSupport;

/**
 * @author Akiba no ichiichiyoha
 */
public class JdbcContextHolder extends ResourceHolderSupport {
    private JdbcContext context;

    public JdbcContextHolder(JdbcContext context) {
        this.context = context;
    }

    public JdbcContext getContext() {
        return context;
    }
}
