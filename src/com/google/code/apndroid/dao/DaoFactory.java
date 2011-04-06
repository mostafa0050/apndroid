package com.google.code.apndroid.dao;

import android.content.Context;

import com.google.code.apndroid.internal.InternalDao;
import com.google.code.apndroid.preferences.Prefs;

/**
 * @author Dmitry Pavlov <pavlov.dmitry.n@gmail.com>
 * @since 22.06.2010
 */
public final class DaoFactory {

    public static ConnectionDao getDao(Context context) {
        return getDao(context, new Prefs(context).isNativeToggle());
    }

    public static ConnectionDao getDao(Context context, boolean nativeToggle) {
        if (nativeToggle) {
            return new InternalDao(context);
        } else {
            Prefs prefs = new Prefs(context);
            boolean disableAll = prefs.isDisableAll();
            ApnDao apnDao = new ApnDao(context.getContentResolver());
            apnDao.setDisableAllApns(disableAll);
            return apnDao;
        }
    }

}
