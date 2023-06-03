package com.crazy.sql.core.utils;

import com.crazy.sql.core.enums.QueryCondition;
import com.crazy.sql.core.query.QueryWord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 数据库持久化工具，可以实现从java实体类对象持久化到数据库数据
 * @param <T>
 */
public class SQLUtils<T> {
    private static Logger logger= LoggerFactory.getLogger(SQLUtils.class);
    private ReflectUtils<T> reflectUtils;
    public SQLUtils(Class<T> tClass,String tableSuffix,boolean standColumnName){
        this.reflectUtils=new ReflectUtils<>(tClass,tableSuffix,standColumnName);
    }

    /**
     * 添加一条数据到数据库
     * @param t
     * @param connection
     * @return
     * @throws SQLException
     */
    public PreparedStatement insert(T t, Connection connection) throws SQLException {
        StringBuffer stringBuffer=new StringBuffer("insert into ");
        stringBuffer.append(reflectUtils.getTableName());
        stringBuffer.append("(");
        Field[] fields=reflectUtils.getFields();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            try {
                if(fields[i].get(t)!=null&&!reflectUtils.isPrimaryKey(fields[i]))
                    stringBuffer.append(reflectUtils.handleColumnName(fields[i].getName()))
                            .append(",");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        stringBuffer=new StringBuffer(stringBuffer.substring(0,stringBuffer.length()-1));
        stringBuffer.append(") values(");
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            try {
                if(fields[i].get(t)!=null&&!reflectUtils.isPrimaryKey(fields[i]))
                    stringBuffer.append("?,");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        stringBuffer=new StringBuffer(stringBuffer.substring(0,stringBuffer.length()-1));
        stringBuffer.append(")");
        logger.info(stringBuffer.toString());
        PreparedStatement preparedStatement=connection.prepareStatement(stringBuffer.toString(),PreparedStatement.RETURN_GENERATED_KEYS);
        for (int i = 0,j=1; i < fields.length; i++) {
            fields[i].setAccessible(true);
            try {
                if(fields[i].get(t)!=null&&!reflectUtils.isPrimaryKey(fields[i])){
                    preparedStatement.setObject(j,fields[i].get(t));
                    j++;
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return preparedStatement;
    }

    /**
     * 修改数据库的一条数据
     * @param t
     * @param connection
     * @return
     * @throws SQLException
     */
    public PreparedStatement update(T t, Connection connection) throws SQLException {
        StringBuffer stringBuffer=new StringBuffer("update ");
        stringBuffer.append(reflectUtils.getTableName());
        stringBuffer.append(" set ");
        Field[] fields=reflectUtils.getFields();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            if(!reflectUtils.isPrimaryKey(fields[i])) {
                stringBuffer.append(reflectUtils.handleColumnName(fields[i].getName()))
                        .append("=")
                        .append("?,");
            }
        }
        stringBuffer=new StringBuffer(stringBuffer.substring(0,stringBuffer.length()-1));
        stringBuffer.append(" where ")
                        .append(reflectUtils.getPrimaryKey())
                        .append("=?");
        logger.info(stringBuffer.toString());
        PreparedStatement preparedStatement=connection.prepareStatement(stringBuffer.toString());
        int j=1;
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            try {
                if(!reflectUtils.isPrimaryKey(fields[i])){
                    preparedStatement.setObject(j,fields[i].get(t));
                    j++;
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        preparedStatement.setObject(j,reflectUtils.getPrimaryKeyValue(t));
        return preparedStatement;
    }

    /**
     * 根据主键值删除数据库数据
     * @param t
     * @param connection
     * @return
     * @throws SQLException
     */
    public PreparedStatement delete(T t, Connection connection) throws SQLException {
        StringBuffer stringBuffer=new StringBuffer("delete from ");
        stringBuffer.append(reflectUtils.getTableName())
                .append(" where ")
                .append(reflectUtils.getPrimaryKey())
                .append("=?");
        logger.info(stringBuffer.toString());
        PreparedStatement preparedStatement=connection.prepareStatement(stringBuffer.toString());
        preparedStatement.setObject(1,reflectUtils.getPrimaryKeyValue(t));
        return preparedStatement;
    }

    /**
     * 通过主键值查找一条数据
     * @param t
     * @param connection
     * @return
     * @throws SQLException
     */
    public T queryOne(T t, Connection connection) throws SQLException {
        StringBuffer stringBuffer=new StringBuffer("select * from ");
        stringBuffer.append(reflectUtils.getTableName())
                .append(" where ")
                .append(reflectUtils.getPrimaryKey()).append(" =?");
        logger.info(stringBuffer.toString());
        PreparedStatement preparedStatement=connection.prepareStatement(stringBuffer.toString());
        preparedStatement.setObject(1,reflectUtils.getPrimaryKeyValue(t));
        ResultSet resultSet= preparedStatement.executeQuery();
        String[] columns= Arrays.stream(reflectUtils.getFields()).map(Field::getName).toArray(String[]::new);
        List<T> resultList=convertEntity(resultSet,columns);
        if(resultList==null||resultList.size()==0)
             return null;
        else
            return resultList.get(0);
    }

    /**
     * 查询该表的全部数据
     * @param connection
     * @return
     * @throws SQLException
     */
    public List<T> queryAll(Connection connection) throws SQLException {
        StringBuffer stringBuffer=new StringBuffer("select * from ");
        stringBuffer.append(reflectUtils.getTableName());
        logger.info(stringBuffer.toString());
        PreparedStatement preparedStatement=connection.prepareStatement(stringBuffer.toString());
        ResultSet resultSet= preparedStatement.executeQuery();
        String[] columns= Arrays.stream(reflectUtils.getFields()).map(Field::getName).toArray(String[]::new);
        return convertEntity(resultSet,columns);
    }

    /**
     * 条件查询，返回满足条件的数据
     * @param connection
     * @param queryWords
     * @return
     * @throws SQLException
     */
    public List<T> queryByWords(Connection connection,QueryWord... queryWords) throws SQLException {
        StringBuffer stringBuffer=new StringBuffer("select * from ");
        PreparedStatement preparedStatement;
        stringBuffer.append(reflectUtils.getTableName());
        if(queryWords!=null&&queryWords.length>0) {
            stringBuffer.append(" where ");
            for (QueryWord queryWord : queryWords) {
                if (queryWord.getValue() != null)
                    stringBuffer.append(reflectUtils.handleColumnName(queryWord.getField()))
                            .append(" ")
                            .append(queryWord.getCondition())
                            .append(" ? and");
            }
            stringBuffer = new StringBuffer(stringBuffer.substring(0, stringBuffer.length() - 3));
            logger.info(stringBuffer.toString());
            preparedStatement = connection.prepareStatement(stringBuffer.toString());
            for (int i = 0, j = 1; i < queryWords.length; i++) {
                if (queryWords[i].getValue() != null) {
                    if (queryWords[i].getCondition().equals(QueryCondition.LIKE))
                        preparedStatement.setString(j, "%" + queryWords[i].getValue() + "%");
                    else
                        preparedStatement.setObject(j, queryWords[i].getValue());
                    j++;
                }
            }
        }else {
            preparedStatement = connection.prepareStatement(stringBuffer.toString());
        }
        ResultSet resultSet= preparedStatement.executeQuery();
        String[] columns= Arrays.stream(reflectUtils.getFields()).map(Field::getName).toArray(String[]::new);
        return convertEntity(resultSet,columns);
    }

    /**
     * 将resultSet转换为实体类
     * @param resultSet
     * @param columns
     * @return
     * @throws SQLException
     */
    public List<T> convertEntity(ResultSet resultSet,String[] columns) throws SQLException {
        List<T> resultList=new ArrayList<>();
        while (resultSet.next()){
            T result;
            try {
                result=reflectUtils.gettClass().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            for (int i=0;i<columns.length;i++) {
                try {
                    reflectUtils.getSetter(reflectUtils.getFields()[i]).invoke(result,resultSet.getObject(reflectUtils.handleColumnName(columns[i])));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
            resultList.add(result);
        }
        return resultList;
    }
    public String getTableName(){
        return this.reflectUtils.getTableName();
    }
    public String getPrimaryKeyValue(T t){
        return this.reflectUtils.getPrimaryKeyValue(t).toString();
    }
    public void setPrimaryKeyValue(T t,Object value){
        try {
            reflectUtils.getSetter(reflectUtils.getPrimaryKeyField()).invoke(t,value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
