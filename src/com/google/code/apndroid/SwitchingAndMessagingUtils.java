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
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.text.MessageFormat;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Date: 30.09.2009
 *
 * @author Pavlov Dmitry <pavlov.dmitry.n@gmail.com>
 */
public class SwitchingAndMessagingUtils {
    private static final int DATA_CONNECTION_CHECK_TIME = (int) TimeUnit.SECONDS.toMillis(10);

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
    public static int switchAndNotify(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        int onState = ApplicationConstants.State.ON;
        int offState = ApplicationConstants.State.OFF;

        boolean showNotification = preferences.getBoolean(ApplicationConstants.SETTINGS_SHOW_NOTIFICATION, true);
        int mmsTarget = preferences.getBoolean(ApplicationConstants.SETTINGS_KEEP_MMS_ACTIVE, true) ? onState : offState;
        boolean disableAll = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(ApplicationConstants.SETTINGS_DISABLE_ALL, false);
        ApnDao dao = new ApnDao(context.getContentResolver());
        dao.setDisableAllApns(disableAll);
        int currentState = dao.getApnState();
        int targetState = currentState == onState ? offState : onState;
        return switchAndNotify(targetState, mmsTarget, showNotification, context, dao)
                ? targetState
                : currentState;
    }

    /**
     * Performs "smart" switching. It defines current apn state and if it does not equal desired state,
     * then it performs switch.
     * <br>
     * This method also set the flag for updating UI (mms state)
     *
     * @param targetState      target state
     * @param mmsTarget        if need to modify mms (active only if passed target state is false)
     * @param showNotification show notification on success switch
     * @param context          application context
     * @param dao              apn dao.
     * @return {@code true} if switch was sucessfull and {@code false} otherwise.
     */
    public static boolean switchIfNecessaryAndNotify(int targetState, int mmsTarget,
                                                     boolean showNotification,
                                                     Context context, ApnDao dao) {
        int currentState = dao.getApnState();
        if (currentState != targetState) {
            return SwitchingAndMessagingUtils.switchAndNotify(targetState, mmsTarget, showNotification, context, dao);
        } else if (targetState == ApplicationConstants.State.OFF) {//main states are equals but let check what is up with mms states
            int currentMmsState = dao.getMmsState();
            if (currentMmsState != mmsTarget) {
                //current and target mms states are not equals lets switch only mms apns now.
                boolean success = dao.switchMmsState(mmsTarget);
                if (success) {
                    storeMmsSettings(context, mmsTarget);
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
     * @param targetState      target state
     * @param mmsTarget        mmsTarget state
     * @param showNotification show notification on success switch
     * @param context          application context
     * @param dao              apn dao.
     * @return {@code true} if switch was successfull and {@code false} otherwise
     */
    public static boolean switchAndNotify(int targetState, int mmsTarget, boolean showNotification,
                                          Context context, ApnDao dao) {
        if (Log.isLoggable(ApplicationConstants.APP_LOG, Log.INFO)) {
            Log.i(ApplicationConstants.APP_LOG,
                    MessageFormat.format("switching apn state [target={0}, modifyMms={1}, showNotification={2}]",
                            targetState, mmsTarget, showNotification));
        }
        int onState = ApplicationConstants.State.ON;
        dao.setMmsTarget(mmsTarget);
        //this var is used for storing preferred apn in switch on->off, and as a container for restoring id in switch off->on
        long preferredApnId = -1;
        if (targetState == onState) {
            preferredApnId = PreferenceManager.getDefaultSharedPreferences(context).
                    getLong(ApplicationConstants.SETTING_PREFERRED_APN, -1);
        } else {
            preferredApnId = dao.getPreferredApnId();
        }
        boolean success = dao.switchApnState(targetState);
        if (success) {
            sendStatusMessage(context, targetState == onState, showNotification);
            if (targetState != onState) {
                storeMmsSettings(context, mmsTarget);
                //storing preferred apn id
                PreferenceManager.getDefaultSharedPreferences(context).
                        edit().putLong(ApplicationConstants.SETTING_PREFERRED_APN, preferredApnId).commit();
            } else {
                long currentPreferredApn = dao.getPreferredApnId();
                Log.d(ApplicationConstants.APP_LOG, "Current Preferred APN="+currentPreferredApn+", stored preferred APN="+preferredApnId);
                //reinitializing preferred apn
                tryFixConnection(dao, preferredApnId);
            }
        }
        if (Log.isLoggable(ApplicationConstants.APP_LOG, Log.INFO)) {
            Log.i(ApplicationConstants.APP_LOG, "switch success=" + success);
        }
        return success;
    }

    private static void registerDataStateListener(Context context, ApnDao dao) {
        final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        DataConnectionListener listener = new DataConnectionListener(telephonyManager);
        telephonyManager.listen(listener, PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);

        long preferredApn = PreferenceManager.getDefaultSharedPreferences(context).
                getLong(ApplicationConstants.SETTING_PREFERRED_APN, -1);

        long currentPreferredApn = dao.getPreferredApnId();
        if (currentPreferredApn == -1L){
            tryFixConnection(dao, preferredApn);
        }
    }

    private static void storeMmsSettings(Context context, int mmsTarget) {
        boolean keepMmsActive = mmsTarget == ApplicationConstants.State.ON;
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(ApplicationConstants.SETTINGS_KEEP_MMS_ACTIVE, keepMmsActive)
                .commit();
    }

    /**
     * Performs direct switching to passed target state
     *
     * @param targetState      target state
     * @param mmsTarget        mms target state
     * @param showNotification show notification on success switch
     * @param context          application context
     * @return {@code true} if switch was successful and {@code false} otherwise
     */
    public static boolean switchAndNotify(int targetState, int mmsTarget, boolean showNotification, Context context) {
        boolean disableAll = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(ApplicationConstants.SETTINGS_DISABLE_ALL, false);
        ApnDao apnDao = new ApnDao(context.getContentResolver());
        apnDao.setDisableAllApns(disableAll);
        return switchAndNotify(targetState, mmsTarget, showNotification, context, apnDao);
    }

    static void tryFixConnection(ApnDao dao, long preferredApn) {
        Log.d(ApplicationConstants.APP_LOG, "trying to fix connection");
        if (preferredApn != -1) {
            dao.restorePreferredApn(preferredApn);
        } else {
            //we does not have preferred apn now, so lets try to set some random data apn
            long apnId = dao.getRandomCurrentDataApn();
            if (apnId != -1) {
                dao.restorePreferredApn(apnId);
            } else {
                Log.w(ApplicationConstants.APP_LOG, "no apn found for connection fix");
            }
        }
    }

    private static class DataConnectionChecker extends TimerTask {
        private DataConnectionListener listener;
        private ApnDao dao;
        private long preferredApn;

        public DataConnectionChecker(DataConnectionListener listener, ApnDao dao, long preferredApn) {
            this.listener = listener;
            this.dao = dao;
            this.preferredApn = preferredApn;
        }

        public void run() {
            Log.d(ApplicationConstants.APP_LOG, "data connection checker task started");
            listener.cancelListeningProcess();
            if (!listener.apnForConnectionFound) {
                tryFixConnection(dao, preferredApn);
            }
        }
    }

    private static class DataConnectionListener extends PhoneStateListener {

        boolean apnForConnectionFound = false;
        private TelephonyManager telephonyManager;
        private boolean listenerCanceled;

        private DataConnectionListener(TelephonyManager manager) {
            this.telephonyManager = manager;
        }

        @Override
        public void onDataConnectionStateChanged(int state) {
            Log.d(ApplicationConstants.APP_LOG, "data connection state changed");
            if (state == TelephonyManager.DATA_CONNECTED) {
                Log.d(ApplicationConstants.APP_LOG, "state switched to connected... ok!");
                cancelListeningProcess();
                apnForConnectionFound = true;
            }
        }

        public void cancelListeningProcess() {
            Log.d(ApplicationConstants.APP_LOG, "canceling listener");
            telephonyManager.listen(this, LISTEN_NONE);
        }
    }
}
