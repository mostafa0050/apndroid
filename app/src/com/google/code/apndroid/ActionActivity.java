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

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

/**
 * @author Dmitry Pavlov <pavlov.dmitry.n@gmail.com>
 */
public class ActionActivity extends Activity {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        final Intent intent = getIntent();
        if (intent != null) {
            new ProcessRequestTask().execute(intent);
        } else {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }

    private class ProcessRequestTask extends AsyncTask<Intent, Void, Bundle> {

        @Override
        protected Bundle doInBackground(Intent... intents) {

            if (intents.length > 0) {
                Intent intent = intents[0];
                if (intent.getAction().equals(Constants.STATUS_REQUEST)) {
                    return ActionUtils.processStatusRequest(ActionActivity.this);
                } else if (intents[0].getAction().equals(Constants.CHANGE_STATUS_REQUEST)) {
                    return ActionUtils.processSwitchRequest(ActionActivity.this, intent.getExtras());
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bundle bundle) {
            if (bundle != null) {
                Intent response = new Intent(Constants.APN_DROID_RESULT);
                setResult(RESULT_OK, response.putExtras(bundle));
                finish();
            } else {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        }

    }

}
