package com.ULock.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Set;

public class PreferenceUtils {

    private PreferenceUtils() {}
//method to invoke shared preference memory
    public static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
//get the strings type shared preference value
    public static String getString(String key, String defaultValue, Context context) {
        return getPreferences(context).getString(key, defaultValue);
    }
//get the strings type shared preference value
    public static Set<String> getStringSet(String key, Set<String> defaultValue, Context context) {
        return getPreferences(context).getStringSet(key, defaultValue);
    }
//get the int type shared preference value
    public static int getInt(String key, int defaultValue, Context context) {
        return getPreferences(context).getInt(key, defaultValue);
    }
//get the Long type shared preference value
    public static long getLong(String key, long defaultValue, Context context) {
        return getPreferences(context).getLong(key, defaultValue);
    }
//get the float type shared preference value
    public static float getFloat(String key, float defaultValue, Context context) {
        return getPreferences(context).getFloat(key, defaultValue);
    }
//get the Boolean type shared preference value
    public static boolean getBoolean(String key, boolean defaultValue, Context context) {
        return getPreferences(context).getBoolean(key, defaultValue);
    }
    //method to invoke shared preference memory editor
    public static SharedPreferences.Editor getEditor(Context context) {
        return getPreferences(context).edit();
    }
//set the int type shared preference value
    public static void putString(String key, String value, Context context) {
        getEditor(context).putString(key, value).apply();
    }
//set the String type shared preference value
    public static void putStringSet(String key, Set<String> value, Context context) {
        getEditor(context).putStringSet(key, value).apply();
    }
//set the int type shared preference value
    public static void putInt(String key, int value, Context context) {
        getEditor(context).putInt(key, value).apply();
    }
//set the long type shared preference value
    public static void putLong(String key, long value, Context context) {
        getEditor(context).putLong(key, value).apply();
    }
//set the float type shared preference value
    public static void putFloat(String key, float value, Context context) {
        getEditor(context).putFloat(key, value).apply();
    }
//set the boolean type shared preference value
    public static void putBoolean(String key, boolean value, Context context) {
        getEditor(context).putBoolean(key, value).apply();
    }
//remove value from shared preference memory
    public static void remove(String key, Context context) {
        getEditor(context).remove(key).apply();
    }
    //remove all value from shared preference memory
    public static void clear(Context context) {
        getEditor(context).clear().apply();
    }
}