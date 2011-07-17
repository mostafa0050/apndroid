/*
 * This file is part of APNdroid.
 *
 * APNdroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * APNdroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with APNdroid. If not, see <http://www.gnu.org/licenses/>.
 */

package com.google.code.apndroid;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.code.apndroid.preferences.Prefs;

/**
 * This receiver is responsible for displaying notifications in notification bar.
 * 
 * It is also setting toggle button property, which I don't like, should be removed probably.
 * 
 * @author Dmitry Pavlov <pavlov.dmitry.n@gmail.com>
 */
public class NotificatorReceiver extends BroadcastReceiver {

    public static final int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Constants.STATUS_CHANGED_MESSAGE.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                // let's save current state in preferences
                Prefs prefs = new Prefs(context);
                final boolean isNetEnabled = extras.getBoolean(Constants.STATUS_EXTRA);
                prefs.setLastStatus(isNetEnabled);
                // check if we should show notification
                if (extras.getBoolean(Constants.SHOW_NOTIFICATION, true)) {
                    final boolean showConstantlyInStatusBar = prefs.isUseSwitchNotification();
                    performNotificationStatusChange(context, isNetEnabled, showConstantlyInStatusBar);
                }
            }
        }
    }

    private void performNotificationStatusChange(Context context, boolean isNetEnabled, boolean showConstantlyInStatusBar) {
        int iconId = isNetEnabled ? R.drawable.stat_apndroid_on : R.drawable.stat_apndroid_off;
        // display notification icon without text
        Notification notification = new Notification(iconId, null, System.currentTimeMillis());
        // notification.setLatestEventInfo();
        Intent intent;

        if (showConstantlyInStatusBar) {
            notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
            // intent to perform apn switching on click on notification
            intent = new Intent(context, ActionActivity.class);
            intent.setAction(Constants.CHANGE_STATUS_REQUEST);
        } else {
            // intent to open main activity on click on notification
            intent = new Intent(context, MainActivity.class);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, -1, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        String notifyTitle = context.getResources().getString(R.string.app_name);
        int notifySummaryId = isNetEnabled ? R.string.status_enabled : R.string.status_disabled;
        String notifySummaryText = context.getResources().getString(notifySummaryId);
        notification.setLatestEventInfo(context, notifyTitle, notifySummaryText, pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID, notification);
    }

}
