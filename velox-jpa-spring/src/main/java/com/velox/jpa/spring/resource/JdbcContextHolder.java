package com.velox.jpa.spring.resource;

import com.maxwellnie.vleox.jpa.core.jdbc.context.JdbcContext;
import org.springframework.transaction.support.ResourceHolderSupport;

/**
 * JdbcContext持有者
 *
 * @author Maxwell Nie
 */
public class JdbcContextHolder extends ResourceHolderSupport {
    private final JdbcContext context;

    public JdbcContextHolder(JdbcContext context) {
        this.context = context;
    }

    public JdbcContext getContext() {
        return context;
    }
}
