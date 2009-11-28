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
            if (intent.getAction().equals(ApplicationConstants.STATUS_REQUEST)){
                ApnDao dao = new ApnDao(this.getContentResolver());
                Intent response = new Intent(ApplicationConstants.APN_DROID_RESULT);
                boolean apnState = dao.getApnState();
                response.putExtra(ApplicationConstants.RESPONSE_APN_STATE, apnState);
                if (!apnState){
                    response.putExtra(ApplicationConstants.RESPONSE_MMS_STATE, dao.getMmsState());
                }
                setResult(RESULT_OK, response);
            }else if (intent.getAction().equals(ApplicationConstants.CHANGE_STATUS_REQUEST)){
                Bundle extras = intent.getExtras();
                boolean targetState = extras.getBoolean(ApplicationConstants.TARGET_STATE,true);
                boolean modifyMms = !extras.getBoolean(ApplicationConstants.KEEP_MMS,true);
                boolean showNotification = extras.getBoolean(ApplicationConstants.SHOW_NOTIFICATION,true);
                boolean success = SwitchingAndMessagingUtils.switсhIfNecessaryAndNotify(targetState, modifyMms,
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
