package com.google.code.apndroid.dao;

import android.content.Context;

public class DaoUtil {

    public static DaoFactory getDaoFactory(Context context) {
        return ((DaoFactoryProvider) context.getApplicationContext()).getDaoFactory();
    }

    public static ConnectionDao getDao(Context context) {
        return getDaoFactory(context).getDao(context);
    }

}
