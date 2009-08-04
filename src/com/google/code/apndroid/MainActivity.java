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

import java.util.List;

import com.google.code.apndroid.DbUtil.ApnInfo;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * 
 * @author Martin Adamek <martin.adamek@gmail.com>
 */
public class MainActivity extends Activity {

    static final int NOTIFICATION_ID = 1;

    private final Handler mHandler = new Handler();
    private boolean mIsNetEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Fire off a thread to do some work that we shouldn't do directly in the UI thread
        Thread t = new Thread() {
            public void run() {
                mIsNetEnabled = bgGetAPNState();
                if (mIsNetEnabled) {
                    DbUtil.bgDisableAllInDb(getContentResolver());
                    mIsNetEnabled = false;
                } else {
                    DbUtil.bgEnableAllInDb(getContentResolver());
                    mIsNetEnabled = true;
                }
                mHandler.post(mShowDialog);
            }
        };
        t.start();
    }

    /**
     * Reading from DB, should run in background thread
     * 
     * @return current state
     */
    private boolean bgGetAPNState() {
        List<ApnInfo> apns = DbUtil.bgGetApnMap(getContentResolver());
        if (NameUtil.areAllEnabled(apns)) {
            return true;
        } else if (NameUtil.areAllDisabled(apns)) {
            return false;
        } else {
            // inconsistency - some APNs have suffix, some not, let's remove our
            // suffixes and let user to do whatever she wants
            return false;
        }
    }

    private final Runnable mShowDialog = new Runnable() {
        public void run() {
            int iconId = mIsNetEnabled ? R.drawable.stat_apndroid_on : R.drawable.stat_apndroid_off;
            int barTextId = mIsNetEnabled ? R.string.title_enabled : R.string.title_disabled;
            String barText = getResources().getString(barTextId);
            Notification notification = new Notification(iconId, barText, System.currentTimeMillis());

            Intent intent = new Intent(MainActivity.this, InfoActivity.class);
            intent.putExtra(InfoActivity.EXTRA_IS_NET_ENABLED, mIsNetEnabled);

            PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, -1 /* not used in SDK1.0 */, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            String notifyTitle = getResources().getString(R.string.app_name);
            int notifySummaryId = mIsNetEnabled ? R.string.status_enabled : R.string.status_disabled;
            String notifySummaryText = getResources().getString(notifySummaryId);
            notification.setLatestEventInfo(MainActivity.this, notifyTitle, notifySummaryText, pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, notification);

            MainActivity.this.finish();
        }
    };

}
