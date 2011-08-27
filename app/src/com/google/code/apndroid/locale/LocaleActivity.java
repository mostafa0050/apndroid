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

package com.google.code.apndroid.locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.code.apndroid.Constants;
import com.google.code.apndroid.R;
import com.twofortyfouram.locale.BreadCrumber;

/**
 * Activity for setting up locale plugin settings. It's called by locale application for setting up
 * work parameters.
 *
 * @author Julien Muniak <julien.muniak@gmail.com>
 * @author Dmitry Pavlov <pavlov.dmitry.n@gmail.com>
 */
public class LocaleActivity extends Activity {

    private boolean mIsCancelled;
    private Spinner mStateSpinner;
    private CheckBox mNotificationCheckBox;
    private CheckBox mMmsCheckbox;

    private Spinner getStateSpinner() {
        if (mStateSpinner == null) {
            mStateSpinner = ((Spinner) findViewById(R.id.statespinner));
        }
        return mStateSpinner;
    }

    private CheckBox getNotificationCheckBox() {
        if (mNotificationCheckBox == null) {
            mNotificationCheckBox = ((CheckBox) findViewById(R.id.notification));
        }
        return mNotificationCheckBox;
    }

    private CheckBox getMmsCheckBox() {
        if (mMmsCheckbox == null) {
            mMmsCheckbox = (CheckBox) findViewById(R.id.mms);
        }
        return mMmsCheckbox;
    }

    private void setState(boolean state) {
        getStateSpinner().setSelection(state ? 0 : 1);
    }

    private void setNotification(boolean notify) {
        getNotificationCheckBox().setChecked(notify);
    }

    private int getTargetState() {
        return getStateSpinner().getSelectedItemPosition() == 0 ? Constants.STATE_ON : Constants.STATE_OFF;
    }

    private boolean getNotification() {
        return getNotificationCheckBox().isChecked();
    }

    private int getTargetMmsState() {
        return getMmsCheckBox().isChecked() ? Constants.STATE_ON : Constants.STATE_OFF;
    }

    private void setKeepMms(boolean keep) {
        getMmsCheckBox().setChecked(keep);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();

        performSerializeProtectionChecks(intent);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.localemain);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.locale_ellipsizing_title_text);

        CharSequence breadcrumbString = BreadCrumber.generateBreadcrumb(
                getApplicationContext(),
                intent,
                getString(R.string.app_name)
        );

        ((TextView) findViewById(R.id.locale_ellipsizing_title_text)).setText(breadcrumbString);
        setTitle(breadcrumbString);

        if (savedInstanceState == null) {
            Bundle localeBundle = intent.getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
            if (localeBundle != null){
                final int state = localeBundle.getInt(Constants.TARGET_APN_STATE, Constants.STATE_ON);
                final int mmsTarget = localeBundle.getInt(Constants.TARGET_MMS_STATE, Constants.STATE_ON);
                final boolean showNotification = localeBundle.getBoolean(Constants.SHOW_NOTIFICATION, false);

                setState(state == Constants.STATE_ON);
                setKeepMms(mmsTarget == Constants.STATE_ON);
                setNotification(showNotification);
            }
        }

    }

    private void performSerializeProtectionChecks(Intent intent) {
        if (LocaleSerializeProtectionUtil.checkForCustomSerializableAttack(intent)){
            intent.replaceExtras((Bundle) null);
        }
        if (LocaleSerializeProtectionUtil.checkForCustomSerializableAttackInExtraBundle(intent)){
            intent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, (Bundle) null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.localemenu, menu);
        return true;
    }

    @Override
    public void finish() {
        if (mIsCancelled)
            setResult(RESULT_CANCELED);
        else {
            final int targetState = getTargetState();
            final Intent returnIntent = new Intent();

            Bundle extraBundle = new Bundle();
            extraBundle.putInt(Constants.TARGET_APN_STATE, targetState);
            extraBundle.putInt(Constants.TARGET_MMS_STATE, getTargetMmsState());
            extraBundle.putBoolean(Constants.SHOW_NOTIFICATION, getNotification());

            returnIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, extraBundle);
            returnIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB,
                    targetState == Constants.STATE_ON
                            ? getString(R.string.local_state_enabled)
                            : getString(R.string.local_state_disabled));

            setResult(RESULT_OK, returnIntent);
        }
        super.finish();
    }

    @Override
    public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_dontsave: {
                mIsCancelled = true;
                finish();
                return true;
            }
            case R.id.menu_save: {
                finish();
                return true;
            }
        }
        return super.onMenuItemSelected(featureId, item);
    }

}
