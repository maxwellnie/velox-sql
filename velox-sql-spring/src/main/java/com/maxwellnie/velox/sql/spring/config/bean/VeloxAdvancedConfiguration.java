package com.maxwellnie.velox.sql.spring.config.bean;

import com.maxwellnie.velox.sql.core.config.Configuration;
import com.maxwellnie.velox.sql.spring.listener.ContextCreationEventListener;
import com.maxwellnie.velox.sql.spring.listener.event.ContextCreationEvent;

/**
 * 其它高级配置
 */
public abstract class VeloxAdvancedConfiguration implements ContextCreationEventListener {

    protected abstract void manageDataSource();

    protected abstract void configurationChanged(Configuration configuration);

    @Override
    public void onApplicationEvent(ContextCreationEvent event) {
        manageDataSource();
        Configuration configuration = event.getBaseConfig();
        configurationChanged(configuration);
    }
}
