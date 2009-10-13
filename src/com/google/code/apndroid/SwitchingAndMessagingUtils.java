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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Date: 30.09.2009
 *
 * @author Pavlov Dmitry <pavlov.dmitry.n@gmail.com>
 */
public class SwitchingAndMessagingUtils {
    public static void sendStatusMessage(Context context, boolean isEnabled, boolean showNotification) {
        Intent message = new Intent(ApplicationConstants.STATUS_CHANGED_MESSAGE);
        message.putExtra(ApplicationConstants.STATUS_EXTRA, isEnabled);
        message.putExtra(ApplicationConstants.SHOW_NOTIFICATION, showNotification);
        context.sendBroadcast(message);
    }

    /**
     * Convinience method for switching apn state. It performs switch and send notification about
     * it by throwing broadcast. As a result method also returns current apn state.
     * If you does not need some special logic for switching it's the best way.
     * @param context current application context
     * @return current apn state after switch procedure.
     */
    public static boolean switchAndNotify(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        long start = System.currentTimeMillis();

        ApnDao dao = new ApnDao(context.getContentResolver(),
                !preferences.getBoolean(ApplicationConstants.SETTINGS_KEEP_MMS_ACTIVE, false));
        boolean enabled = dao.switchApnState();

        long end = System.currentTimeMillis();

        if (Log.isLoggable(ApplicationConstants.APP_LOG, Log.INFO)) {
            Log.i(ApplicationConstants.APP_LOG, "Switched in " + (end - start) + " ms");
        }
        boolean showNotification = preferences.getBoolean(ApplicationConstants.SETTINGS_SHOW_NOTIFICATION, true);
        sendStatusMessage(context,enabled, showNotification);
        return enabled;
    }
}
