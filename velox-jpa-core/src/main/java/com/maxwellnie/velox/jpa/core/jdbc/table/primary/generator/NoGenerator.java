package com.maxwellnie.velox.jpa.core.jdbc.table.primary.generator;

public class NoGenerator implements Generator {

    @Override
    public Object nextKey() {
        return null;
    }
}
