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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
