package com.maxwellnie.vleox.jpa.core.jdbc.table.primary.generator;

public class NoGenerator implements Generator {
    @Override
    public void backKey() {

    }

    @Override
    public Object nextKey() {
        return null;
    }
}