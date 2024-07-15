package com.maxwellnie.velox.sql.core.natives.type.convertor.impl.json;

import com.maxwellnie.velox.sql.core.natives.type.convertor.TypeConvertor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Maxwell Nie
 */
public class JsonConvertor<T> implements TypeConvertor<T> {
    private final JsonSupporter supporter;
    private final Class<T> clazz;

    public JsonConvertor(JsonSupporter supporter, Class<T> clazz) {
        this.supporter = supporter;
        this.clazz = clazz;
    }

    @Override
    public T convert(ResultSet resultSet, String column) throws SQLException {
        return supporter.fromJson(clazz, resultSet.getString(column));
    }

    @Override
    public T convert(ResultSet resultSet, int columnIndex) throws SQLException {
        return supporter.fromJson(clazz, resultSet.getString(columnIndex));
    }

    @Override
    public void addParam(PreparedStatement preparedStatement, int index, Object param) throws SQLException {
        preparedStatement.setString(index, supporter.toJson(param));
    }

    @Override
    public T getEmpty() {
        return supporter.empty();
    }
}
