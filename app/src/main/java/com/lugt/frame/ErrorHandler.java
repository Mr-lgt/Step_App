package com.lugt.frame;

import android.content.Context;
import androidx.annotation.NonNull;

public class ErrorHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        LogWriter.logToFile("Error", "崩溃摘要：" + e.getMessage());
        LogWriter.logToFile("Error", "崩溃线程：" + t.getName() + " 线程id：" + t.getId());
        //打印堆栈错误信息
        final StackTraceElement[] stackTrace = e.getStackTrace();
        for (final StackTraceElement el : stackTrace) {
            LogWriter.logToFile("Error", "Line: " + el.getLineNumber() + ": " + el.getMethodName());
        }
        e.printStackTrace();
        FrameApplication.exitApp();
    }

    private ErrorHandler() {
    }

    public void setErrorHandler(final Context context) {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    private static ErrorHandler instance;

    public static ErrorHandler getInstance() {
        if (instance == null) {
            synchronized (ErrorHandler.class) {
                if (instance == null) {
                    instance = new ErrorHandler();
                }
            }
        }
        return instance;
    }
}
