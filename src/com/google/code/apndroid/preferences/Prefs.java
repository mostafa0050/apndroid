package com.google.code.apndroid.preferences;

import com.google.code.apndroid.Constants;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

public class Prefs {

    // TODO replace this status with settings toggle button preference
    public static final String LAST_CONNECTION_STATUS = "com.google.code.apndroid.widget.STATUS";

    public static final boolean DEFAULT_SHOW_NOTIFICATION = true;
    public static final boolean DEFAULT_KEEP_MMS_ACTIVE = true;

    private static final boolean DEFAULT_USE_SWITCH_NOTIFICATION = false;
    private static final boolean DEFAULT_DISABLE_ALL = false;
    public static final boolean DEFAULT_NATIVE_TOGGLE = false;

    private static final String SETTINGS_KEEP_MMS_ACTIVE = "com.google.code.apndroid.preferences.KEEP_MMS_ENABLED";
    private static final String SETTINGS_SHOW_NOTIFICATION = "com.google.code.apndroid.preferences.SHOW_NOTIFICATION";
    public static final String SETTINGS_USE_SWITCH_NOTIFICATION = "com.google.code.apndroid.preferences.SETTINGS_USE_SWITCH_NOTIFICATION";
    private static final String SETTINGS_DISABLE_ALL = "com.google.code.apndroid.preferences.DISABLE_ALL";
    public static final String SETTINGS_NATIVE_TOGGLE = "com.google.code.apndroid.preferences.NATIVE_SWITCH"; 
    static final String SETTINGS_ADS_ENABLED = "com.google.code.apndroid.preferences.ADS_ENABLED";
    static final String SETTINGS_ADS_EXPIRY = "com.google.code.apndroid.preferences.ADS_EXPIRY";

    public static final String PREFERENCES_HELP = "com.google.code.apndroid.preferences.HELP";
    public static final String PREFERENCES_CDMA_TIP = "com.google.code.apndroid.preferences.CDMA_TIP";

    private final SharedPreferences mPreferences;

    public Prefs(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void registerListener(OnSharedPreferenceChangeListener listener) {
        mPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public boolean showNotifications() {
        return mPreferences.getBoolean(SETTINGS_SHOW_NOTIFICATION, DEFAULT_SHOW_NOTIFICATION);
    }

    public boolean keepMmsActive() {
        return mPreferences.getBoolean(SETTINGS_KEEP_MMS_ACTIVE, DEFAULT_KEEP_MMS_ACTIVE);
    }

    public void setKeepMmsActive(boolean keep) {
        mPreferences.edit().putBoolean(SETTINGS_KEEP_MMS_ACTIVE, keep).commit();
    }

    public boolean isUseSwitchNotification() {
        return mPreferences.getBoolean(SETTINGS_USE_SWITCH_NOTIFICATION, DEFAULT_USE_SWITCH_NOTIFICATION);
    }

    public boolean isNativeToggle() {
        return mPreferences.getBoolean(SETTINGS_NATIVE_TOGGLE, DEFAULT_NATIVE_TOGGLE);
    }

    public boolean setNativeToggle(boolean enable) {
        return mPreferences.edit().putBoolean(SETTINGS_NATIVE_TOGGLE, enable).commit();
    }

    public boolean hasNativeToggle() {
        return mPreferences.contains(SETTINGS_NATIVE_TOGGLE);
    }

    public boolean isLastStatusConnected() {
        return mPreferences.getBoolean(LAST_CONNECTION_STATUS, true);
    }

    public boolean hasLastStatus() {
        return mPreferences.contains(LAST_CONNECTION_STATUS);
    }

    public void setLastStatus(boolean connected) {
        mPreferences.edit().putBoolean(LAST_CONNECTION_STATUS, connected).commit();
    }

    public int getTargetMmsState() {
        return mPreferences.getInt(Constants.TARGET_MMS_STATE, Constants.DEFAULT_MMS_STATE);
    }

    public boolean isDisableAll() {
        return mPreferences.getBoolean(SETTINGS_DISABLE_ALL, DEFAULT_DISABLE_ALL);
    }

    public boolean isAdsEnabled() {
    	long expiration = mPreferences.getLong(SETTINGS_ADS_EXPIRY, System.currentTimeMillis() - 1000);
    	long currentTime = System.currentTimeMillis();
    	return currentTime > expiration;
    }
    
}
