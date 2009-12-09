package com.example.apndroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import com.google.code.apndroid.ApplicationConstants;

/**
 * User: Zelgadis
 * Date: 27.11.2009
 */
public class MyActivity extends Activity {

    public static final int STATE_REQUEST = 0;
    public static final int CHANGE_REQUEST = 1;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button stateRequest = (Button) findViewById(R.id.state_request);
        stateRequest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(ApplicationConstants.STATUS_REQUEST);
                MyActivity.this.startActivityForResult(intent, STATE_REQUEST);
            }
        });
        Button changeRequet = (Button) findViewById(R.id.switch_request);
        changeRequet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                boolean targetState = getCheckBoxState(R.id.target_state);
                boolean keepMms = getCheckBoxState(R.id.keep_mms);
                boolean showNotification = getCheckBoxState(R.id.show_notification);
                Intent intent = new Intent(ApplicationConstants.CHANGE_STATUS_REQUEST);
                int onState = ApplicationConstants.State.ON;
                int offState = ApplicationConstants.State.OFF;
                intent.putExtra(ApplicationConstants.TARGET_MMS_STATE, keepMms ? onState : offState);
                intent.putExtra(ApplicationConstants.TARGET_APN_STATE, targetState ? onState : offState);
                intent.putExtra(ApplicationConstants.SHOW_NOTIFICATION, showNotification);
                MyActivity.this.startActivityForResult(intent, CHANGE_REQUEST);
            }
        });
    }

    private boolean getCheckBoxState(int id) {
        CheckBox cb = (CheckBox) findViewById(id);
        return cb.isChecked();
    }

    @Override
    protected void onActivityResult(int requestedCode, int resultCode, Intent intent) {
        super.onActivityResult(requestedCode, resultCode, intent);
        switch (requestedCode) {
            case STATE_REQUEST:
                if (resultCode == RESULT_OK && intent != null) {
                    if (ApplicationConstants.APN_DROID_RESULT.equals(intent.getAction())) {
                        int onState = ApplicationConstants.State.ON;
                        boolean state = intent.getIntExtra(ApplicationConstants.RESPONSE_APN_STATE, onState) == onState;
                        String currentState = "Current state is " + (state ? "on" : "off");
                        if (!state) {
                            currentState += "\nMms state is " +
                                    (intent.getIntExtra(ApplicationConstants.RESPONSE_MMS_STATE, onState) == onState
                                            ? "on"
                                            : "off");
                        }
                        Toast toast = Toast.makeText(this, currentState, Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
                break;
            case CHANGE_REQUEST:
                if (resultCode == RESULT_OK && intent != null) {
                    if (ApplicationConstants.APN_DROID_RESULT.equals(intent.getAction())) {
                        String switchSuccess = "Switch was " + (intent.getBooleanExtra(ApplicationConstants.RESPONSE_SWITCH_SUCCESS, true) ? "successful" : "unsuccessful");
                        Toast toast = Toast.makeText(this, switchSuccess, Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
                break;
        }


    }
}
