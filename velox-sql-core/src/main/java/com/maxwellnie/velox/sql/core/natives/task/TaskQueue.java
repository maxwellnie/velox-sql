package com.maxwellnie.velox.sql.core.natives.task;

import com.maxwellnie.velox.sql.core.cache.key.CacheKey;
import com.maxwellnie.velox.sql.core.natives.jdbc.session.JdbcSession;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * @author Maxwell Nie
 */
public interface TaskQueue extends Serializable {
    void require(String group, CacheKey cacheKey, Runnable task);
}
