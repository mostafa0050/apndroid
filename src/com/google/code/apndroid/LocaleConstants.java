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
 * @author Julien Muniak <julien.muniak@gmail.com>
 */
public final class LocaleConstants {
    private LocaleConstants() {
        throw new UnsupportedOperationException("LocaleConstants(): Cannot instantiate Constants");
    }

    /**
     * Constant used for storing and retrieving the message text from store-and-forward {@code Intent}s.
     */
    protected static final String INTENT_EXTRA_STATE = "com.google.code.apndroid.localestate";

    protected static final String INTENT_EXTRA_SHOW_NOTIFICATION = "com.google.code.apndroid.localshownotifcation";
    protected static final String LOCALE_PLUGIN_LOG_TAG = "apndroid.locale";
    protected static final String INTENT_EXTRA_KEEP_MMS = "com.google.code.apndroid.localkeepmms";
}