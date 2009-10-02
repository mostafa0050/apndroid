package com.google.code.apndroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.ContentResolver;

/**
 * @author Julien Muniak <julien.muniak@gmail.com>
 */
public class LocaleEventReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (com.twofortyfouram.Intent.ACTION_FIRE_SETTING.equals(intent.getAction())) {
            boolean isNetEnabled = intent.getBooleanExtra(LocaleConstants.INTENT_EXTRA_STATE, true);
            ContentResolver contentResolver = context.getContentResolver();
            boolean currentState = DbUtil.getApnState(contentResolver);
            if (currentState != isNetEnabled) {

                boolean showNotification = intent.getBooleanExtra(LocaleConstants.INTENT_EXTRA_SHOW_NOTIFICATION, true);

                DbUtil.switchApnState(contentResolver, currentState);
                MessagingUtils.sendStatusMessage(context, !isNetEnabled, showNotification);                
            }
        }
    }
}
