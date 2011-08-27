package com.google.code.apndroid.preferences;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
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
