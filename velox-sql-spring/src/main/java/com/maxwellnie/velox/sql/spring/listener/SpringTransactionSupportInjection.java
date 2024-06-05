package com.maxwellnie.velox.sql.spring.listener;

import com.maxwellnie.velox.sql.spring.listener.event.PostJdbcContextFactoryEvent;
import com.maxwellnie.velox.sql.spring.support.SpringSupportExecuteMethodHandler;
import org.slf4j.Logger;

/**
 * @author Maxwell Nie
 */
public class SpringTransactionSupportInjection implements PostJdbcContextFactoryEventListener{
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(SpringTransactionSupportInjection.class);
    @Override
    public void onApplicationEvent(PostJdbcContextFactoryEvent event) {
        logger.debug(SpringSupportExecuteMethodHandler.class.getName() + " injection is successful.");
        logger.info("VeloxSql has enabled spring transaction support.");
        event.getEnvironment().addMethodHandler(new SpringSupportExecuteMethodHandler(event.getJdbcContextFactory()));
    }
}
