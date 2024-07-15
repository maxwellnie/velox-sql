package com.maxwellnie.velox.sql.core.natives.dao;

import java.io.Serializable;
import java.util.function.Function;

@FunctionalInterface
public interface VFunction<T, R> extends Function<T, R>, Serializable {

}