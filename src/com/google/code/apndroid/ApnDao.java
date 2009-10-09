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

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Martin Adamek <martin.adamek@gmail.com>
 * @author Pavlov Dmitry <pavlov.dmitry.n@gmail.com>
 */
public final class ApnDao {

    private static final String ID = "_id";
    private static final String APN = "apn";
    private static final String TYPE = "type";

    // from frameworks/base/core/java/android/provider/Telephony.java
    static final Uri CONTENT_URI = Uri.parse("content://telephony/carriers");

    private static final String DB_LIKE_SUFFIX = "%" + NameUtil.SUFFIX;

    private ContentResolver contentResolver;

    private boolean modifyInternet = true;
    private boolean modifyMms = true;

    public ApnDao(ContentResolver contentResolver, boolean modifyInternet, boolean modifyMms) {
        this.contentResolver = contentResolver;
        this.modifyInternet = modifyInternet;
        this.modifyMms = modifyMms;
    }

    public ApnDao(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    List<ApnInfo> getEnabledApnsMap() {
        String query;
        boolean modifyInternet = this.modifyInternet;
        boolean modifyMms = this.modifyMms;
        if (modifyInternet && modifyMms) {
            query = "current is not null";
        } else if (modifyInternet && !modifyMms) {
            query = "(not lower(type)='mms' or type is null) and current is not null";
        } else if (!modifyInternet && modifyMms) {
            query = "lower(type)='mms' and current is not null";
        } else {
            return Collections.EMPTY_LIST;
        }
        return selectApnInfo(query, null);
    }

    List<ApnInfo> getDisabledApnsMap() {
        return selectApnInfo("apn like ? or type like ?", new String[]{DB_LIKE_SUFFIX, DB_LIKE_SUFFIX});
    }

    private List<ApnInfo> selectApnInfo(String whereQuery, String[] whereParams) {
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(CONTENT_URI, new String[]{ID, APN, TYPE}, whereQuery, whereParams, null);
            return createApnList(cursor);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    void enableAllInDb() {
        List<ApnInfo> apns = getDisabledApnsMap();
        enableAllInDb(apns);
    }

    /**
     * Use this one if you have fresh list of APNs already and you can save one query to DB
     *
     * @param apns list of apns data to modify
     */
    void enableAllInDb(List<ApnInfo> apns) {
        final ContentResolver contentResolver = this.contentResolver;
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
            contentResolver.update(CONTENT_URI, values, ID + "=?", new String[]{apnInfo.id});
        }
    }


    private List<ApnInfo> createApnList(Cursor mCursor) {
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

    void disableAllInDb() {
        List<ApnInfo> apns = getEnabledApnsMap();
        final ContentResolver contentResolver = this.contentResolver;
        for (ApnInfo apnInfo : apns) {
            ContentValues values = new ContentValues();
            String newApnName = NameUtil.addSuffixIfNotPresent(apnInfo.apn);
            values.put(APN, newApnName);
            String newApnType = NameUtil.addSuffixIfNotPresent(apnInfo.type);
            values.put(TYPE, newApnType);
            contentResolver.update(CONTENT_URI, values, ID + "=?", new String[]{apnInfo.id});
        }
    }

    /**
     * Calculates current apn state and perfroms switch to another (on -> off, off->on)
     *
     * @return new apn state ({@code true} if apn is now enabled, and {@code false} if apn is disabled)
     */
    boolean switchApnState() {
        boolean currentState = getApnState();
        switchApnState(currentState);
        return !currentState;
    }

    /**
     * Perorms switching apns work state according to passed state parameter
     *
     * @param enabled apn state. this method tries to make a switch to another state( enabled == true -> off, enabled == false -> on)
     */
    void switchApnState(boolean enabled) {
        if (enabled) {
            disableAllInDb();
        } else {
            enableAllInDb();
        }
    }

    /**
     * Calculates current apn state
     *
     * @return current apn state;
     */
    boolean getApnState() {
        return countDisabledApns() == 0;
    }

    int countAllApns() {
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(CONTENT_URI, new String[]{"count(*)"}, "current is not null", null, null);
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            } else {
                return -1;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    int countEnabledApns() {
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(CONTENT_URI, new String[]{"count(*)"}, "apn not like ? and type not like ?", new String[]{DB_LIKE_SUFFIX, DB_LIKE_SUFFIX}, null);
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            } else {
                return -1;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    int countDisabledApns() {
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(CONTENT_URI, new String[]{"count(*)"}, "apn like ? or type like ?", new String[]{DB_LIKE_SUFFIX, DB_LIKE_SUFFIX}, null);
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            } else {
                return -1;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public boolean isModifyInternet() {
        return modifyInternet;
    }

    public void setModifyInternet(boolean modifyInternet) {
        this.modifyInternet = modifyInternet;
    }

    public boolean isModifyMms() {
        return modifyMms;
    }

    public void setModifyMms(boolean modifyMms) {
        this.modifyMms = modifyMms;
    }

    /**
     * Selection of few interesting columns from APN table
     */
    static final class ApnInfo {

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
