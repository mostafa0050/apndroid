package com.google.code.apndroid.ads;

import android.content.Context;

public class AdUtil {

    public static AdProvider getProvider(Context context) {
        if (context.getApplicationContext() instanceof AdProviderFactory) {
            return ((AdProviderFactory) context.getApplicationContext()).getProvider();
        }
        return null;
    }

}
