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

/**
 * @author Pavlov Dmitry
 * @since 03.10.2009
 */
public final class ApplicationConstants {

    public static final String STATUS_EXTRA = "com.google.code.apndroid.intent.extra.STATUS";
    /**
     * Intent name for send to request current apn state.
     */
    public static final String STATUS_REQUEST = "com.google.code.apndroid.intent.action.STATUS_REQUEST";
    /**
     * Intent name for send to switch apn state.
     */
    public static final String CHANGE_STATUS_REQUEST = "com.google.code.apndroid.intent.action.CHANGE_REQUEST";


    public static final String SHOW_NOTIFICATION = "com.google.code.apndroid.intent.extra.SHOW_NOTIFICATION";

    public static final String TARGET_MMS_STATE = "com.google.code.apndroid.intent.extra.TARGET_MMS_STATE";

    public static final String TARGET_APN_STATE = "com.google.code.apndroid.intent.extra.TARGET_STATE";

    /**
     * Intent name for returned result
     */
    public static final String APN_DROID_RESULT = "com.google.code.apndroid.intent.REQUEST_RESULT";
    /**
     * Extra name that holds main apn state
     */
    public static final String RESPONSE_APN_STATE = "APN_STATE";
    /**
     * Extra name that holds mms state. This extra set only if main apn state is 'off' (result == false)
     */
    public static final String RESPONSE_MMS_STATE = "MMS_STATE";
    /**
     * Intent name that holds switch result. It is {@code true} if the resulted switched request performed succesfully
     * and {@code false} otherwise.
     */
    public static final String RESPONSE_SWITCH_SUCCESS = "SWITCH_SUCCESS";

    public static final class State{
        public static final int OFF = 0;
        public static final int ON = 1;
    }

    static final String STATUS_CHANGED_MESSAGE = "com.google.code.apndroid.intent.action.STATUS_CHANGED";

    static final String SETTINGS_TOGGLE_BUTTON = "toggle_preference";

    static final String SETTINGS_KEEP_MMS_ACTIVE = "com.google.code.apndroid.preferences.KEEP_MMS_ENABLED";
    static final String SETTINGS_SHOW_NOTIFICATION = "com.google.code.apndroid.preferences.SHOW_NOTIFICATION";
    static final String SETTINGS_DISABLE_ALL = "com.google.code.apndroid.preferences.DISABLE_ALL";

    static final String SETTING_PREFERRED_APN= "preferred_apn_id";

    /**
     * Common log tag
     */
    static final String APP_LOG = "apndroid.log";

}
