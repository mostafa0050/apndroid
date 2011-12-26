package com.google.code.apndroid.apisample;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ToggleButton;

public class NewApiActivity extends Activity {

    private ToggleButton mToggle;
    private CheckBox mMmsCheckbBox;
    private BroadcastReceiver mReceiver;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.switch_screen);

        mToggle = (ToggleButton) findViewById(R.id.btn_switch);
        mMmsCheckbBox = (CheckBox) findViewById(R.id.btn_checkbox);

        // receiver that updates UI according to Apndroid switch state
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("apndroid.intent.action.STATUS")) {
                    boolean dataEnabled = intent.getBooleanExtra("apndroid.intent.extra.STATUS", true);
                    mToggle.setChecked(dataEnabled);
                }
            }
        };

        mToggle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent("apndroid.intent.action.CHANGE_STATUS");
                intent.putExtra("apndroid.intent.extra.STATUS", mToggle.isChecked());
                intent.putExtra("apndroid.intent.extra.KEEP_MMS_ON", mMmsCheckbBox.isChecked());
                startService(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(mReceiver, new IntentFilter("apndroid.intent.action.STATUS"));

        // request switch state
        startService(new Intent("apndroid.intent.action.GET_STATUS"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

}