package com.maxwellnie.vleox.jpa.core.jdbc.table.primary.keyselector;

/**
 * 配置自动主键生成器的查询器
 *
 * @author Maxwell Nie
 */
public class GeneratorSelector implements KeySelector {
    @Override
    public Object selectGeneratorKey(Object param) {
        return param;
    }
}
