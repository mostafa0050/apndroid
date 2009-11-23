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

import java.text.MessageFormat;

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
     * Convinience method for switching apn state to another state (based on current system state).
     * It performs switch and send notification about
     * it by sending broadcast message. As a result method also returns current apn state.
     * If you does not need some special logic for switching it's the best way.
     *
     * @param context current application context
     * @return current apn state after switch procedure.
     */
    public static boolean switchAndNotify(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        boolean showNotification = preferences.getBoolean(ApplicationConstants.SETTINGS_SHOW_NOTIFICATION, true);
        boolean keepMms = !preferences.getBoolean(ApplicationConstants.SETTINGS_KEEP_MMS_ACTIVE, true);

        ApnDao dao = new ApnDao(context.getContentResolver());
        boolean currentState = dao.getApnState();

        return switchAndNotify(!currentState, keepMms, showNotification, context, dao)
                ? !currentState
                : currentState;
    }

    /**
     * Performs "smart" switching. It defines current apn state and if it does not equal desired state,
     * then it performs switch.
     * <br>
     * This method also set the flag for updating UI (mms state)
     *
     * @param targetState      target state
     * @param modifyMms        if need to modify mms (active only if passed target state is false)
     * @param showNotification show notification on success switch
     * @param context          application context
     * @param dao              apn dao.
     * @return
     */
    public static boolean switсhIfNecessaryAndNotify(boolean targetState, boolean modifyMms,
                                                     boolean showNotification,
                                                     Context context, ApnDao dao) {
        boolean currentState = dao.getApnState();
        if (currentState != targetState) {
            return SwitchingAndMessagingUtils.switchAndNotify(targetState, modifyMms, showNotification, context, dao);
        } else if (!targetState) {//main states are equals but let check what is up with mms states
            boolean currentMmsState = dao.getMmsState();
            if (currentMmsState != modifyMms) {
                //current and target mms states are not equals lets switch only mms apns now.
                boolean success = dao.switchMmsState(currentMmsState);
                if (success) {
                    storeMmsSettings(context, modifyMms);
                }
                return success;
            }
        }
        return true;
    }

    /**
     * Performs direct switching to passed target state. This method should be used if you already has apnDao.
     * Passing existing dao helps to avoid creating a new one
     *
     * @param isTargerStateOn  target state
     * @param modifyMms        if need to modify mms (active only if passed target state is false)
     * @param showNotification show notification on success switch
     * @param context          application context
     * @param dao              apn dao.
     * @return {@code true} if switch was successfull and {@code false} otherwise
     */
    public static boolean switchAndNotify(boolean isTargerStateOn, boolean modifyMms, boolean showNotification,
                                          Context context, ApnDao dao) {
        if (Log.isLoggable(ApplicationConstants.APP_LOG, Log.INFO)) {
            Log.i(ApplicationConstants.APP_LOG,
                    MessageFormat.format("switching apn state [target={0}, modifyMms={1}, showNotification={2}]",
                            isTargerStateOn, modifyMms, showNotification));
        }
        dao.setModifyMms(modifyMms);
        boolean success = dao.switchApnState(!isTargerStateOn);
        if (success) {
            sendStatusMessage(context, isTargerStateOn, showNotification);
            if (!isTargerStateOn) {
                storeMmsSettings(context, modifyMms);
            }
        }
        if (Log.isLoggable(ApplicationConstants.APP_LOG, Log.INFO)) {
            Log.i(ApplicationConstants.APP_LOG, "switch success=" + success);
        }
        return success;
    }

    private static void storeMmsSettings(Context context, boolean modifyMms) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(ApplicationConstants.SETTINGS_KEEP_MMS_ACTIVE, !modifyMms)
                .putBoolean(ApplicationConstants.SETTINGS_CHANGED, true)
                .commit();
    }

    /**
     * Performs direct switching to passed target state
     *
     * @param isTargerStateOn  target state
     * @param modifyMms        if need to modify mms (active only if passed target state is false)
     * @param showNotification show notification on success switch
     * @param context          application context
     * @return {@code true} if switch was successfull and {@code false} otherwise
     */
    public static boolean switchAndNotify(boolean isTargerStateOn, boolean modifyMms, boolean showNotification, Context context) {
        return switchAndNotify(isTargerStateOn, modifyMms, showNotification, context, new ApnDao(context.getContentResolver()));
    }
}
