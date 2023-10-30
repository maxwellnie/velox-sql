package com.maxwellnie.velox.jpa.core.jdbc.table.primary.generator;

public class NoKeyGenerator implements KeyGenerator {

    @Override
    public Object nextKey() {
        return null;
    }
}
