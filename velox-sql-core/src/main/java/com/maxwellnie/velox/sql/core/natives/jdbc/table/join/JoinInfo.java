package com.maxwellnie.velox.sql.core.natives.jdbc.table.join;

import com.maxwellnie.velox.sql.core.natives.enums.JoinType;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.TableInfo;
import com.maxwellnie.velox.sql.core.utils.reflect.MetaField;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author Maxwell Nie
 */
public class JoinInfo {
    private Class<?> masterTable;
    private String masterTableField;
    private Class<?> slaveTable;
    /**
     * 当启用notNested时，slaveTableName才会生效。
     */
    private String slaveTableName;
    private String aliasSlaveTable;
    private JoinType joinType;
    private boolean manyToMany;
    private String slaveTableField;
    /**
     * 当启用notNested时，slaveTableColumn才会生效。
     */
    private String slaveTableColumn;
    private MetaField field;
    private Class<?> columnTypeClass;
    private boolean notNested;

    public Class<?> getSlaveTable() {
        return slaveTable;
    }

    public void setSlaveTable(Class<?> slaveTable) {
        this.slaveTable = slaveTable;
    }

    public String getMasterTableField() {
        return masterTableField;
    }
    public void setMasterTableField(String masterTableField) {
        this.masterTableField = masterTableField;
    }

    public JoinType getJoinType() {
        return joinType;
    }

    public void setJoinType(JoinType joinType) {
        this.joinType = joinType;
    }

    public boolean isManyToMany() {
        return manyToMany;
    }

    public void setManyToMany(boolean manyToMany) {
        this.manyToMany = manyToMany;
    }

    public String getSlaveTableField() {
        return slaveTableField;
    }

    public void setSlaveTableField(String slaveTableField) {
        this.slaveTableField = slaveTableField;
    }

    public String getAliasSlaveTable() {
        return aliasSlaveTable;
    }

    public void setAliasSlaveTable(String aliasSlaveTable) {
        this.aliasSlaveTable = aliasSlaveTable;
    }

    public Class<?> getColumnTypeClass() {
        return columnTypeClass;
    }

    public Class<?> getMasterTable() {
        return masterTable;
    }

    public void setMasterTable(Class<?> masterTable) {
        this.masterTable = masterTable;
    }

    public void setColumnTypeClass(Class<?> columnTypeClass) {
        this.columnTypeClass = columnTypeClass;
    }

    public MetaField getField() {
        return field;
    }

    public void setField(MetaField field) {
        this.field = field;
    }

    public boolean isNotNested() {
        return notNested;
    }
    public void setNotNested(boolean notNested) {
        this.notNested = notNested;
    }

    public String getSlaveTableName() {
        return slaveTableName;
    }

    public void setSlaveTableName(String slaveTableName) {
        this.slaveTableName = slaveTableName;
    }

    public String getSlaveTableColumn() {
        return slaveTableColumn;
    }

    public void setSlaveTableColumn(String slaveTableColumn) {
        this.slaveTableColumn = slaveTableColumn;
    }
}
