package com.kyh.app.config;

import android.content.Context;
import android.content.SharedPreferences;

public class SystemConfigSp {
    private final String fileName = "systemconfig.ini";
    SharedPreferences sharedPreferences;
    private static SystemConfigSp systemConfigSp = new SystemConfigSp();

    public static SystemConfigSp instance() {
        if (systemConfigSp == null) {
            synchronized (SystemConfigSp.class) {
                systemConfigSp = new SystemConfigSp();
            }
        }
        return systemConfigSp;
    }

    private SystemConfigSp() {

    }

    public void init(Context ctx) {
        sharedPreferences = ctx.getSharedPreferences(fileName, ctx.MODE_PRIVATE);
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public String getStrConfig(SysCfgDimension dimension) {
        return sharedPreferences.getString(dimension.name(), "");
    }

    public void setStrConfig(SysCfgDimension dimension, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(dimension.name(), value);
        //提交当前数据
        editor.apply();
    }

    public int getIntConfig(SysCfgDimension dimension) {
        return sharedPreferences.getInt(dimension.name(), 0);
    }

    public void setIntConfig(SysCfgDimension dimension, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(dimension.name(), value);
        //提交当前数据
        editor.apply();
    }

    public long getLongConfig(SysCfgDimension dimension) {
        long strValue = sharedPreferences.getLong(dimension.name(), 0);
        return strValue;
    }

    public void setLongConfig(SysCfgDimension dimension, long value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(dimension.name(), value);
        //提交当前数据
        editor.apply();
    }

    /**
     * 取布尔值
     * @param dimension
     * @return
     */
    public boolean getBooleanConfig(SysCfgDimension dimension) {
        return sharedPreferences.getBoolean(dimension.name(), false);
    }

    /**
     * 存布尔值
     * @param dimension
     * @param value
     */
    public void setBooleanConfig(SysCfgDimension dimension, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(dimension.name(), value);
        editor.apply();
    }

    public int getIntConfig(String key) {
        int strValue = sharedPreferences.getInt(key, 0);
        return strValue;
    }

    public void setIntConfig(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        //提交当前数据
        editor.apply();
    }

    /**
     * 保存float值
     * @param dimension
     * @param value
     */
    public void setFloatConfig(SysCfgDimension dimension, float value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(dimension.name(), value);
        //提交当前数据
        editor.apply();
    }

    /**
     * 目前用到的只有字体大小
     * @param dimension
     * @return
     */
    public float getFloatConfig(SysCfgDimension dimension) {
        return sharedPreferences.getFloat(dimension.name(), 1);
    }

    public enum SysCfgDimension {
        deviceToken,
        notifycationId
    }
}
