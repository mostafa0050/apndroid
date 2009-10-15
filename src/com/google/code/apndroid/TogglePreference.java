package com.google.code.apndroid;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ToggleButton;

public class TogglePreference extends Preference implements View.OnClickListener{
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
        boolean enabled = SwitchingAndMessagingUtils.switchAndNotify(view.getContext());
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
        // This preference type's value type is Integer, so we read the default
        // value from the attributes as an Integer.
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

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

}
