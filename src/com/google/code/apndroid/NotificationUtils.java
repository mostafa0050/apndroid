package com.google.code.apndroid;

import android.content.Context;
import android.content.Intent;

/**
 * Date: 30.09.2009
 * @author Pavlov Dmitry <pavlov.dmitry.n@gmail.com>
 */
public class NotificationUtils {
    public static void sendStatusNotification(Context context, boolean isEnabled) {
        Intent message = new Intent(StatusReceiver.APN_DROID_STATUS);
        message.putExtra(StatusReceiver.APN_DROID_STATUS_EXTRA, isEnabled);
        context.sendBroadcast(message);
    }
}
