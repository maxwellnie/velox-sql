package com.maxwellnie.velox.jpa.framework.utils;

import java.util.List;

/**
 * @author Maxwell Nie
 */
public class ErrorUtils {

    public static String getExceptionLog(Throwable throwable, String sql, List<Object> objects) {
        StringBuffer stringBuffer = new StringBuffer("ERROR LOG \n").append("ERROR: SQL ERROR\n")
                .append("ERROR: MESSAGE - ").append(throwable.getMessage()).append("\n")
                .append("ERROR: SQL STRING - ").append(sql).append("\n")
                .append("ERROR: SQL PARAM - | ");
        for (Object param : objects) {
            stringBuffer.append(param.toString()).append(" | ");
        }
        if (throwable.getCause() != null)
            stringBuffer.append("\nCAUSE: ").append(throwable.getCause());
        return stringBuffer.toString();
    }

    public static String getSimpleExceptionLog(Throwable throwable) {
        StringBuffer stringBuffer = new StringBuffer("ERROR MESSAGE: " + throwable.getMessage());
        if (throwable.getCause() != null)
            stringBuffer.append("ERROR CAUSE: " + throwable.getCause());
        return stringBuffer.toString();
    }
}
