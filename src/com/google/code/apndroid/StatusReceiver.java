package com.google.code.apndroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.appwidget.AppWidgetManager;
import android.os.Bundle;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.NotificationManager;

/**
 * @author Pavlov "Zelgadis: Dmitry
 */
public class StatusReceiver extends BroadcastReceiver {

    static final String APN_DROID_STATUS = "com.google.code.apndroid.intent.action.STATUS_CHANGED";
    static final String APN_DROID_STATUS_EXTRA = "com.google.code.apndroid.intent.extra.STATUS";

    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (APN_DROID_STATUS.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                boolean isNetEnabled = extras.getBoolean(APN_DROID_STATUS_EXTRA);
                performStatusChange(context, isNetEnabled);
            }
        }
    }

    private void performStatusChange(Context context, boolean isNetEnabled) {
        int iconId = isNetEnabled ? R.drawable.stat_apndroid_on : R.drawable.stat_apndroid_off;
        int barTextId = isNetEnabled ? R.string.title_enabled : R.string.title_disabled;
        String barText = context.getResources().getString(barTextId);
        Notification notification = new Notification(iconId, barText, System.currentTimeMillis());

        Intent intent = new Intent(context, InfoActivity.class);
        intent.putExtra(InfoActivity.EXTRA_IS_NET_ENABLED, isNetEnabled);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, -1 /* not used in SDK1.0 */, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        String notifyTitle = context.getResources().getString(R.string.app_name);
        int notifySummaryId = isNetEnabled ? R.string.status_enabled : R.string.status_disabled;
        String notifySummaryText = context.getResources().getString(notifySummaryId);
        notification.setLatestEventInfo(context, notifyTitle, notifySummaryText, pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }    

}
