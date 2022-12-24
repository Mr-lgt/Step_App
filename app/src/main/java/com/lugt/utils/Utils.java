package com.lugt.utils;

import android.app.ActivityManager;
import android.content.Context;

import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class Utils {

    /**
     * 将对象转换为字符串
     *
     * @param obj
     * @return
     */
    public static String objToJson(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    public static long getTimeByDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String dateStr = sdf.format(date);
        try {
            return sdf.parse(dateStr).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public static String getFormatVal(double val)
    {
        return	getFormatVal(val,"#.00");
    }

    /**
     * "#.00"
     * @param val
     * @param format
     * @return
     */
    public static String getFormatVal(double val,String format)
    {
        DecimalFormat df=new DecimalFormat(format);
        return df.format(val);
    }


    /**
     * 判断服务是否运行
     *
     * @param context
     * @param serviceName
     * @return
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        if (context == null && serviceName == null) {
            return false;
        }
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        Iterator<ActivityManager.RunningServiceInfo> iterator = serviceList.iterator();
        while (iterator.hasNext()) {
            ActivityManager.RunningServiceInfo runningServiceInfo = iterator.next();
            if (serviceName.trim().equals(runningServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
