package com.maxwellnie.velox.sql.core.natives.jdbc.resultset.hash;

import com.maxwellnie.velox.sql.core.utils.base.CollectionUtils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Objects;

/**
 * Hash
 */
public class Hash {
    public static final Hash NO_HASH_KEY = new Hash();
    int hash0 = 4;

    public static Hash create(Object... values) {
        Hash hash = new Hash();
        for (Object value : values) hash.addValue(value);
        return hash;
    }

    public void addValue(Object value) {
        if (value == null) ;
        else if (value.getClass().isArray()) {
            for (int i = 0; i < Array.getLength(value); i++) {
                hash0 = Objects.hash(hash0, Array.get(value, i));
            }
        } else if (CollectionUtils.isCollection(value)) {
            for (Object o : (Collection) value) hash0 = Objects.hash(o);
        } else
            hash0 = Objects.hash(hash0, value);
    }

    public void addValues(Object... values) {
        for (Object value : values) addValue(value);
    }

    @Override
    public int hashCode() {
        return hash0;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        else if (o.getClass() != getClass())
            return false;
        else {
            Hash other = (Hash) o;
            return hashCode() == other.hashCode();
        }
    }

    @Override
    public String toString() {
        return hashCode() + "";
    }
}
