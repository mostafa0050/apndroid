package com.google.code.apndroid.dao;

import android.content.Context;

public interface DaoFactory {

    ConnectionDao getDao(Context context);

    ApnInformationDao getInformationDao(Context context);

}
