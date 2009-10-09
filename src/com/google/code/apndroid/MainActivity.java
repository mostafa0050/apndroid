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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SimpleAdapter;

/**
 * @author Martin Adamek <martin.adamek@gmail.com>
 */
public class MainActivity extends Activity implements View.OnClickListener {

    static final int NOTIFICATION_ID = 1;

    private SharedPreferences sharedPreferences;

    private SettingsPersister settingsPersister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initSettingsListener();
        findViewById(R.id.switch_button).setOnClickListener(this);
    }

    private void initSettingsListener() {
        sharedPreferences = getSharedPreferences(ApplicationConstants.AND_DROID_SETTINGS, Context.MODE_PRIVATE);
        
        SettingsPersister persister = settingsPersister = new SettingsPersister(sharedPreferences);

        CheckBox checkBox = (CheckBox) findViewById(R.id.internet_enabled_button);
        checkBox.setChecked(sharedPreferences.getBoolean(ApplicationConstants.AND_DROID_SETTINGS_INTERNET_ENABLED, true));
        checkBox.setOnClickListener(persister);

        checkBox = (CheckBox) findViewById(R.id.mms_enabled_button);
        checkBox.setChecked(sharedPreferences.getBoolean(ApplicationConstants.AND_DROID_SETTINGS_MMS_ENABLED, true));
        checkBox.setOnClickListener(persister);

        checkBox = (CheckBox) findViewById(R.id.show_notification_button);
        checkBox.setChecked(sharedPreferences.getBoolean(ApplicationConstants.APN_DROID_SHOW_NOTIFICATION, true));
        checkBox.setOnClickListener(persister);
    }

    public void onClick(View view) {
        settingsPersister.persist();
        sendBroadcast(new Intent(ApplicationConstants.APN_DROID_CHANGE_STATUS));
    }

    @Override
    protected void onStop() {
        super.onStop();
        settingsPersister.persist();
    }

    private static final class SettingsPersister implements View.OnClickListener {
        
        private SharedPreferences.Editor editor;

        private SettingsPersister(SharedPreferences sharedPreferences) {
            editor = sharedPreferences.edit();
        }

        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.internet_enabled_button:
                    editor.putBoolean(ApplicationConstants.AND_DROID_SETTINGS_INTERNET_ENABLED, ((CheckBox) view).isChecked());
                    break;
                case R.id.mms_enabled_button:
                    editor.putBoolean(ApplicationConstants.AND_DROID_SETTINGS_MMS_ENABLED, ((CheckBox) view).isChecked());
                    break;
                case R.id.show_notification_button:
                    editor.putBoolean(ApplicationConstants.AND_DROID_SETTINGS_SHOW_NOTIFICATION, ((CheckBox) view).isChecked());
                    break;
            }
        }

        public void persist(){
            editor.commit();
        }
    }

}
