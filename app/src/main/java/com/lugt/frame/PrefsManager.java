package com.lugt.frame;

import android.annotation.SuppressLint;
import android.content.Context;

/**
 * SharedPreferences的存储管理
 */
public class PrefsManager {
    private final Context mContext;
    private static final String PREFERENCE_NAME = "lugt_step";

    public PrefsManager(final Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 清除存储文件内容
     */
    public void clear()
    {
        this.mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit().clear().commit();
    }

    /**
     * 是否存在该文件
     */
    public boolean contains()
    {
        return this.mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
                .contains(PREFERENCE_NAME);
    }

    public Boolean getBoolean(final String key)
    {
        return this.mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).getBoolean(key, false);
    }

    public Boolean getBooleanDefaultTrue(final String key)
    {
        return this.mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).getBoolean(key, true);
    }

    public Float getFloat(final String key)
    {
        return this.mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).getFloat(key, 0.0f);
    }

    public int getInt(final String key)
    {
        return this.mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).getInt(key, 0);
    }

    public long getLong(final String key)
    {
        return this.mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).getLong(key, 0L);
    }

    public String getString(final String key)
    {
        return this.mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).getString(key, "");
    }

    public boolean putBoolean(final String key, final boolean value)
    {
        return this.mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
                .edit().putBoolean(key, value)
                .commit();
    }

    public boolean putFloat(final String key, final Float value)
    {
        return this.mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
                .edit().putFloat(key, value).commit();
    }

    public boolean putInt(final String key, final int value)
    {
        return this.mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
                .edit().putInt(key, value).commit();
    }

    public boolean putLong(final String key, final Long value)
    {
        return this.mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
                .edit().putLong(key, value).commit();
    }

    public boolean putString(final String key, final String value)
    {
        return this.mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
                .edit().putString(key, value).commit();
    }
}
