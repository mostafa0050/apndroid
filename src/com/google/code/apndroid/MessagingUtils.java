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
