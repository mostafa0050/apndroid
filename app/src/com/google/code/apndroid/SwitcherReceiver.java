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
import com.google.code.apndroid.dao.ConnectionDao;
import com.google.code.apndroid.dao.DaoUtil;

/**
 * Broadcast receiver that performs switching current apn state and performs notification about this through sending a
 * broadcast message {@link com.google.code.apndroid.Constants#STATUS_CHANGED_MESSAGE}
 *
 * @author Dmitry Pavlov <pavlov.dmitry.n@gmail.com>
 */
public class SwitcherReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        String action = intent.getAction();
        if (Constants.CHANGE_STATUS_REQUEST.equals(action)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    processStandardSwitchEvent(context, intent);
                }
            }).start();
        }
    }

    private void processStandardSwitchEvent(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null || bundle.size() == 0) { // no params
            Utils.switchAndNotify(context);
        } else {
            boolean targetDataEnabled = bundle.getInt(Constants.TARGET_APN_STATE, Constants.STATE_ON) == Constants.STATE_ON;
            boolean targetMmsEnabled = bundle.getInt(Constants.TARGET_MMS_STATE, Constants.STATE_ON) == Constants.STATE_ON;
            boolean showNotification = bundle.getBoolean(Constants.SHOW_NOTIFICATION, true);
            ConnectionDao dao = DaoUtil.getDao(context);
            Utils.switchIfNecessaryAndNotify(targetDataEnabled, targetMmsEnabled, showNotification, context, dao);
        }
    }    

}
