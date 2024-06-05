package com.maxwellnie.velox.sql.spring.listener;

import com.maxwellnie.velox.sql.spring.listener.event.PostContextCreationEvent;
import org.springframework.context.ApplicationListener;

/**
 * @author Maxwell Nie
 */
public interface PostContextCreationEventListener extends ApplicationListener<PostContextCreationEvent> {
    @Override
    void onApplicationEvent(PostContextCreationEvent event);
}
