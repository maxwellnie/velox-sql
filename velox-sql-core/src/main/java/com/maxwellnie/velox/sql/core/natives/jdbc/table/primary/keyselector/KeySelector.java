package com.maxwellnie.velox.sql.core.natives.jdbc.table.primary.keyselector;

public interface KeySelector {
    /**
     * about params:params[0] is Statement,params[1] is PrimaryInfo,params[2] is entity Objects collection.
     *
     * @param params
     */
    void selectGeneratorKey(Object... params);
}
