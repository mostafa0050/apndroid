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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

/**
 * User: Zelgadis
 * Date: 26.11.2009
 */
public class ActionActivity extends Activity {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        if (intent != null) {
            int onState = ApplicationConstants.State.ON;
            if (intent.getAction().equals(ApplicationConstants.STATUS_REQUEST)) {
                processStatusRequest(onState);
            } else if (intent.getAction().equals(ApplicationConstants.CHANGE_STATUS_REQUEST)) {
                processSwitchRequest(intent, onState);
            } else {
                setResult(Activity.RESULT_CANCELED);
            }
        } else {
            setResult(Activity.RESULT_CANCELED);
        }
        finish();
    }

    private void processSwitchRequest(Intent intent, int onState) {
        Bundle extras = intent.getExtras();
        boolean success;
        if (extras == null) {
            //no parameters specified. switch to another state with default settings
            //todo this place can be optimized for one status request (now 2 performed)
            int currentState = new ApnDao(this.getContentResolver()).getApnState();
            success = currentState != SwitchingAndMessagingUtils.switchAndNotify(this);
        } else {
            //check what parameters specified by api caller
            boolean disableAll = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(ApplicationConstants.SETTINGS_DISABLE_ALL, false);
            boolean mmsTargetIncluded = extras.containsKey(ApplicationConstants.TARGET_MMS_STATE);
            boolean notificationIncluded = extras.containsKey(ApplicationConstants.SHOW_NOTIFICATION);
            int targetState = extras.getInt(ApplicationConstants.TARGET_APN_STATE);
            int mmsTarget;
            boolean showNotification;
            //if some parameters not specified, load default shared preferences
            SharedPreferences sp = (!mmsTargetIncluded || !notificationIncluded)
                    ? PreferenceManager.getDefaultSharedPreferences(this)
                    : null;
            if (!mmsTargetIncluded) {
                mmsTarget = sp.getBoolean(ApplicationConstants.SETTINGS_KEEP_MMS_ACTIVE, true)
                        ? ApplicationConstants.State.ON
                        : ApplicationConstants.State.OFF;
            } else {
                mmsTarget = extras.getInt(ApplicationConstants.TARGET_MMS_STATE, onState);
            }
            if (!notificationIncluded) {
                showNotification = sp.getBoolean(ApplicationConstants.SETTINGS_SHOW_NOTIFICATION, true);
            } else {
                showNotification = extras.getBoolean(ApplicationConstants.SHOW_NOTIFICATION, true);
            }
            ApnDao apnDao = new ApnDao(this.getContentResolver());
            apnDao.setDisableAllApns(disableAll);
            success = SwitchingAndMessagingUtils.switchIfNecessaryAndNotify(targetState, mmsTarget,
                    showNotification, this, apnDao);
        }
        Intent response = new Intent(ApplicationConstants.APN_DROID_RESULT);
        response.putExtra(ApplicationConstants.RESPONSE_SWITCH_SUCCESS, success);
        setResult(RESULT_OK, response);
    }

    private void processStatusRequest(int onState) {
        ApnDao dao = new ApnDao(this.getContentResolver());
        Intent response = new Intent(ApplicationConstants.APN_DROID_RESULT);
        int apnState = dao.getApnState();
        response.putExtra(ApplicationConstants.RESPONSE_APN_STATE, apnState);
        if (apnState != onState) {
            response.putExtra(ApplicationConstants.RESPONSE_MMS_STATE, dao.getMmsState());
        }
        setResult(RESULT_OK, response);
    }
}
