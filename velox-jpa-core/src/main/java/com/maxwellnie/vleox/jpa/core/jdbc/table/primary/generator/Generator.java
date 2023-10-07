package com.maxwellnie.vleox.jpa.core.jdbc.table.primary.generator;

public interface Generator {
    void backKey();

    Object nextKey();
}
