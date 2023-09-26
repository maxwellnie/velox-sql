package com.crazy.sql.core.utils.java;

import com.crazy.sql.core.jdbc.sql.SqlFragment;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * 字符串工具类
 */
public class StringUtils {
    /**
     * 把java字段转化为一个标准的字段名，例如：userId->user_id
     * @param column
     * @return
     */
    public static String getStandName(String column){
        char[] chars=column.toCharArray();
        StringBuffer stringBuffer=new StringBuffer(String.valueOf((char)(chars[0]>=65&&chars[0]<=90?chars[0]+32:chars[0])));
        for (int i = 1; i < chars.length; i++) {
            if(chars[i]>=65&&chars[i]<=90){
                stringBuffer.append("_").append((char)(chars[i]+32));
            }else {
                stringBuffer.append(chars[i]);
            }
        }
        return stringBuffer.toString();
    }

    /**
     * 判断字符串是否为空
     * @param str
     * @return
     */
    public static boolean isNullOrEmpty(String str){
        if(str==null)
            return true;
        else if(str.trim().isEmpty()){
            return true;
        }
        else
            return false;
    }

    /**
     * 获取全部的sql并且把他们拼接为：sql1,sql2,sql3的形式
     * @param fragments
     * @return
     */
    public static String getAllNativeSql(List<? extends SqlFragment> fragments){
        StringBuffer stringBuffer=new StringBuffer();
        for (SqlFragment sqlFragment :fragments) {
            String fr=sqlFragment.getNativeSql();
            if(!isNullOrEmpty(fr))
                stringBuffer.append(fr).append(",");
        }
        return stringBuffer.substring(0,stringBuffer.length()-1);
    }

    /**
     * 转换Java对象为一个不为null的字符串对象
     * @param s
     * @return
     */
    public static String getNoNullStr(Object s){
        if(s==null)
            return "";
        else
            return s.toString();
    }

    /**
     * 获取一个类似于元组形式的字符串：(str1,str2,str3,str4)
     * @param strings
     * @return
     */
    public static String getTupleStr(List<String> strings){
        StringBuffer stringBuffer=new StringBuffer("(");
        for (String s:strings) {
            if (!isNullOrEmpty(s))
                stringBuffer.append(s).append(",");
        }
        return stringBuffer.substring(0,stringBuffer.length()-1)+")";
    }

    /**
     * 根据自己的需求转换String，效率可能会低
     * @param strings
     * @param concatFunction
     * @param lastFunction
     * @return
     */
    public static String convertStr(List<String> strings, Function<Object[],Void> concatFunction,Function<StringBuffer,String> lastFunction){
        StringBuffer stringBuffer=new StringBuffer();
        for (String s:strings) {
            concatFunction.apply(new Object[]{stringBuffer,s});
        }
        return lastFunction.apply(stringBuffer);
    }

    /**
     * 构建insert语句的values部分
     * @param size
     * @return
     */
    public static String buildValuesSql(int size){
        StringBuffer stringBuffer=new StringBuffer("VALUES(");
        for (int i = 0; i < size; i++) {
            stringBuffer.append("?,");
        }
        return stringBuffer.substring(0,stringBuffer.length()-1)+")";
    }

    /**
     * 获取可以被方法映射管理器识别到的方法的名字getId[class c.c.c.A, class c.c.c.f.B]
     * @param method
     * @return
     */
    public static String getMethodDeclaredName(Method method){
        return method.getName()+ Arrays.toString(method.getParameterTypes());
    }
}
