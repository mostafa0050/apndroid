package com.example.apndroid;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.widget.Button;
import android.widget.Toast;
import android.widget.CheckBox;
import android.view.View;
import com.google.code.apndroid.ApplicationConstants;
import com.google.code.apndroid.api.example.R;

import java.util.List;

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
                intent.putExtra(ApplicationConstants.KEEP_MMS, keepMms);
                intent.putExtra(ApplicationConstants.TARGET_STATE, targetState);
                intent.putExtra(ApplicationConstants.SHOW_NOTIFICATION, showNotification);
                MyActivity.this.startActivityForResult(intent, CHANGE_REQUEST);
            }
        });
    }

    private boolean getCheckBoxState(int id){
        CheckBox cb =(CheckBox) findViewById(id);
        return cb.isChecked();
    }

    @Override
    protected void onActivityResult(int requestedCode, int resultCode, Intent intent) {
        super.onActivityResult(requestedCode, resultCode, intent);
        switch (requestedCode){
            case STATE_REQUEST:
                if (resultCode == RESULT_OK && intent != null) {
                    if (ApplicationConstants.APN_DROID_RESULT.equals(intent.getAction())) {
                        String currentState = "Current state is " + (intent.getBooleanExtra(ApplicationConstants.RESPONSE_APN_STATE, true) ? "on" : "off");
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
