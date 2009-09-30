package com.google.code.apndroid;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author Julien Muniak <julien.muniak@gmail.com>
 */
public class LocaleEventReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (com.twofortyfouram.Intent.ACTION_FIRE_SETTING.equals(intent.getAction())) {
            boolean isNetEnabled = intent.getBooleanExtra(LocaleConstants.INTENT_EXTRA_STATE, true);

            DbUtil.switchApnState(context.getContentResolver(), isNetEnabled);
            if (intent.getBooleanExtra(LocaleConstants.INTENT_EXTRA_SHOW_NOTIFICATION, true)) {
                NotificationUtils.sendStatusNotification(context, !isNetEnabled);
            }
        }
    }
}
