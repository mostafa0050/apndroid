package com.google.code.apndroid.ads;

public class AdProviderFactory {

    private static AdProvider sProvider;
    private static BannerlessAdProvider sBannerlessAdProvider;

    public static AdProvider getProvider() {
        return sProvider;
    }

    public static void setProvider(AdProvider provider) {
        sProvider = provider;
    }

    public static void setBannerlessProvider(BannerlessAdProvider provider) {
        sBannerlessAdProvider = provider;
        sBannerlessAdProvider.addAd();
    }

}
