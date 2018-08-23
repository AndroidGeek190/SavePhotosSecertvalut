package com.ULock.Prefhelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// class to help save app info in shared preference and used it from the preference memory when needed
public class Prefshelper {
    public static final String KEY_PREFS_USER_INFO = "user_info";
    private Context context;
    public static SharedPreferences preferences;

    public Prefshelper(Context context) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public Context getContext() {
        return context;
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }

    public void storePin(String pin) {
        Editor edit = getPreferences().edit();
        edit.putString("pin", pin);
        edit.commit();

    }
    //method to get the info of pin lock stored in the shared preference memory
    public String getPin() {
        return getPreferences().getString("pin", "");
    }

    public void storePackageType(String pin) {
        Editor edit = getPreferences().edit();
        edit.putString("package_type", pin);
        edit.commit();

    }
    //method to store pattern lock in the shared preference memory
    public String getPackageType() {
        return getPreferences().getString("package_type", "");
    }

    public void storePattern(String pin) {
        Editor edit = getPreferences().edit();
        edit.putString("pattern", pin);
        edit.commit();

    }
    //method to get the info of pattern lock stored in the shared preference memory
    public String getPattern() {
        return getPreferences().getString("pattern", "");
    }


    //method to store pin lock in the shared preference memory
    public void storeConfirmPin(String pin) {
        Editor edit = getPreferences().edit();
        edit.putString("cpin", pin);
        edit.commit();

    }
    //method to store password lock in the shared preference memory

    public void storePassword(String pin) {
        Editor edit = getPreferences().edit();
        edit.putString("pass", pin);
        edit.commit();

    }
    //method to get the info of password lock stored in the shared preference memory
    public String getPassword() {
        return getPreferences().getString("pass", "");
    }

    public void storeConfirmPass(String pin) {
        Editor edit = getPreferences().edit();
        edit.putString("cpass", pin);
        edit.commit();

    }

    //method to get the info of confirm pin lock stored in the shared preference memory
    public String getConfirmPin() {
        return getPreferences().getString("cpin", "");
    }

    //method to get the info of confirm password lock stored in the shared preference memory
    public String getConfirmPass() {
        return getPreferences().getString("cpass", "");
    }

    //to store the type of locks used for locking the app
    public void storeLockType(String lock) {
        Editor edit = getPreferences().edit();
        edit.putString("lock_type", lock);
        edit.commit();

    }
    //method to get the info of type of locks stored in the shared preference memory
    public String getLockType() {
        return getPreferences().getString("lock_type", "");
    }

    public void storeSecondaryLock(String lock) {
        Editor edit = getPreferences().edit();
        edit.putString("secondary_lock", lock);
        edit.commit();

    }
    //method to get the info of second/confirm lock stored in the shared preference memory

    public String getSecondaryLock() {
        return getPreferences().getString("secondary_lock", "");
    }

    public void appLocked(String locked) {
        Editor edit = getPreferences().edit();
        edit.putString("locked", locked);
        edit.commit();

    }
    //method to get the info of second/confirm lock stored in the shared preference memory
    public String isAppLocked() {
        return getPreferences().getString("locked","");
    }
//store encrypt string for the lock pattern for first lock screen of any lock type(for first lock type) so the lock cannot be read
    public void istImage(String image) {
        Editor edit = getPreferences().edit();
        edit.putString("istImage", image);
        edit.commit();

    }
    public String getIstImage() {
        return getPreferences().getString("istImage","");
    }
    //store encrypt string for the lock pattern for second lock screen of any lock type (for first lock type)so the lock cannot be read
        public void secondImage(String image) {
        Editor edit = getPreferences().edit();
        edit.putString("secImage", image);
        edit.commit();

    }
    //store encrypt string for the lock pattern for first lock screen of any lock type(for second lock type) so the lock cannot be read
    public String getSecondImage() {
        return getPreferences().getString("secImage","");
    }

    public void thirdImage(String image) {
        Editor edit = getPreferences().edit();
        edit.putString("thirdImage", image);
        edit.commit();

    }
    //store encrypt string for the lock pattern for second lock screen of any lock type (for second lock type)so the lock cannot be read

    public String getThirdImage() {
        return getPreferences().getString("thirdImage","");
    }

    public void positionOfIstApp(String image) {
        Editor edit = getPreferences().edit();
        edit.putString("fposition", image);
        edit.commit();

    }
    //to store the position of 1 app locked
    public String getPositionOfIstApp() {
        return getPreferences().getString("fposition","");
    }

    public void positionOfSecApp(String image) {
        Editor edit = getPreferences().edit();
        edit.putString("sposition", image);
        edit.commit();

    }
    //to store the position of 2nd app locked
    public String getPositionOfSecApp() {
        return getPreferences().getString("sposition","");
    }
    public void positionOfThirdApp(String image) {
        Editor edit = getPreferences().edit();
        edit.putString("tposition", image);
        edit.commit();

    }
    //to store the position of 3rd app locked
    public String getPositionOfThirdApp() {
        return getPreferences().getString("tposition","");
    }

    public void storeStartTimeOfApp(String image) {
        Editor edit = getPreferences().edit();
        edit.putString("start_time", image);
        edit.commit();

    }
//get the starting time of app use
    public String getStartTimeOfApp() {
        return getPreferences().getString("start_time","");
    }
//get the starting time of app and store it it in shared preference memory
    public void storeStopTimeOfApp(String time) {
        Editor edit = getPreferences().edit();
        edit.putString("stop_time", time);
        edit.commit();

    }
    //to get the end time of app used
    public String getStopTimeOfApp() {
        return getPreferences().getString("stop_time","");
    }

    public boolean storeCheckBoxStates(Boolean[] array, String arrayName, Context mContext) {
        Editor edit = getPreferences().edit();
        edit.putInt(arrayName +"_size", array.length);

        for(int i=0;i<array.length;i++)
            edit.putBoolean(arrayName + "_" + i, array[i]);
        return edit.commit();

    }
//    public Boolean[] getCheckBoxStates(String arrayName, Context mContext) {
//        int size = getPreferences().getInt(arrayName + "_size", 0);
//        Boolean array[] = new Boolean[size];
//        for(int i=0;i<size;i++)
//            array[i] = getPreferences().getBoolean(arrayName + "_" + i, false);
//        return array;
//    }

    //to store the in app purchase purchased time
    public void isIInAppPurchased(String time) {
        Editor edit = getPreferences().edit();
        edit.putString("iinapppurchase", time);
        edit.commit();

    }
    //to store the in app purchase purchased type
    public String getIInAppPurchased() {
        return getPreferences().getString("iinapppurchase","");
    }
    public void storeservice(String pin) {
        Editor edit = getPreferences().edit();
        edit.putString("service", pin);
        edit.commit();

    }
    //get the purchased package services
    public String getservice() {
        return getPreferences().getString("service", "");
    }

// get app locked package names
    public void storepkg(String pin) {
        Editor edit = getPreferences().edit();
        edit.putString("pkg", pin);
        edit.commit();

    }
    //get the package type time
    public String getpkg() {
        return getPreferences().getString("pkg", "");
    }

    public void storeTimer(String lock) {
        Editor edit = getPreferences().edit();
        edit.putString("timer", lock);
        edit.commit();

    }
    //get time of the package purchased
    public String getTimer() {
        return getPreferences().getString("timer", "");
    }


}
