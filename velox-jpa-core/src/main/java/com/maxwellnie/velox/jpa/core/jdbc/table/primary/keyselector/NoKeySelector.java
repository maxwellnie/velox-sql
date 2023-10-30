package com.maxwellnie.velox.jpa.core.jdbc.table.primary.keyselector;

public class NoKeySelector implements KeySelector {
    @Override
    public Object selectGeneratorKey(Object... params) {
        return null;
    }
}
