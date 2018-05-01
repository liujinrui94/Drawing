package com.rdc.drawing.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreUtil {
    private static final String APPLICATION_PREFERENCES = "app_prefs";
    private static SharedPreferences.Editor editor;
    private static SharedPreferences pref;

    /**
     * 保存String类类型的数据
     *
     * @param context
     * @param key
     * @param value
     */
    public static void saveStringPref(Context context, String key, String value) {
        initEditor(context);
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 保存布尔值类型的数据
     *
     * @param context
     * @param key
     * @param value
     */
    public static void saveBooleanPref(Context context, String key, boolean value) {
        initEditor(context);
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 保存整形数据
     *
     * @param context
     * @param key
     * @param value
     */
    public static void saveIntPref(Context context, String key, int value) {
        initEditor(context);
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * 获取字符串类型的数据
     *
     * @param context
     * @param key
     * @return
     */
    public static String getStringPref(Context context, String key) {
        return getStringPref(context, key, "");
    }



    public static String getStringPref(Context context, String key,
                                       String defaultValue) {//保存默认值
        initPref(context);
        return pref.getString(key, defaultValue);
    }

    /**
     * 获取布尔值类型的数据
     *
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    public static boolean getBooleanPref(Context context, String key,
                                         boolean defaultValue) {
        initPref(context);
        return pref.getBoolean(key, defaultValue);
    }

    /**
     * 获取整形类型的数据
     *
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    public static int getIntPref(Context context, String key, int defaultValue) {
        initPref(context);
        return pref.getInt(key, defaultValue);
    }


    /**
     * 私有工具类
     * @param context
     */

    private static void initEditor(Context context) {
        initPref(context);
        editor = pref.edit();
    }

    private static void initPref(Context context) {
        // 空判断，防止空指针，导致程序崩溃。
        if (null == pref)
            pref = context.getSharedPreferences(APPLICATION_PREFERENCES,
                    Context.MODE_PRIVATE);
    }
}
