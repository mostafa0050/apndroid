package com.google.code.apndroid.apisample;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ToggleButton;

public class OldApiActivity extends Activity {

    private static final int GET_STATE_REQUEST = 1;
    private static final int CHANGE_STATE_REQUEST = 2;

    private ToggleButton mToggle;
    private CheckBox mMmsCheckbBox;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.switch_screen);

        mToggle = (ToggleButton) findViewById(R.id.btn_switch);
        mMmsCheckbBox = (CheckBox) findViewById(R.id.btn_checkbox);

        mToggle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent("com.google.code.apndroid.intent.action.CHANGE_REQUEST");
                intent.putExtra("com.google.code.apndroid.intent.extra.TARGET_STATE", mToggle.isChecked());
                intent.putExtra("com.google.code.apndroid.intent.extra.TARGET_MMS_STATE", mMmsCheckbBox.isChecked());
                startActivityForResult(intent, CHANGE_STATE_REQUEST);
            }
        });

        // request switch state
        startActivityForResult(new Intent("com.google.code.apndroid.intent.action.STATUS_REQUEST"), GET_STATE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == GET_STATE_REQUEST && resultCode == RESULT_OK && intent != null) {
            if (intent.getAction().equals("com.google.code.apndroid.intent.REQUEST_RESULT")) {
                boolean dataEnabled = (intent.getIntExtra("APN_STATE", 1) == 1);
                mToggle.setChecked(dataEnabled);
            }
        }

    }
}