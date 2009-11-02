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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * @author Martin Adamek <martin.adamek@gmail.com>
 */
public class InfoActivity extends Activity {

    private static final int ON_OFF_DAILOG = 7;
    static final String EXTRA_IS_NET_ENABLED = "extraIsNetEnabled";

    private boolean mIsNetEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(MainActivity.NOTIFICATION_ID);
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                mIsNetEnabled = extras.getBoolean(EXTRA_IS_NET_ENABLED);
                showDialog(ON_OFF_DAILOG);
            }
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        super.onCreateDialog(id);

        View view = getLayoutInflater().inflate(R.layout.notification_info, null);
        TextView statusText = (TextView) view.findViewById(R.id.status_text);

        int iconId = mIsNetEnabled ? R.drawable.stat_apndroid_on : R.drawable.stat_apndroid_off;
        int textId = mIsNetEnabled ? R.string.title_enabled : R.string.title_disabled;
        int statusId = mIsNetEnabled ? R.string.status_enabled : R.string.status_disabled;
        statusText.setText(statusId);

        return new AlertDialog.Builder(this).setIcon(iconId).setTitle(textId).setView(view).setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                finish();
            }
        }).create();
    }

}
