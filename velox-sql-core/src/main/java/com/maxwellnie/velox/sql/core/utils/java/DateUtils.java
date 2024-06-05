package com.maxwellnie.velox.sql.core.utils.java;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期工具类。
 * @author Maxwell Nie
 */
public class DateUtils {
    /**
     * 时间格式转换。
     * @param date
     * @param dateFormat
     * @return 转换好的时间字符串
     */
    public static String convert(Date date, DateFormat dateFormat){
        return dateFormat.format(date);
    }

    /**
     * 中国时间格式转换。
     * @param date
     * @return 转换好的时间字符串
     */
    public static String chConvert(Date date){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

}
