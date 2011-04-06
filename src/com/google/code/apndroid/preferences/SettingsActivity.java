package com.google.code.apndroid.preferences;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import android.telephony.TelephonyManager;
import com.google.code.apndroid.NotificatorReceiver;
import com.google.code.apndroid.R;
import com.google.code.apndroid.Utils;
import com.google.code.apndroid.dao.ConnectionDao;
import com.google.code.apndroid.dao.DaoFactory;

public class SettingsActivity extends PreferenceActivity {

    private OnSharedPreferenceChangeListener mListener;
    private boolean mIsNativeToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // whole UI of main activity is constructed as Preferences
        addPreferencesFromResource(R.xml.preferences);
        mListener = new SwitchNotificationPropertyListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.unregisterOnSharedPreferenceChangeListener(mListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        Prefs prefs = new Prefs(this);
        prefs.registerListener(mListener);

        mIsNativeToggle = prefs.isNativeToggle();


        // disable Native Toggle preference checkbox if data is turned off
        // one can change the switching method only when data is on
        boolean dataEnabled = DaoFactory.getDao(this).isDataEnabled();
        CheckBoxPreference nativeToggle = (CheckBoxPreference) findPreference(Prefs.SETTINGS_NATIVE_TOGGLE);

        boolean apnToggleWorks = isApnToggleWorks();
        
        if (!apnToggleWorks){
            //force set to native toggle
            if (!nativeToggle.isChecked()){
                nativeToggle.setChecked(true);
            }
        }
        nativeToggle.setEnabled(dataEnabled && apnToggleWorks);

        if (dataEnabled || !apnToggleWorks) {
        	Preference help = findPreference(Prefs.PREFERENCES_HELP);
        	if (help != null) {
        		getPreferenceScreen().removePreference(help);
        	}
        }
        if (apnToggleWorks){
            Preference cdmaTip = findPreference(Prefs.PREFERENCES_CDMA_TIP);
            if (cdmaTip != null){
                getPreferenceScreen().removePreference(cdmaTip);
            }
        }
        // check if ads hiding is expired
        CheckBoxPreference ads = (CheckBoxPreference) findPreference(Prefs.SETTINGS_ADS_ENABLED);
        
        ads.setChecked(prefs.isAdsEnabled());
    }

    private boolean isApnToggleWorks(){
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        return manager.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA;
    }

    private class SwitchNotificationPropertyListener implements OnSharedPreferenceChangeListener {

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String propertyName) {
        	
            if (Prefs.SETTINGS_USE_SWITCH_NOTIFICATION.equals(propertyName)) {
                boolean value = sharedPreferences.getBoolean(propertyName, false);
                if (value) {
                    setUpNotification(sharedPreferences);
                } else {
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(NotificatorReceiver.NOTIFICATION_ID);
                }
            } else if (Prefs.SETTINGS_NATIVE_TOGGLE.equals(propertyName)) {
                boolean isNativeToggleProperty = sharedPreferences.getBoolean(Prefs.SETTINGS_NATIVE_TOGGLE, Prefs.DEFAULT_NATIVE_TOGGLE);
                if (isNativeToggleProperty != mIsNativeToggle) {
                    performSwitchOfDaoTypes();
                }
            } else if (Prefs.SETTINGS_ADS_ENABLED.equals(propertyName)) {
            	boolean enable = sharedPreferences.getBoolean(Prefs.SETTINGS_ADS_ENABLED, true);
            	if (enable) {
            		sharedPreferences.edit().remove(Prefs.SETTINGS_ADS_EXPIRY);
            	} else {
            		long currentTime = System.currentTimeMillis();
            		// TODO check is this time handling is correct, maybe display datetime when it will expire?
            		sharedPreferences.edit().putLong(Prefs.SETTINGS_ADS_EXPIRY, currentTime + 14/*days*/*24/*hours*/*60/*minutes*/*60/*seconds*/*1000/*milliseconds*/).commit();
            	}
            }
        }

        private void performSwitchOfDaoTypes() {
            ConnectionDao dao = DaoFactory.getDao(SettingsActivity.this, mIsNativeToggle);
            if (!dao.isDataEnabled() || !dao.isMmsEnabled()) {
                // state is off. for correct switch to another dao type we need
                // to restore connection and then rollback it of state with new dao type
                dao.setDataEnabled(true);
                dao = DaoFactory.getDao(SettingsActivity.this);
                dao.setDataEnabled(false);
            }
        }

        private void setUpNotification(SharedPreferences sharedPreferences) {
            boolean enabled = DaoFactory.getDao(SettingsActivity.this).isDataEnabled();
            Utils.broadcastStatusChange(SettingsActivity.this, enabled, true);
        }

    }

}
