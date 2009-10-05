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
import android.os.Bundle;

/**
 * @author Martin Adamek <martin.adamek@gmail.com>
 */
public class MainActivity extends Activity {

    static final int NOTIFICATION_ID = 1;

    private boolean mIsNetEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Fire off a thread to do some work that we shouldn't do directly in the UI thread
        setContentView(R.layout.main);
//        Thread t = new Thread() {
//            public void run() {
//                mIsNetEnabled = DbUtil.getApnState(getContentResolver());
//                DbUtil.switchApnState(getContentResolver(), mIsNetEnabled);
//                MessagingUtils.sendStatusMessage(MainActivity.this, !mIsNetEnabled, true /* temporary a constant value */);//we switched apns state so we should send negation of isNetEnabled var
//                MainActivity.this.finish();
//            }
//        };
//        t.start();
    }

}
