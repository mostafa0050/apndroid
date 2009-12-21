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
import android.preference.PreferenceManager;

/**
 * Broadcast receiver that performs switching current apn state and performs notification about this through sending
 * a broadcast message {@link com.google.code.apndroid.ApplicationConstants#STATUS_CHANGED_MESSAGE}
 *
 * @author Pavlov Dmitry <pavlov.dmitry.n@gmail.com>
 */
public class SwitchStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ApplicationConstants.CHANGE_STATUS_REQUEST.equals(intent.getAction())) {
            Bundle bundle = intent.getExtras();
            if (bundle == null || bundle.size() == 0) {//no params
                SwitchingAndMessagingUtils.switchAndNotify(context);
            } else {
                int onState = ApplicationConstants.State.ON;
                int targetState = bundle.getInt(ApplicationConstants.TARGET_APN_STATE, onState);
                int mmsTarget = bundle.getInt(ApplicationConstants.TARGET_MMS_STATE, onState);
                boolean showNotification = bundle.getBoolean(ApplicationConstants.SHOW_NOTIFICATION, true);
                boolean disableAll = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(ApplicationConstants.SETTINGS_DISABLE_ALL, false);
                ApnDao apnDao = new ApnDao(context.getContentResolver());
                apnDao.setDisableAllApns(disableAll);
                SwitchingAndMessagingUtils.switchIfNecessaryAndNotify(targetState, mmsTarget,
                        showNotification, context, apnDao);
            }
        }
    }
}
