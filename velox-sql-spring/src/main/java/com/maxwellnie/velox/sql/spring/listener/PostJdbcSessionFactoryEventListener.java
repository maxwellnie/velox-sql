package com.maxwellnie.velox.sql.spring.listener;

import com.maxwellnie.velox.sql.spring.listener.event.PostJdbcSessionFactoryEvent;
import org.springframework.context.ApplicationListener;

/**
 * @author Maxwell Nie
 */
public interface PostJdbcSessionFactoryEventListener extends ApplicationListener<PostJdbcSessionFactoryEvent> {
    @Override
    void onApplicationEvent(PostJdbcSessionFactoryEvent event);
}
