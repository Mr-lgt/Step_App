package com.lugt.frame;

import android.app.Activity;
import android.app.Application;
import android.os.Process;

import java.util.LinkedList;

/**
 * 1、保存Activity列表
 * 2、完全退出应用程序
 * 3、异常处理
 */
public class FrameApplication extends Application {
    //保存Activity的列表
    private static LinkedList<Activity> activityList = new LinkedList<>();
    private PrefsManager prefsManager;
    private static FrameApplication instance;
    private ErrorHandler errorHandler;

    public PrefsManager getPrefsManager(){
        return prefsManager;
    }

    /**
     * 初始化
     */
    public void onCreate() {
        super.onCreate();
        prefsManager = new PrefsManager(this);
        instance = this;
        errorHandler = ErrorHandler.getInstance();
    }

    public static FrameApplication getInstance(){
        return instance;
    }

    public static LinkedList<Activity> getActivityList() {
        return activityList;
    }

    /**
     * 添加到列表
     * @param activity
     */
    public static void addActivity(Activity activity){
        if (activity != null){
            activityList.add(activity);
        }
    }

    /**
     * 移除Activity
     * @param activity
     */
    public static void removeActivity(Activity activity){
        if (activityList != null && activityList.size() > 0 && activityList.indexOf(activity) != -1){
            activityList.remove(activity);
        }
    }

    /**
     * 清理所有Activity
     */
    public static void cleanActivityList(){
        for (final Activity activity : activityList) {
            if (activity != null){
                activity.finish();
            }
        }
    }

    /**
     * 退出进程
     */
    public static void exitApp(){
        try {
            cleanActivityList();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            System.exit(0);
            Process.killProcess(Process.myPid());
        }
    }


}
