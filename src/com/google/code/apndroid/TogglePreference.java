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
