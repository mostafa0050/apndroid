package com.google.code.apndroid.internal;

import android.content.Context;
import com.google.code.apndroid.dao.ConnectionDao;

/**
 * Class stub
 */
public final class InternalDao implements ConnectionDao {

    private final Context mContext;

    public InternalDao(Context context) {
        this.mContext = context;
    }

    public boolean isDataEnabled() {
        return true;
    }

    public boolean isMmsEnabled() {
        return true;
    }

    public boolean setDataEnabled(boolean enable) {
        return setDataEnabled(enable, enable);
    }

    public boolean setDataEnabled(boolean enableData, boolean enableMms) {
        return false;
    }

    public boolean setMmsEnabled(boolean enable) {
        return false;
    }

}
