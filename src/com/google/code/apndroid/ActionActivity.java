package com.google.code.apndroid;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;

/**
 * User: Zelgadis
 * Date: 26.11.2009
 */
public class ActionActivity extends Activity{

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        if (intent != null){
            int onState = ApplicationConstants.State.ON;
            if (intent.getAction().equals(ApplicationConstants.STATUS_REQUEST)){
                ApnDao dao = new ApnDao(this.getContentResolver());
                Intent response = new Intent(ApplicationConstants.APN_DROID_RESULT);
                int apnState = dao.getApnState();
                response.putExtra(ApplicationConstants.RESPONSE_APN_STATE, apnState);
                if (apnState != onState){
                    response.putExtra(ApplicationConstants.RESPONSE_MMS_STATE, dao.getMmsState());
                }
                setResult(RESULT_OK, response);
            }else if (intent.getAction().equals(ApplicationConstants.CHANGE_STATUS_REQUEST)){
                Bundle extras = intent.getExtras();
                int targetState = extras.getInt(ApplicationConstants.TARGET_APN_STATE,onState);
                int mmsTarget = extras.getInt(ApplicationConstants.TARGET_MMS_STATE,onState);
                boolean showNotification = extras.getBoolean(ApplicationConstants.SHOW_NOTIFICATION,true);
                boolean success = SwitchingAndMessagingUtils.switсhIfNecessaryAndNotify(targetState, mmsTarget,
                        showNotification, this, new ApnDao(this.getContentResolver()));
                Intent response = new Intent(ApplicationConstants.APN_DROID_RESULT);
                response.putExtra(ApplicationConstants.RESPONSE_SWITCH_SUCCESS, success);
                setResult(RESULT_OK, response);
            }else{
                setResult(Activity.RESULT_CANCELED);
            }
        }else{
            setResult(Activity.RESULT_CANCELED);
        }
        finish();
    }
}
