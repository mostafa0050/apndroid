package com.google.code.apndroid.ads;

import android.app.Activity;
import android.util.Log;
import android.widget.RelativeLayout;
import com.mopub.mobileads.MoPubView;

public class MoPubProvider implements AdProvider {

    @Override
    public void show(final Activity activity, final RelativeLayout relativeLayout) {
        final MoPubView adView = new MoPubView(activity);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);

        relativeLayout.addView(adView, layoutParams);
//        relativeLayout.invalidate();

        adView.setAdUnitId(AdConstants.MOPUB_AD_UNIT_ID);
        adView.setOnAdFailedListener(new MoPubView.OnAdFailedListener() {
            @Override
            public void OnAdFailed(MoPubView m) {
                Log.d("APNdroid", "MoPub ad failed, switching to AdMob");

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        relativeLayout.removeView(adView);
                        new AdMobProvider().show(activity, relativeLayout);
//                        relativeLayout.invalidate();
                    }
                });
            }
        });

        adView.loadAd();
    }

}
