package com.maxwellnie.velox.sql.spring.listener;

import com.maxwellnie.velox.sql.spring.listener.event.ContextCreationEvent;
import org.springframework.context.ApplicationListener;

/**
 * @author Maxwell Nie
 */
public interface ContextCreationEventListener extends ApplicationListener<ContextCreationEvent> {
    @Override
    void onApplicationEvent(ContextCreationEvent event);
}
