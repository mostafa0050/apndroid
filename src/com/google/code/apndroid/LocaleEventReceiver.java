package com.google.code.apndroid;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 
 * @author Julien Muniak <julien.muniak@gmail.com>
 */
public class LocaleEventReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (com.twofortyfouram.Intent.ACTION_FIRE_SETTING.equals(intent.getAction()))
        {
            boolean IsNetEnabled = intent.getBooleanExtra(LocaleConstants.INTENT_EXTRA_STATE, true);
            
            if (!IsNetEnabled) {
                DbUtil.bgDisableAllInDb(context.getContentResolver());
            } else {
                DbUtil.bgEnableAllInDb(context.getContentResolver());
            }
            if (intent.getBooleanExtra(LocaleConstants.INTENT_EXTRA_SHOW_NOTIFICATION, true)) {
                int iconId = IsNetEnabled ? R.drawable.stat_apndroid_on : R.drawable.stat_apndroid_off;
                int barTextId = IsNetEnabled ? R.string.title_enabled : R.string.title_disabled;
                String barText = context.getResources().getString(barTextId);
                Notification notification = new Notification(iconId, barText, System.currentTimeMillis());
                
                Intent intentdlg = new Intent(context, InfoActivity.class);
                intentdlg.putExtra(InfoActivity.EXTRA_IS_NET_ENABLED, IsNetEnabled);

                PendingIntent pendingIntent = PendingIntent.getActivity(context, -1 /* not used in SDK1.0 */, intentdlg, PendingIntent.FLAG_CANCEL_CURRENT);

                String notifyTitle = context.getResources().getString(R.string.app_name);
                int notifySummaryId = IsNetEnabled ? R.string.status_enabled : R.string.status_disabled;
                String notifySummaryText = context.getResources().getString(notifySummaryId);
                notification.setLatestEventInfo(context, notifyTitle, notifySummaryText, pendingIntent);

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(MainActivity.NOTIFICATION_ID, notification);
            }
        }
    }
}
