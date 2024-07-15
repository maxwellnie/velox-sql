package com.maxwellnie.velox.sql.core.utils.log;

import com.maxwellnie.velox.sql.core.natives.exception.ExecutorException;

import java.util.List;

/**
 * @author Maxwell Nie
 */
public class LogUtils {
    public static ExecutorException convertToAdaptLoggerException(Throwable throwable, String sql, List<List<Object>> objects) {
        StringBuilder stringBuilder = new StringBuilder("\n#####\n|\n")
                .append("|- ERROR: MESSAGE - ").append(throwable.getMessage()).append("\n")
                .append("|- ERROR: SQL STRING - ").append(sql).append("\n");

        for (List<Object> currentParams : objects) {
            stringBuilder.append("|- ERROR: SQL PARAM - | ");
            for (Object currentParam : currentParams) {
                stringBuilder.append(currentParam.toString()).append(" | ");
            }
            stringBuilder.append("\n");
        }
        ExecutorException executorException = new ExecutorException(stringBuilder.toString(), throwable.getCause());
        executorException.setStackTrace(throwable.getStackTrace());
        return executorException;
    }

    public static String convertExceptionToLogMessage(Throwable throwable) {
        StringBuilder stringBuilder = new StringBuilder("\n|- ERROR MESSAGE: " + throwable.getMessage());
        if (throwable.getCause() != null)
            stringBuilder.append("\n|- ERROR CAUSE: ").append(throwable.getCause());
        return stringBuilder.toString();
    }
}
