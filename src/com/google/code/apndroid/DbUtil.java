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

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * 
 * @author Martin Adamek <martin.adamek@gmail.com>
 */
public class DbUtil {

    private static final String ID = "_id";
    private static final String APN = "apn";
    private static final String TYPE = "type";

    // from frameworks/base/core/java/android/provider/Telephony.java
    static final Uri CONTENT_URI = Uri.parse("content://telephony/carriers");

    static List<ApnInfo> bgGetApnMap(ContentResolver contentResolver) {
        Cursor mCursor = contentResolver.query(CONTENT_URI, new String[] { ID, APN, TYPE }, null, null, null);
        List<ApnInfo> result = new ArrayList<ApnInfo>();
        mCursor.moveToFirst();
        while (!mCursor.isAfterLast()) {
            String id = mCursor.getString(0);
            String apn = mCursor.getString(1);
            String type = mCursor.getString(2);
            result.add(new ApnInfo(id, apn, type));
            mCursor.moveToNext();
        }
        return result;
    }

    static void bgEnableAllInDb(ContentResolver contentResolver) {
        List<ApnInfo> apns = bgGetApnMap(contentResolver);
        bgEnableAllInDb(contentResolver, apns);
    }

    /**
     * Use this one if you have fresh list of APNs already and you can save one query to DB
     */
    static void bgEnableAllInDb(ContentResolver contentResolver, List<ApnInfo> apns) {
        for (ApnInfo apnInfo : apns) {
            ContentValues values = new ContentValues();
            String newApnName = NameUtil.removeSuffixIfPresent(apnInfo.apn);
            values.put(APN, newApnName);
            String newApnType = NameUtil.removeSuffixIfPresent(apnInfo.type);
            if ("".equals(newApnType)) {
                values.putNull(TYPE);
            } else {
                values.put(TYPE, newApnType);
            }
            contentResolver.update(CONTENT_URI, values, ID + "=?", new String[] { apnInfo.id });
        }
    }

    static void bgDisableAllInDb(ContentResolver contentResolver) {
        List<ApnInfo> apns = bgGetApnMap(contentResolver);
        for (ApnInfo apnInfo : apns) {
            ContentValues values = new ContentValues();
            String newApnName = NameUtil.addSuffixIfNotPresent(apnInfo.apn);
            values.put(APN, newApnName);
            String newApnType = NameUtil.addSuffixIfNotPresent(apnInfo.type);
            values.put(TYPE, newApnType);
            contentResolver.update(CONTENT_URI, values, ID + "=?", new String[] { apnInfo.id });
        }
    }

    /**
     * Selection of few interesting columns from APN table
     */
    static class ApnInfo {

        final String id;
        final String apn;
        final String type;

        public ApnInfo(String id, String apn, String type) {
            this.id = id;
            this.apn = apn;
            this.type = type;
        }
    }

}
