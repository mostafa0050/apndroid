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

    private boolean modifyMms = true;

    public ApnDao(ContentResolver contentResolver, boolean modifyMms) {
        this.contentResolver = contentResolver;
        this.modifyMms = modifyMms;
    }

    public ApnDao(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    List<ApnInfo> getEnabledApnsMap() {
        String query;
        boolean modifyMms = this.modifyMms;
        if (modifyMms) {
            query = "current is not null";
        } else {
            query = "(not lower(type)='mms' or type is null) and current is not null";
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

    boolean enableAllInDb() {
        List<ApnInfo> apns = getDisabledApnsMap();
        return enableApnList(apns);
    }

    /**
     * Creates list of apn dtos from a DB cursor
     *
     * @param mCursor db cursor with select result set
     * @return list of APN dtos
     */
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

    /**
     * Tries to disable apn's according to user preferences.
     *
     * @return {@code true} if one o more apns changed and {@code false} if all APNs did not changed their states
     */
    boolean disableAllInDb() {
        List<ApnInfo> apns = getEnabledApnsMap();

        if (apns.isEmpty()) return false;

        return disableApnList(apns);
    }

    /**
     * Use this one if you have fresh list of APNs already and you can save one query to DB
     *
     * @param apns list of apns data to modify
     */
    private boolean enableApnList(List<ApnInfo> apns) {
        final ContentResolver contentResolver = this.contentResolver;
        for (ApnInfo apnInfo : apns) {
            ContentValues values = new ContentValues();
            String newApnName = NameUtil.removeSuffix(apnInfo.apn);
            values.put(APN, newApnName);
            String newApnType = NameUtil.removeSuffix(apnInfo.type);
            if ("".equals(newApnType)) {
                values.putNull(TYPE);
            } else {
                values.put(TYPE, newApnType);
            }
            contentResolver.update(CONTENT_URI, values, ID + "=?", new String[]{apnInfo.id});
        }
        return true;//we always return true because in any situation we can reset all apns to initial state
    }

    private boolean disableApnList(List<ApnInfo> apns) {
        final ContentResolver contentResolver = this.contentResolver;
        for (ApnInfo apnInfo : apns) {
            ContentValues values = new ContentValues();
            String newApnName = NameUtil.addSuffix(apnInfo.apn);
            values.put(APN, newApnName);
            String newApnType = NameUtil.addSuffix(apnInfo.type);
            values.put(TYPE, newApnType);
            contentResolver.update(CONTENT_URI, values, ID + "=?", new String[]{apnInfo.id});
        }
        return true;
    }

    /**
     * Calculates current apn state and try to perfrom switching to another (on -> off, off->on) state.
     * Switch is always successfull if we are in "disabled" state (any APN has our prefix), but not always
     * available in enabled state.
     * <br/>
     * If in enabled state query result set is empty, then we stay in the same state. If result set is not empty,
     * then we switch to another state (off, with disabled APN's according to user preferences)
     *
     * @return new apn state ({@code true} if apn is now enabled, and {@code false} if apn is disabled).
     */
    boolean switchApnState() {
        boolean currentState = getApnState();
        if (switchApnState(currentState)) {
            return !currentState;
        } else {
            return currentState;
        }
    }

    /**
     * Performs switching apns work state according to passed state parameter
     *
     * @param enabled apn state. this method tries to make a switch to another state( enabled == true -> off, enabled == false -> on)
     * @return {@code true} if switch was successfull (apn state changed) and {@code false} if apn state was not changed
     */
    boolean switchApnState(boolean enabled) {
        if (enabled) {
            return disableAllInDb();
        } else {
            return enableAllInDb();
        }
    }

    /**
     * Performs switching apns with 'mms' type according to passed state parameter
     *
     * @param enabled apn state. this method tries to make a switch to another state( enabled == true -> off, enabled == false -> on)
     * @return {@code true} if switch was successfull (apn state changed) and {@code false} if apn state was not changed
     */
    boolean switchMmsState(boolean enabled){
        if (enabled){
            final List<ApnInfo> mmsList = selectEnabledMmsApns();
            return mmsList.size() != 0 && disableApnList(mmsList);
        }else{
            return enableApnList(selectDisabledMmsApns());
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

    int countAllCurrentApns() {
        return executeCountQuery("current is not null", null);
    }

    int countEnabledApns() {
        return executeCountQuery("apn not like ? and type not like ?", new String[]{DB_LIKE_SUFFIX, DB_LIKE_SUFFIX});
    }

    int countDisabledApns() {
        return executeCountQuery("apn like ? or type like ?", new String[]{DB_LIKE_SUFFIX, DB_LIKE_SUFFIX});
    }

    public int countMmsApns() {
        return executeCountQuery("type like ? or type like 'mms'", new String[]{"mms"+NameUtil.SUFFIX});
    }

    public int countDisabledMmsApns(){
        return executeCountQuery("type like ?", new String[]{"mms"+NameUtil.SUFFIX});
    }

    private int executeCountQuery(String whereQuery, String[] whereParams) {
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(CONTENT_URI, new String[]{"count(*)"}, whereQuery, whereParams, null);
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

    public List<ApnInfo> selectDisabledMmsApns(){
        return selectApnInfo("type like ?", new String[]{"mms"+NameUtil.SUFFIX});
    }

    public List<ApnInfo> selectEnabledMmsApns(){
        return selectApnInfo("type like ? and current is not null", new String[]{"mms"});
    }

    public boolean enableMmsApns(){
        List<ApnInfo> disabledList = selectDisabledMmsApns();
        enableApnList(disabledList);
        return true;
    }

    /**
     *
     * @return {@code true} if mms apns are enabled now and {@code false} otherwise
     */
    public boolean getMmsState(){
        return countMmsApns() > 0 && countDisabledMmsApns() > 0;
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
