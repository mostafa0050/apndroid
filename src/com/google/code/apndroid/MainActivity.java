/*
 * This file is part of APNdroid.
 *
 * APNdroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * APNdroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with APNdroid. If not, see <http://www.gnu.org/licenses/>.
 */

package com.google.code.apndroid;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ToggleButton;
import com.google.code.apndroid.ads.AdProvider;
import com.google.code.apndroid.ads.AdProviderFactory;
import com.google.code.apndroid.dao.ConnectionDao;
import com.google.code.apndroid.dao.DaoUtil;
import com.google.code.apndroid.preferences.SettingsActivity;

/**
 * @author Martin Adamek <martin.adamek@gmail.com>
 */
public class MainActivity extends Activity {

    private static final int MENU_SETTINGS = 1;
    public static final int CHANGE_REQUEST = 1;

    private ConnectivityManager mConnectivityManager;
    private BroadcastReceiver mReceiver;
    private FrameLayout mAdFrame;
    private AdProvider mAdProvider;
    private ConnectionDao mDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // give up any internal focus before we switch layouts
        final View focused = getCurrentFocus();
        if (focused != null) {
            focused.clearFocus();
        }

        setContentView(R.layout.main);

        // http://crazygui.wordpress.com/2010/09/05/high-quality-radial-gradient-in-android/
//        getWindow().setFormat(PixelFormat.RGBA_8888);

        mDao = DaoUtil.getDaoFactory(getApplication()).getDao(this);
        mAdFrame = (FrameLayout) findViewById(R.id.ad_frame);
        mAdProvider = AdProviderFactory.getProvider();
        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onConnectivityEvent();
            }
        };

        // take care about data icon

        boolean isDataEnabled = mDao.isDataEnabled();

        // Set the initial state of the clock "checkbox"
        final ToggleButton dataOnOff = (ToggleButton) findViewById(R.id.btn_on_off);
        dataOnOff.setBackgroundResource(isDataEnabled ? R.drawable.btn_on : R.drawable.btn_off);
        dataOnOff.setChecked(isDataEnabled);

        // Clicking outside the "checkbox" should also change the state.

        dataOnOff.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                boolean enable = dataOnOff.isChecked();
                updateIndicatorAndData(enable, dataOnOff);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(mReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        if (mAdProvider == null) {
            // if this is PRO version
            mAdFrame.setVisibility(View.GONE);
        } else {
            // this is FREE version
            mAdFrame.setVisibility(View.VISIBLE);
            boolean connected = Utils.isConnected(mConnectivityManager, true);
            if (connected) {
                mAdProvider.addAd(this, mAdFrame);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        
        try {
            unregisterReceiver(mReceiver);
        } catch (IllegalArgumentException e) {
            // there is no way to know whether receiver was registered
            // so just ignore this
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_SETTINGS, 0, R.string.settings).setIcon(android.R.drawable.ic_menu_preferences);
        return true;
    }

    /* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_SETTINGS:
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        }
        return false;
    }

    private void updateIndicatorAndData(final boolean enable, ToggleButton toggleButton) {
        toggleButton.setBackgroundResource(enable ? R.drawable.btn_on: R.drawable.btn_off);
        toggleButton.setChecked(enable);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //todo it is better to use Utils method here because it contains extra logic for apn dao switcher
                mDao.setDataEnabled(MainActivity.this, enable);
                Utils.broadcastStatusChange(MainActivity.this, enable);
            }
        }).start();
    }

    private void onConnectivityEvent() {

        // we're not initialized yet
        if (mAdFrame == null) {
            return;
        }

        boolean connected = Utils.isConnected(mConnectivityManager, true);
        if (connected) {
            if (mAdFrame.getChildCount() > 0) {
                mAdFrame.removeViewAt(0);
            }

            AdProvider adProvider = AdProviderFactory.getProvider();
            if (adProvider != null) {
                adProvider.addAd(this, mAdFrame);
            }
        }
    }

}
