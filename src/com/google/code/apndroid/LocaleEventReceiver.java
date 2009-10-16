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

import java.text.MessageFormat;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Receiver that activated on locale event broadcast.
 * On receive of event try switch apn to a target state
 * If current apn state equals target apn state then switch is not performed.
 *
 * @author Julien Muniak <julien.muniak@gmail.com>
 * @author Pavlov Dmitry <pavlov.dmitry.n@gmail.com>
 */
public class LocaleEventReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (com.twofortyfouram.Intent.ACTION_FIRE_SETTING.equals(intent.getAction())) {
            final Bundle bundle = intent.getExtras();
            boolean targetState = bundle.getBoolean(LocaleConstants.INTENT_EXTRA_STATE, true);
            boolean mmsEnabled = !bundle.getBoolean(LocaleConstants.INTENT_EXTRA_KEEP_MMS, true);

            ContentResolver contentResolver = context.getContentResolver();
            ApnDao dao = new ApnDao(contentResolver, mmsEnabled);
            boolean currentState = dao.getApnState();
            if (currentState != targetState) {
                if (Log.isLoggable(LocaleConstants.LOCALE_PLUGIN_LOG_TAG, Log.INFO)) {
                    Log.i(LocaleConstants.LOCALE_PLUGIN_LOG_TAG, MessageFormat.format("Switching apn state [{0} -> {1}]", currentState, targetState));
                }
                boolean showNotification = intent.getBooleanExtra(LocaleConstants.INTENT_EXTRA_SHOW_NOTIFICATION, true);

                boolean resultState = dao.switchApnState(currentState);
                SwitchingAndMessagingUtils.sendStatusMessage(context, resultState, showNotification);
            }
        }
    }
}
