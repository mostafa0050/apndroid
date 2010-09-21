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

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.code.apndroid.dao.ConnectionDao;
import com.google.code.apndroid.dao.DaoFactory;
import com.google.code.apndroid.preferences.Prefs;

/**
 * @author Pavlov Dmitry <pavlov.dmitry.n@gmail.com>
 */
public final class Utils {

    public static void broadcastStatusChange(Context context, boolean isEnabled, boolean showNotification) {
        Intent message = new Intent(Constants.STATUS_CHANGED_MESSAGE);
        message.putExtra(Constants.STATUS_EXTRA, isEnabled);
        message.putExtra(Constants.SHOW_NOTIFICATION, showNotification);
        context.sendBroadcast(message);
    }

    public static void broadcastStatusChange(Context context, boolean isEnabled) {
        Prefs prefs = new Prefs(context);
        boolean showNotification = prefs.showNotifications();
        broadcastStatusChange(context, isEnabled, showNotification);
    }

    /**
     * Convenience method for switching apn state to another state (based on current system state). It performs switch
     * and send notification about it by sending broadcast message. As a result method also returns current apn state.
     * If you does not need some special logic for switching it's the best way.
     *
     * @param context current application context
     * @return current apn state after switch procedure.
     */
    public static boolean switchAndNotify(Context context) {
        Prefs prefs = new Prefs(context);

        boolean showNotification = prefs.showNotifications();
        boolean keepMmsActive = prefs.keepMmsActive();
        ConnectionDao dao = DaoFactory.getDao(context);
        boolean currentState = dao.isDataEnabled();
        return switchAndNotify(!currentState, keepMmsActive, showNotification, context, dao);
    }

    /**
     * Performs "smart" switching. It defines connection state and if it does not equal desired state, then it performs switch. <br>
     * This method also set the flag for updating UI (mms state)
     *
     * @param targetStateEnabled target state
     * @param enableMms          if need to modify mms (active only if passed target state is false)
     * @param showNotification   show notification on success switch
     * @param context            application context
     * @param dao                apn dao.
     * @return {@code true} if switch was sucessfull and {@code false} otherwise.
     */
    public static boolean switchIfNecessaryAndNotify(boolean targetStateEnabled, boolean enableMms, boolean showNotification, Context context, ConnectionDao dao) {
        boolean currentStateEnabled = dao.isDataEnabled();
        if (currentStateEnabled != targetStateEnabled) {
            return Utils.switchAndNotify(targetStateEnabled, enableMms, showNotification, context, dao);
        } else if (!targetStateEnabled) {
            // main states are equals but let check what is up with mms states
            boolean currentMmsEnabled = dao.isMmsEnabled();
            if (currentMmsEnabled != enableMms) {
                // current and target mms states are not equals lets switch only mms apns now.
                boolean success = dao.setMmsEnabled(enableMms);
                if (success) {
                    storeMmsSettings(context, enableMms);
                }
                return success;
            }
        }
        return true;
    }

    public static boolean isConnectedOrConnecting(Context context, boolean anyConnectionType) {
    	ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo info = connectivity.getActiveNetworkInfo();

    	return info != null
                && (info.getType() == ConnectivityManager.TYPE_MOBILE || anyConnectionType) 
                && info.isConnectedOrConnecting();
    }

    /**
     * Performs direct switching to passed target state. This method should be used if you already has initialized dao.
     * Passing existing dao helps to avoid creating a new one
     */
    private static boolean switchAndNotify(boolean targetDataStateEnabled, boolean enableMms, boolean showNotification, Context context, ConnectionDao dao) {
        if (Log.isLoggable(Constants.APP_LOG, Log.INFO)) {
            Log.i(Constants.APP_LOG, MessageFormat.format("switching apn state [target={0}, mmsTarget={1}, showNotification={2}]", targetDataStateEnabled, enableMms, showNotification));
        }
        sendSwitchInProgressMessage(context);
        boolean success = false;
        try {
            success = dao.setDataEnabled(targetDataStateEnabled, enableMms);
        } catch (Exception ex) {
            Log.e(Constants.APP_LOG, "Exception occurred while switching data connection",ex);
        } finally {
            // anyway, we should switch to some not in progress state
            if (success) {
                broadcastStatusChange(context, targetDataStateEnabled, showNotification);
                if (!targetDataStateEnabled) {
                    storeMmsSettings(context, enableMms);
                }
            } else {
                broadcastStatusChange(context, !targetDataStateEnabled, showNotification);
            }
        }
        if (Log.isLoggable(Constants.APP_LOG, Log.INFO)) {
            Log.i(Constants.APP_LOG, "switch success=" + success);
        }
        return success;
    }

    private static void storeMmsSettings(Context context, boolean keepMmsActive) {
        Prefs prefs = new Prefs(context);
        prefs.setKeepMmsActive(keepMmsActive);
    }

    private static void sendSwitchInProgressMessage(Context context) {
        Log.d(Constants.APP_LOG, "sending switch in progress broadcast");
        Intent message = new Intent(Constants.STATUS_SWITCH_IN_PROGRESS_MESSAGE);
        context.sendBroadcast(message);
    }

}
