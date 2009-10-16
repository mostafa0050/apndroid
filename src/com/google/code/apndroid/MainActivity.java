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

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * @author Martin Adamek <martin.adamek@gmail.com>
 */
public class MainActivity extends PreferenceActivity  {

    static final int NOTIFICATION_ID = 1;
    private TogglePreference togglePreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        togglePreference = (TogglePreference) getPreferenceManager().findPreference(ApplicationConstants.SETTINGS_TOGGLE_BUTTON);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!PreferenceManager.getDefaultSharedPreferences(this).contains(ApplicationConstants.SETTINGS_TOGGLE_BUTTON)){
            ApnDao apnDao = new ApnDao(this.getContentResolver());
            boolean isEnabled = apnDao.getApnState();
            togglePreference.setToggleButtonChecked(isEnabled);            
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
