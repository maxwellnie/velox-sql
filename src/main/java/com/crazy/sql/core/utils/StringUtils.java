package com.crazy.sql.core.utils;

import com.crazy.sql.core.query.QueryWord;

import java.util.Arrays;

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
        StringBuffer stringBuffer=new StringBuffer("");
        for (int i = 0; i < chars.length; i++) {
            if(chars[i]>=65&&chars[i]<=90){
                stringBuffer.append("_").append((char)(chars[i]+32));
            }else {
                stringBuffer.append(chars[i]);
            }
        }
        return stringBuffer.toString();
    }
    public static String queryWordsArrayToString(QueryWord... queryWords){
        StringBuffer stringBuffer=new StringBuffer("[");
        Arrays.stream(queryWords).forEach((x)->{
            stringBuffer.append(x).append(",");
        });
        return stringBuffer.substring(0,stringBuffer.length()-1)+"]";
    }
}
