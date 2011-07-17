package com.google.code.apndroid.ads;

import android.app.Activity;
import android.widget.RelativeLayout;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class AdMobProvider implements AdProvider {

    @Override
    public void show(Activity activity, RelativeLayout relativeLayout) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);

        AdView adView = new AdView(activity, AdSize.BANNER, AdConstants.ADMOB_AD_UNIT_ID);
        relativeLayout.addView(adView, layoutParams);
        relativeLayout.invalidate();

        adView.loadAd(new AdRequest());
    }

}
