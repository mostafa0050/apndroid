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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.code.apndroid.dao.ConnectionDao;
import com.google.code.apndroid.dao.DaoFactory;
import com.google.code.apndroid.internal.AdSpecFactory;
import com.google.code.apndroid.preferences.SettingsActivity;

/**
 * @author Martin Adamek <martin.adamek@gmail.com>
 */
public class MainActivity extends Activity {

    private static final int MENU_SETTINGS = 1;
    public static final int CHANGE_REQUEST = 1;
    
    private ConnectivityHandler mConnectivityHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // give up any internal focus before we switch layouts
        final View focused = getCurrentFocus();
        if (focused != null) {
            focused.clearFocus();
        }

        setContentView(R.layout.main);

        // take care about data icon

        View indicatorData = findViewById(R.id.indicator_data);
        boolean isConnectedOrConnecting = Utils.isConnectedOrConnecting(this,false);

        // Set the initial resource for the bar image.
        final ImageView barOnOff = (ImageView) indicatorData.findViewById(R.id.bar_data_onoff);
        barOnOff.setImageResource(isConnectedOrConnecting ? R.drawable.ic_indicator_on : R.drawable.ic_indicator_off);

        // Set the initial state of the clock "checkbox"
        final CheckBox dataOnOff = (CheckBox) indicatorData.findViewById(R.id.data_onoff);
        dataOnOff.setChecked(isConnectedOrConnecting);

        TextView infoText = (TextView) findViewById(R.id.info_text);
        TextView reconnectText = (TextView) findViewById(R.id.reconnect_text);
        mConnectivityHandler = new ConnectivityHandler(this, infoText, reconnectText, barOnOff);
        
        // Clicking outside the "checkbox" should also change the state.

        indicatorData.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                dataOnOff.toggle();
                boolean enable = dataOnOff.isChecked();
                updateIndicatorAndData(getBaseContext(), enable, barOnOff);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mConnectivityHandler.resume();
        
        refreshAll();
        
        // Set up GoogleAdView
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        AdSpecFactory.create(this, mainLayout);
    }

    @Override
    protected void onPause() {
        super.onPause();
        
        mConnectivityHandler.pause();
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

    static void updateIndicatorAndData(Context context, boolean enable, ImageView bar) {
        bar.setImageResource(enable ? R.drawable.ic_indicator_on : R.drawable.ic_indicator_off);
        //todo it is better to use Utils method here because it contains extra logic for apn dao switcher
        ConnectionDao connectionDao = DaoFactory.getDao(context);
        connectionDao.setDataEnabled(enable);
        Utils.broadcastStatusChange(context, enable);
    }

    private void refreshAll() {
    	mConnectivityHandler.refresh();
    }

}
