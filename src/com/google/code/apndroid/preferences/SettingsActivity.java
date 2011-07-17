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
import com.google.code.apndroid.dao.DaoUtil;

public class SettingsActivity extends PreferenceActivity {

    private OnSharedPreferenceChangeListener mListener;

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

        // disable Native Toggle preference checkbox if data is turned off
        // one can change the switching method only when data is on
        boolean dataEnabled = DaoUtil.getDao(this).isDataEnabled();
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
            }
        }

        private void setUpNotification(SharedPreferences sharedPreferences) {
            boolean enabled = DaoUtil.getDao(SettingsActivity.this).isDataEnabled();
            Utils.broadcastStatusChange(SettingsActivity.this, enabled, true);
        }

    }

}
