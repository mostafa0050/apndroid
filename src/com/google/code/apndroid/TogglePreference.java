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

import android.content.Context;
import android.preference.Preference;
import android.view.View;
import android.util.AttributeSet;
import android.widget.ToggleButton;

public class TogglePreference extends Preference {
    private ToggleButton button;
    private View.OnClickListener listener;
    private Boolean enabled;

    public TogglePreference(Context context) {
        super(context);
        loadLayout();
    }

    public TogglePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        loadLayout();
    }

    private void loadLayout() {
        setLayoutResource(R.layout.toggle_preference);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        button = (ToggleButton) view.findViewById(R.id.toggle_button);
        if (listener != null){
            button.setOnClickListener(listener);
        }
        if (enabled != null){
            button.setChecked(true);
        }
    }

    public void setOnClickListener(View.OnClickListener listener){        
        if (button != null){
            button.setOnClickListener(listener);
        }else{
            this.listener = listener;
        }
    }

    public void setToggleButtonChecked(boolean enabled) {
        if (button != null){
            button.setChecked(enabled);
        }else{
            this.enabled = enabled;
        }
    }
}
