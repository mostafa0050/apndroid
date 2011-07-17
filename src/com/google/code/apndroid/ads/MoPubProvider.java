package com.google.code.apndroid.ads;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.mopub.mobileads.MoPubView;

public class MoPubProvider implements AdProvider {

    @Override
    public void addAd(final Activity activity, final ViewGroup adFrame) {
        MoPubView adView = new MoPubView(activity);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        adFrame.addView(adView, 0, layoutParams);
        adFrame.invalidate();

        adView.setAdUnitId(AdConstants.MOPUB_AD_UNIT_ID);

        adView.setOnAdFailedListener(new MoPubView.OnAdFailedListener() {
            @Override
            public void OnAdFailed(MoPubView m) {
                Log.d("APNdroid", "MoPub ad failed, switching to AdMob");

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adFrame.removeViewAt(0);
                        new AdMobProvider().addAd(activity, adFrame);
                        adFrame.invalidate();
                    }
                });
            }
        });

        adView.loadAd();
    }

}
