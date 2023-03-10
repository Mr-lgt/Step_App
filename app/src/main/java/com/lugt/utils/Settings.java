package com.lugt.utils;

import android.content.Context;

import com.lugt.frame.PrefsManager;

public class Settings {
    public static final float[] SENSITIVE_ARRAY = {1.97f, 2.96f, 4.44f, 6.66f, 10.0f, 15.0f, 22.50f, 33.75f, 50.62f};
    public static final int[] INTERVAL_ARRAY = {100, 200, 300, 400, 500, 600, 700, 800};
    public static final String SENSITIVITY = "sensitivity";
    public static final String INTERVAL = "interval";
    public static final String STEP_LEN = "stepLen";
    public static final String BODY_WEIGHT = "bodyWeight";

    private PrefsManager prefsManager;

    public Settings(Context context) {
        prefsManager = new PrefsManager(context);
    }

    /**
     * 获取传感器灵敏度
     *
     * @return
     */
    public double getSensitivity() {
        float sensitivity = prefsManager.getFloat(SENSITIVITY);
        if (sensitivity == 0.0f) {
            return 10.0f;
        }
        return sensitivity;
    }

    /**
     * 设置灵敏度
     * @param sensitivity
     */
    public void setSensitivity(float sensitivity) {
        prefsManager.putFloat(SENSITIVITY, sensitivity);
    }

    /**
     * 获取灵敏度
     * @return
     */
    public int getInterval() {
        int interval = prefsManager.getInt(INTERVAL);
        if (interval == 0) {
            return 200;
        }
        return interval;
    }

    /**
     * 设置灵敏度
     * @param interval
     */
    public void setInterval(int interval){
        prefsManager.putInt(INTERVAL,interval);
    }

    /**
     * 获取步幅
     * @return
     */
    public float getStepLength()
    {
        float stepLength = prefsManager.getFloat(STEP_LEN);
        if (stepLength == 0.0f)
        {
            return 50.0f;
        }
        return stepLength;
    }

    public void setStepLength(float stepLength)
    {
        prefsManager.putFloat(STEP_LEN, stepLength);
    }

    /**
     * 获取体重
     * @return
     */
    public float getBodyWeight()
    {
        float bodyWeight = prefsManager.getFloat(BODY_WEIGHT);
        if (bodyWeight == 0.0f)
        {
            return 60.0f;
        }
        return bodyWeight;
    }

    public void setBodyWeight(float bodyWeight)
    {
        prefsManager.putFloat(BODY_WEIGHT, bodyWeight);
    }
}
