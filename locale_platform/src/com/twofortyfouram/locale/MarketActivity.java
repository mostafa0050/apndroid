// Copyright 2010 two forty four a.m. LLC <http://www.twofortyfouram.com>
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at <http://www.apache.org/licenses/LICENSE-2.0>
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.twofortyfouram.locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.twofortyfouram.locale.platform.R;

/**
 * Provides a simple dialog UI for the user to understand what a plug-in is when tapping on the "open" button from the Android
 * Market.
 */
public final class MarketActivity extends Activity
{
	/*
	 * The desired UI is to have a dialog that behaves like a normal Android dialog. Using an Activity with the dialog theme
	 * doesn't look right, so this Activity has no UI for itself but hosts a standard Android dialog.
	 */

	/**
	 * Class name cached for logging purposes.
	 */
	private static final String LOGGING_CLASS_NAME = MarketActivity.class.getSimpleName();

	/**
	 * URI to Locale's page on the Android Market
	 */
	private static final String MARKET_URI = String.format("market://details?id=%s", Constants.LOCALE_PACKAGE); //$NON-NLS-1$

	/**
	 * URI to the two forty four a.m. website store
	 */
	private static final String TFF_STORE_URI = "http://www.twofortyfouram.com/store.html"; //$NON-NLS-1$

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setTheme(android.R.style.Theme_Translucent_NoTitleBar);

		/*
		 * hack: the ContentView must have a layout for a dialog to be displayed. Then we set the layout to be completely hidden
		 * to achieve the desired effect.
		 */
		final LinearLayout layout = new LinearLayout(getApplicationContext());
		layout.setVisibility(View.GONE);
		setContentView(layout);

		showDialog(0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Dialog onCreateDialog(final int id)
	{
		/*
		 * set up the custom layout with the Alert and Informative messages
		 */
		final LayoutInflater factory = LayoutInflater.from(this);
		final View messageView = factory.inflate(R.layout.twofortyfouram_locale_dialog, null);
		final TextView primaryMessageView = (TextView) messageView.findViewById(R.id.twofortyfouram_locale_dialog_primary_message);
		final TextView secondaryMessageView = (TextView) messageView.findViewById(R.id.twofortyfouram_locale_dialog_secondary_message);

		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(messageView);
		builder.setTitle(R.string.twofortyfouram_locale_marketactivity_dialog_title);

		final PackageManager manager = getPackageManager();

		final String compatiblePackage = PackageUtilities.getCompatiblePackage(manager, null);

		if (compatiblePackage != null)
		{
			// after this point, assume Locale-compatible package is installed
			Log.i(Constants.LOG_TAG, String.format("%s.onCreateDialog(): Locale-compatible package %s is installed", LOGGING_CLASS_NAME, compatiblePackage)); //$NON-NLS-1$

			final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
			{
				@SuppressWarnings("synthetic-access")
				public void onClick(final DialogInterface dialog, final int which)
				{
					try
					{
						final Intent i = manager.getLaunchIntentForPackage(compatiblePackage);
						i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(i);
					}
					catch (final Exception e)
					{
						/*
						 * Under normal circumstances, this shouldn't happen. Potential causes would be a TOCTOU error where the
						 * application is uninstalled. Or possibly due to the application enforcing permissions that it shouldn't
						 * be.
						 */
						Log.e(Constants.LOG_TAG, String.format("%s.onCreateDialog(): %s launch Activity not found", LOGGING_CLASS_NAME, compatiblePackage), e); //$NON-NLS-1$
						Toast.makeText(getApplicationContext(), R.string.twofortyfouram_locale_application_not_available, Toast.LENGTH_LONG).show();
					}

					finish();
				}
			};

			/*
			 * Determine whether this is a condition, setting, or both. Note that this test is not as thorough as Locale will be
			 * in deciding whether a Condition/Setting is valid.
			 */
			boolean isCondition = false;
			for (ResolveInfo x : getPackageManager().queryIntentActivities(new Intent(com.twofortyfouram.locale.Intent.ACTION_EDIT_CONDITION), PackageManager.GET_ACTIVITIES))
			{
				ActivityInfo activityInfo = x.activityInfo;
				if (getPackageName().equals(activityInfo.packageName))
				{
					isCondition = true;
					break;
				}
			}

			boolean isSetting = false;
			for (ResolveInfo x : getPackageManager().queryIntentActivities(new Intent(com.twofortyfouram.locale.Intent.ACTION_EDIT_SETTING), PackageManager.GET_ACTIVITIES))
			{
				ActivityInfo activityInfo = x.activityInfo;
				if (getPackageName().equals(activityInfo.packageName))
				{
					isSetting = true;
					break;
				}
			}

			primaryMessageView.setText(SharedResources.getTextResource(manager, null, SharedResources.STRING_PLUGIN_MESSAGE));

			if (isCondition && isSetting)
			{
				secondaryMessageView.setText(SharedResources.getTextResource(manager, null, SharedResources.STRING_PLUGIN_INFORMATIVE_CONDITION_AND_SETTING));
			}
			else if (isCondition)
			{
				secondaryMessageView.setText(SharedResources.getTextResource(manager, null, SharedResources.STRING_PLUGIN_INFORMATIVE_CONDITION));
			}
			else if (isSetting)
			{
				secondaryMessageView.setText(SharedResources.getTextResource(manager, null, SharedResources.STRING_PLUGIN_INFORMATIVE_SETTING));
			}
			builder.setPositiveButton(SharedResources.getTextResource(manager, null, SharedResources.STRING_PLUGIN_OPEN), listener);
		}
		else
		{
			Log.i(Constants.LOG_TAG, String.format("%s.onCreateDialog(): Locale-compatible package is not installed", LOGGING_CLASS_NAME)); //$NON-NLS-1$

			primaryMessageView.setText(R.string.twofortyfouram_locale_marketactivity_dialog_primary_message);
			secondaryMessageView.setText(R.string.twofortyfouram_locale_marketactivity_dialog_secondary_message);

			final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
			{
				@SuppressWarnings("synthetic-access")
				public void onClick(final DialogInterface dialog, final int which)
				{
					try
					{
						final Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(MARKET_URI)).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(i);
					}
					catch (final ActivityNotFoundException e)
					{
						Log.i(Constants.LOG_TAG, String.format("%s.onClick(): Android Market not found", LOGGING_CLASS_NAME), e); //$NON-NLS-1$

						/*
						 * No need for this to have a try-catch; Android requires a web browser
						 */
						startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(TFF_STORE_URI)).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
					}

					finish();
				}
			};

			builder.setPositiveButton(R.string.twofortyfouram_locale_marketactivity_dialog_button_install, listener);
		}

		builder.setNegativeButton(android.R.string.cancel, null);

		/*
		 * if the dialog isn't showing, then neither should the Activity
		 */
		final Dialog dialog = builder.create();

		dialog.setOnDismissListener(new OnDismissListener()
		{
			public void onDismiss(final DialogInterface arg0)
			{
				finish();
			}
		});

		return dialog;
	}
}