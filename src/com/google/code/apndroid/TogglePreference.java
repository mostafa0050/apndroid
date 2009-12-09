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
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ToggleButton;

public class TogglePreference extends Preference implements View.OnClickListener {
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
        ToggleButton button = (ToggleButton) view.findViewById(R.id.toggle_button);
        button.setOnClickListener(this);
        if (enabled != null) {
            button.setChecked(enabled);
        }
    }

    public void onClick(View view) {
        ToggleButton button = (ToggleButton) view;
        boolean enabled = SwitchingAndMessagingUtils.switchAndNotify(view.getContext()) == ApplicationConstants.State.ON;
        button.setChecked(enabled);
        setToggleButtonChecked(enabled);
        notifyChanged();
    }

    public void setToggleButtonChecked(boolean enabled) {
        this.enabled = enabled;
        persistBoolean(enabled);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getBoolean(index, true);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        if (restoreValue) {
            enabled = getPersistedBoolean(enabled != null ? enabled : true);
        } else {
            // Set state
            Boolean value = (Boolean) defaultValue;
            enabled = value;
            persistBoolean(value);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        /*
         * Suppose a client uses this preference type without persisting. We
         * must save the instance state so it is able to, for example, survive
         * orientation changes.
         */

        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            // No need to save instance state since it's persistent
            return superState;
        }

        // Save the instance state
        final SavedState myState = new SavedState(superState);
        myState.enabled = enabled;
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!state.getClass().equals(SavedState.class)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }

        // Restore the instance state
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        enabled = myState.enabled;
        notifyChanged();
    }

    public void fireStateChanged() {
        this.notifyChanged();
    }

    /**
     * SavedState, a subclass of {@link BaseSavedState}, will store the state
     * of MyPreference, a subclass of Preference.
     * <p/>
     * It is important to always call through to super methods.
     */
    private static final class SavedState extends BaseSavedState {
        boolean enabled;

        public SavedState(Parcel source) {
            super(source);

            // Restore the click counter
            enabled = source.readInt() == 1;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);

            // Save the click counter
            dest.writeInt(enabled ? 1 : 0);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

    }

}
