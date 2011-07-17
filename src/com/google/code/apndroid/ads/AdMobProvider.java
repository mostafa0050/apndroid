package com.google.code.apndroid.ads;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class AdMobProvider implements AdProvider {

    @Override
    public void addAd(Activity activity, ViewGroup adFrame) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        AdView adView = new AdView(activity, AdSize.BANNER, AdConstants.ADMOB_AD_UNIT_ID);
        adFrame.addView(adView, 0, layoutParams);
        adFrame.invalidate();

        adView.loadAd(new AdRequest());
    }

}
