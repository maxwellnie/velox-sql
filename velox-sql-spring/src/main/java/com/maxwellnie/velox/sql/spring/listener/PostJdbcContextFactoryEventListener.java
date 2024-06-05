package com.maxwellnie.velox.sql.spring.listener;

import com.maxwellnie.velox.sql.spring.listener.event.PostJdbcContextFactoryEvent;
import org.springframework.context.ApplicationListener;

/**
 * @author Maxwell Nie
 */
public interface PostJdbcContextFactoryEventListener extends ApplicationListener<PostJdbcContextFactoryEvent> {
    @Override
    void onApplicationEvent(PostJdbcContextFactoryEvent event);
}
