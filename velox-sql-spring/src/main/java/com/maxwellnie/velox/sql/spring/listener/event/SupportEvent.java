package com.maxwellnie.velox.sql.spring.listener.event;

import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * @author Maxwell Nie
 */
public abstract class SupportEvent extends ApplicationEvent {
    public SupportEvent(List<Object> obj) {
        super(obj);
    }

    public List<Object> getParamObjects() {
        return (List<Object>) getSource();
    }
}
