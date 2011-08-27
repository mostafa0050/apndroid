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

package com.google.code.apndroid.locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.google.code.apndroid.Constants;
import com.google.code.apndroid.Utils;
import com.google.code.apndroid.dao.ConnectionDao;
import com.google.code.apndroid.dao.DaoUtil;

/**
 * Receiver that activated on locale event broadcast. On receive of event try
 * switch apn to a target state If current apn state equals target apn state
 * then switch is not performed.
 *
 * @author Julien Muniak <julien.muniak@gmail.com>
 * @author Dmitry Pavlov <pavlov.dmitry.n@gmail.com>
 */
public class LocaleEventReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (com.twofortyfouram.locale.Intent.ACTION_FIRE_SETTING.equals(intent.getAction())) {
            if (LocaleSerializeProtectionUtil.checkForCustomSerializableAttack(intent)) return;

            final Bundle bundle = intent.getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);

            if (bundle == null){
                Log.w(Constants.APP_LOG, "Locale bundle is null, can't perform switching");
                return;
            }

            if (LocaleSerializeProtectionUtil.checkForCustomSerializableAttack(bundle)) return;

            boolean targetDataEnabled = bundle.getInt(Constants.TARGET_APN_STATE, Constants.STATE_ON) == Constants.STATE_ON;
            boolean targetMmsEnabled = bundle.getInt(Constants.TARGET_MMS_STATE, Constants.STATE_ON) == Constants.STATE_ON;
            boolean showNotification = bundle.getBoolean(Constants.SHOW_NOTIFICATION, true);
            ConnectionDao dao = DaoUtil.getDao(context);
            Utils.switchIfNecessaryAndNotify(targetDataEnabled, targetMmsEnabled, showNotification, context, dao);
        }
    }

}
