package com.google.code.apndroid.internal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import com.google.code.apndroid.Constants;
import com.google.code.apndroid.Utils;
import com.google.code.apndroid.dao.ConnectionDao;
import com.google.code.apndroid.dao.DaoFactory;
import com.google.code.apndroid.preferences.Prefs;

import java.text.MessageFormat;

/**
 * Class stub 
 * @author pavlov
 * @since 27.08.2010
 */
public class InternalDaoBroadcastHelper extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            Log.d(Constants.APP_LOG, "received on start broadcast");
            processOnPhoneBootEvent(context);
        } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            Log.d(Constants.APP_LOG, "received connection state changed broadcast");
            processOnConnectionChangedEvent(context, intent.getExtras());
        }
    }

    private void processOnPhoneBootEvent(Context context) {

    }

    private void processOnConnectionChangedEvent(Context context, Bundle bundle) {

    }
}
