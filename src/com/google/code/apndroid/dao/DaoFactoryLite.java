package com.google.code.apndroid.dao;

import android.content.Context;
import com.google.code.apndroid.preferences.Prefs;

/**
 * @author Dmitry Pavlov <pavlov.dmitry.n@gmail.com>
 * @since 22.06.2010
 */
public final class DaoFactoryLite implements DaoFactory {

    @Override
    public ConnectionDao getDao(Context context) {
        Prefs prefs = new Prefs(context);
        boolean disableAll = prefs.isDisableAll();
        ApnDao apnDao = new ApnDao(context.getContentResolver());
        apnDao.setDisableAllApns(disableAll);
        return apnDao;
    }

}
