package com.google.code.apndroid;

import android.app.Application;
import com.google.code.apndroid.ads.AdProvider;
import com.google.code.apndroid.ads.AdProviderFactory;
import com.google.code.apndroid.ads.MoPubProvider;
import com.google.code.apndroid.dao.DaoFactory;
import com.google.code.apndroid.dao.DaoFactoryLite;
import com.google.code.apndroid.dao.DaoFactoryProvider;

public class ApndroidLiteApplication extends Application implements DaoFactoryProvider, AdProviderFactory {

    @Override
    public DaoFactory getDaoFactory() {
        return new DaoFactoryLite();
    }

    @Override
    public AdProvider getProvider() {
        return new MoPubProvider();
    }

}
