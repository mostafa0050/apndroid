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
 * @author Martin Adamek <martin.adamek@gmail.com>
 */
public final class NameUtil {

    public static final String SUFFIX = "apndroid";

    static String addSuffix(String currentName) {
        if (currentName == null) {
            return SUFFIX;
        } else {
            return currentName + SUFFIX;
        }
    }

    static String removeSuffix(String currentName) {
        if (currentName.endsWith(SUFFIX)){
            return currentName.substring(0, currentName.length() - SUFFIX.length());
        }else{
            return currentName;
        }
    }

}
