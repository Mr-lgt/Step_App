package com.lugt.frame;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 日志管理类
 */
public class LogWriter {
    private static final String TAG = "iStep";
    private static boolean isDebug = true;
    private static boolean isWriteToLog = false;

    /**
     * 将日志输出到文件
     * @param tag
     * @param logText
     */
    public static void logToFile(final String tag,final String logText){
        if (!isWriteToLog){
            return;
        }
        final String writeMessage = tag + " : " + logText;
        final String fileName = Environment.getExternalStorageDirectory().getPath()
                + "/logFile.txt";

        final File file = new File(fileName);
        try {
            final BufferedWriter bufferedWriter = new BufferedWriter(
                    new FileWriter(file,true));
            bufferedWriter.write(writeMessage);
            bufferedWriter.newLine();
            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 对控制台打印的日志进行封装
     */

    public static void d(final String msg){
        if (isDebug){
            Log.d(TAG,msg);
        }

        if (isWriteToLog){
            logToFile(TAG,msg);
        }
    }

    public static void e(final String msg){
        if (isDebug){
            Log.e(TAG,msg);
        }

        if (isWriteToLog){
            logToFile(TAG,msg);
        }
    }

    public static void i(final String msg){
        if (isDebug){
            Log.i(TAG,msg);
        }

        if (isWriteToLog){
            logToFile(TAG,msg);
        }
    }

    public static void v(final String msg){
        if (isDebug){
            Log.v(TAG,msg);
        }

        if (isWriteToLog){
            logToFile(TAG,msg);
        }
    }

    public static void w(final String msg){
        if (isDebug){
            Log.w(TAG,msg);
        }

        if (isWriteToLog){
            logToFile(TAG,msg);
        }
    }
}
