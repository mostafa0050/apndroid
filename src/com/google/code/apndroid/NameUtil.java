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

import java.util.Collection;

import com.google.code.apndroid.DbUtil.ApnInfo;

/**
 * 
 * @author Martin Adamek <martin.adamek@gmail.com>
 */
public final class NameUtil {

    public static final String SUFFIX = "apndroid";

    static String addSuffixIfNotPresent(String currentName) {
        String result = currentName;
        if (currentName == null) {
            result = SUFFIX;
        } else if (!currentName.endsWith(SUFFIX)) {
            result = currentName + SUFFIX;
        }
        return result;
    }

    static String removeSuffixIfPresent(String currentName) {
        String result = currentName;
        if (currentName != null && currentName.endsWith(SUFFIX)) {
            result = currentName.substring(0, currentName.length() - SUFFIX.length());
        }
        return result;
    }

    /**
     * Checks if all names are without APNdroid's suffix
     * 
     * @param apnInfos list of ApnInfos
     * @return true only if there is no name with APNdroid's suffix, false otherwise
     */
    static boolean areAllEnabled(Collection<ApnInfo> apnInfos) {
        for (ApnInfo apnInfo : apnInfos) {
            if ((apnInfo.apn != null && apnInfo.apn.endsWith(SUFFIX)) || (apnInfo.type != null && apnInfo.type.endsWith(SUFFIX))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if all names have APNdroid's suffix
     * 
     * @param apnInfos list of ApnInfos
     * @return true only if all names in list have APNdroid's suffix, false otherwise
     */
    static boolean areAllDisabled(Collection<ApnInfo> apnInfos) {
        for (ApnInfo apnInfo : apnInfos) {
            if (apnInfo.apn == null || !apnInfo.apn.endsWith(SUFFIX) || apnInfo.type == null || !apnInfo.type.endsWith(SUFFIX)) {
                return false;
            }
        }
        return true;
    }
}
