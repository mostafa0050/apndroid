package com.google.code.apndroid.ads;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;

public interface AdProvider {

    void addAd(Activity activity, ViewGroup adFrame);

}
