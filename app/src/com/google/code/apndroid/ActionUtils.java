package com.google.code.apndroid;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.google.code.apndroid.dao.ConnectionDao;
import com.google.code.apndroid.dao.DaoUtil;
import com.google.code.apndroid.preferences.Prefs;

/**
 * Utils for public API needs. It is contains common code that is used by service an ActionActivity
 * 
 * @author wingphil
 * @author Dmitry Pavlov <pavlov.dmitry.n@gmail.com>
 * 
 * @see com.google.code.apndroid.ActionActivity
 * @see com.google.code.apndroid.ActionService
 */
public class ActionUtils {

    public static Bundle processSwitchRequest(Context context, Bundle requestExtras) {
        boolean success;
        if (requestExtras == null) {
            // no parameters specified. switch to another state with default settings
            // todo this place can be optimized for one status request (now 2 performed)
            boolean currentConnectionState = DaoUtil.getDao(context).isDataEnabled();
            success = currentConnectionState != Utils.switchAndNotify(context);
        } else {
            // check what parameters specified by api caller
            boolean mmsTargetIncluded = requestExtras.containsKey(Constants.TARGET_MMS_STATE);
            boolean notificationIncluded = requestExtras.containsKey(Constants.SHOW_NOTIFICATION);

            Log.i(Constants.APP_LOG, "MMS target state is " + (mmsTargetIncluded ? "specified" : "unspecified"));
            Log.i(Constants.APP_LOG, "Show notification icon setting is " + (notificationIncluded ? "specified" : "unspecified"));

            boolean targetStateEnabled = requestExtras.getInt(Constants.TARGET_APN_STATE) == Constants.STATE_ON;
            boolean targetKeepMmsActive = false;
            boolean showNotification;
            // if some parameters not specified, load default shared preferences
            Prefs prefs = new Prefs(context);
            if (!mmsTargetIncluded) {
                targetKeepMmsActive = prefs.keepMmsActive();
            } else {
                targetKeepMmsActive = requestExtras.getInt(Constants.TARGET_MMS_STATE, Constants.STATE_OFF) == Constants.STATE_ON;
            }
            if (!notificationIncluded) {
                showNotification = prefs.showNotifications();
            } else {
                showNotification = requestExtras.getBoolean(Constants.SHOW_NOTIFICATION, Prefs.DEFAULT_SHOW_NOTIFICATION);
            }

            ConnectionDao dao = DaoUtil.getDao(context);
            success = Utils.switchIfNecessaryAndNotify(targetStateEnabled, targetKeepMmsActive, showNotification, context, dao);
        }
        Bundle responseExtras = new Bundle();
        responseExtras.putBoolean(Constants.RESPONSE_SWITCH_SUCCESS, success);
        return responseExtras;
    }

    public static Bundle processStatusRequest(Context context) {
        ConnectionDao dao = DaoUtil.getDao(context);

        boolean dataEnabled = dao.isDataEnabled();
        Bundle responseExtras = new Bundle();
        responseExtras.putInt(Constants.RESPONSE_APN_STATE, dataEnabled ? Constants.STATE_ON : Constants.STATE_OFF);
        if (!dataEnabled) {
            responseExtras.putInt(Constants.RESPONSE_MMS_STATE, dao.isMmsEnabled() ? Constants.STATE_ON : Constants.STATE_OFF );
        }
        return responseExtras;
    }

}
