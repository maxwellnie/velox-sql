package com.maxwellnie.velox.sql.core.natives.jdbc.table.primary.generator;

import com.maxwellnie.velox.sql.core.natives.jdbc.table.TableInfo;

public interface KeyGenerator {
    /**
     * about params:params[0] is TableInfo,params[1] is PrimaryInfo,params[2] is entity Objects collection.
     * @param params
     */
    void nextKey(Object... params);
}
