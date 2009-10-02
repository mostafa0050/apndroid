package com.google.code.apndroid;

import android.content.Context;
import android.content.Intent;

/**
 * Date: 30.09.2009
 * @author Pavlov Dmitry <pavlov.dmitry.n@gmail.com>
 */
public class MessagingUtils {
    public static void sendStatusMessage(Context context, boolean isEnabled, boolean showNotification) {
        Intent message = new Intent(ApplicationConstants.APN_DROID_STATUS);
        message.putExtra(ApplicationConstants.APN_DROID_STATUS_EXTRA, isEnabled);
        message.putExtra(ApplicationConstants.APN_DROID_SHOW_NOTIFICATION, showNotification);
        context.sendBroadcast(message);
    }
}
