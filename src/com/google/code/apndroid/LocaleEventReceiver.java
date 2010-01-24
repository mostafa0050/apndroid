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
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

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
            int onState = ApplicationConstants.State.ON;
            final Bundle bundle = intent.getExtras();
            int targetState = bundle.getInt(ApplicationConstants.TARGET_APN_STATE, onState);
            int mmsTarget = bundle.getInt(ApplicationConstants.TARGET_MMS_STATE, onState);
            boolean showNotification = intent.getBooleanExtra(ApplicationConstants.SHOW_NOTIFICATION, true);
            boolean disableAll = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(ApplicationConstants.SETTINGS_DISABLE_ALL, false);
            ContentResolver contentResolver = context.getContentResolver();
            ApnDao dao = new ApnDao(contentResolver, mmsTarget);
            dao.setDisableAllApns(disableAll);
            SwitchingAndMessagingUtils.switchIfNecessaryAndNotify(
                    targetState, mmsTarget, showNotification, context, dao
            );
        }
    }
}
