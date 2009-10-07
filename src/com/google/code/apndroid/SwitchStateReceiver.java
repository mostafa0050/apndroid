package com.google.code.apndroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

/**
 * Broadcast receiver that performs switching current apn state and performs notification about this through sending
 * a broadcast message {@link com.google.code.apndroid.ApplicationConstants#APN_DROID_STATUS}
 * @author Pavlov Dmitry <pavlov.dmitry.n@gmail.com>
 */
public class SwitchStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ApplicationConstants.APN_DROID_CHANGE_STATUS.equals(intent.getAction())){
            boolean currentState = DbUtil.switchApnState(context.getContentResolver());
            Bundle extras = intent.getExtras();
            boolean showNotification;
            if (extras != null){
                showNotification = extras.getBoolean(ApplicationConstants.APN_DROID_SHOW_NOTIFICATION);
            }else{
                showNotification = context.getSharedPreferences(ApplicationConstants.AND_DROID_SETTINGS, Context.MODE_PRIVATE)
                        .getBoolean(ApplicationConstants.AND_DROID_SETTINGS_SHOW_NOTIFICATION, true);
            }
            MessagingUtils.sendStatusMessage(context, currentState, showNotification);
        }
    }
}
