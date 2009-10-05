package com.google.code.apndroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * @author Julien Muniak <julien.muniak@gmail.com>
 */
public class LocaleActivity extends Activity {
    private boolean mIsCancelled;
    private Spinner mStateSpinner;
    private CheckBox mNotificationCheckBox;


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

    private void setState(boolean state) {
        getStateSpinner().setSelection(state ? 0 : 1);
    }

    private void setNotification(boolean notify) {
        getNotificationCheckBox().setChecked(notify);
    }

    private boolean getState() {
        return getStateSpinner().getSelectedItemPosition() == 0;
    }

    private boolean getNotification() {
        return getNotificationCheckBox().isChecked();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.localemain);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.locale_ellipsizing_title_text);

        String breadcrumbString = getIntent().getStringExtra(com.twofortyfouram.Intent.EXTRA_STRING_BREADCRUMB);

        if (breadcrumbString == null) {
            breadcrumbString = getString(R.string.app_name);
        } else {
            breadcrumbString = String.format("%s%s%s", breadcrumbString, com.twofortyfouram.Intent.BREADCRUMB_SEPARATOR, getString(R.string.app_name));
        }
        ((TextView) findViewById(R.id.locale_ellipsizing_title_text)).setText(breadcrumbString);
        setTitle(breadcrumbString);

        if (savedInstanceState == null) {
            final boolean state = getIntent().getBooleanExtra(LocaleConstants.INTENT_EXTRA_STATE, true);
            final boolean showNotification = getIntent().getBooleanExtra(LocaleConstants.INTENT_EXTRA_SHOW_NOTIFICATION, false);

            setState(state);
            setNotification(showNotification);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.localemenu, menu);
        return true;
    }

    public void finish() {
        if (mIsCancelled)
            setResult(RESULT_CANCELED);
        else {
            final boolean state = getState();
            final Intent returnIntent = new Intent();
            returnIntent.putExtra(LocaleConstants.INTENT_EXTRA_STATE, state);
            returnIntent.putExtra(LocaleConstants.INTENT_EXTRA_SHOW_NOTIFICATION, getNotification());
            returnIntent.putExtra(com.twofortyfouram.Intent.EXTRA_STRING_BLURB, state ? getString(R.string.local_state_enabled) : getString(R.string.local_state_disabled));
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
