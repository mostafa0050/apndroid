package com.google.code.apndroid;

import android.app.Application;
import com.google.code.apndroid.ads.AdProviderFactory;
import com.google.code.apndroid.ads.MoPubProvider;
import com.google.code.apndroid.dao.DaoFactory;
import com.google.code.apndroid.dao.DaoFactoryLite;
import com.google.code.apndroid.dao.DaoFactoryProvider;

public class ApndroidLiteApplication extends Application implements DaoFactoryProvider {

    @Override
    public void onCreate() {
        super.onCreate();

        AdProviderFactory.setProvider(new MoPubProvider());
    }

    @Override
    public DaoFactory getDaoFactory() {
        return new DaoFactoryLite();
    }

}
