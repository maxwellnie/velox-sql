package com.maxwellnie.velox.sql.core.natives.jdbc.sql;

/**
 * SQL语句池
 *
 * @author Maxwell Nie
 */
public interface SqlPool {
    String SQL_INJECT_PLACE_HOLDER = "#sql";
    String INSERT = "INSERT INTO #sql (#sql) VALUES(#sql)";
    String UPDATE = "UPDATE #sql SET #sql #sql";
    String DELETE = "DELETE FROM #sql #sql";
    String QUERY = "SELECT #sql FROM #sql #sql";
    String ORDER_BY = "ORDER BY #sql";
    String GROUP_BY = "GROUP BY #sql";
    String LEFT = "LEFT";
    String RIGHT = "RIGHT";
    String INNER = "INNER";
    String JOIN = "#sql JOIN #sql";
    String JOIN_ON = "#sql JOIN #sql ON #sql";
    String WHERE = "WHERE #sql";
    String BETWEEN_AND = "BETWEEN ? AND ?";
    String EXISTS = "IS EXISTS #sql";
    String LIKE = "LIKE ?";
    String LIST = "#sql IN (#sql)";
    String HAVING = "HAVING #sql";
    String AND = "AND";
    String OR = "OR";
    String ORDER_BY_DESC = "DESC";
    String ORDER_BY_ASC = "ASC";
    String SPACE = " ";
    String AS = "AS";
    String EQ = " = ?";
    String NE = " != ?";
    String GT = " > ?";
    String LT = " < ?";
    String GTE = " >= ?";
    String LTE = " <= ?";
    String IS_NULL = " IS NULL";
    String NOT = " NOT";
    String NOT_EQ = " != ?";
    String NOT_LIKE = " NOT LIKE ?";
    String NOT_IN = " NOT IN (?)";
    String NOT_BETWEEN_AND = " NOT BETWEEN ? AND ?";
    String NOT_EXISTS = " NOT EXISTS (?)";
    String NOT_NULL = " IS NOT NULL";
    String IN = " IN (#sql)";
}
