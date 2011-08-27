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

import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

/**
 * A helper for dynamically loading resources from the <i>Locale</i> Developer Platform host.
 * <p>
 * Localized strings and resources will be returned if they are available within the host.
 */
public final class SharedResources
{
	/**
	 * Class named cached for logging purposes.
	 */
	private static final String LOGGING_CLASS_NAME = SharedResources.class.getSimpleName();

	/**
	 * {@code String} name of the resource for the primary message in the {@link MarketActivity}.
	 */
	/*
	 * This is NOT part of the public Locale Developer Platform API
	 */
	protected static final String STRING_PLUGIN_MESSAGE = "plugin_dialog_message"; //$NON-NLS-1$

	/**
	 * {@code String} name of the resource for the informative message in the {@link MarketActivity}.
	 */
	/*
	 * This is NOT part of the public Locale Developer Platform API
	 */
	protected static final String STRING_PLUGIN_INFORMATIVE_SETTING = "plugin_dialog_informative_setting"; //$NON-NLS-1$

	/**
	 * {@code String} name of the resource for the informative message in the {@link MarketActivity}.
	 */
	/*
	 * This is NOT part of the public Locale Developer Platform API
	 */
	protected static final String STRING_PLUGIN_INFORMATIVE_CONDITION = "plugin_dialog_informative_condition"; //$NON-NLS-1$

	/**
	 * {@code String} name of the resource for the informative message in the {@link MarketActivity}.
	 */
	/*
	 * This is NOT part of the public Locale Developer Platform API
	 */
	protected static final String STRING_PLUGIN_INFORMATIVE_CONDITION_AND_SETTING = "plugin_dialog_informative_condition_and_setting"; //$NON-NLS-1$

	/**
	 * {@code String} name of the resource the open button in the {@link MarketActivity}.
	 */
	/*
	 * This is NOT part of the public Locale Developer Platform API
	 */
	protected static final String STRING_PLUGIN_OPEN = "plugin_open"; //$NON-NLS-1$

	/**
	 * {@code String} name of the resource for the menu string "Save".
	 */
	public static final String STRING_MENU_SAVE = "save"; //$NON-NLS-1$

	/**
	 * {@code String} name of the resource for the menu string "Don't Save".
	 */
	public static final String STRING_MENU_DONTSAVE = "dontsave"; //$NON-NLS-1$

	/**
	 * {@code String} name of the resource for the menu string "Help".
	 */
	public static final String STRING_MENU_HELP = "help"; //$NON-NLS-1$

	/**
	 * {@code String} name of the resource for a menu icon for "Save".
	 */
	public static final String DRAWABLE_MENU_SAVE = "icon_save"; //$NON-NLS-1$

	/**
	 * {@code String} name of the resource for a menu icon for "Don't Save".
	 */
	public static final String DRAWABLE_MENU_DONTSAVE = "icon_dontsave"; //$NON-NLS-1$

	/**
	 * {@code String} name of the resource for a menu icon for "Help".
	 */
	public static final String DRAWABLE_MENU_HELP = "icon_help"; //$NON-NLS-1$

	/**
	 * {@code String} name of the frame put around UI elements
	 */
	public static final String DRAWABLE_LOCALE_BORDER = "locale_border"; //$NON-NLS-1$

	/**
	 * Helper method to load resources from the <i>Locale</i> Developer Platform host.
	 *
	 * @param packageManager an instance of {@code PackageManager}. Cannot be null.
	 * @param callingPackageHint hint as to which package is the calling package, from which resources might be preferred. This
	 *            parameter may be null.
	 * @param resourceName the {@code String} name of the resource to load. This must be one of the strings defined as a static
	 *            constant in this class. Cannot be null or empty.
	 * @return the resource requested.
	 */
	public static CharSequence getTextResource(final PackageManager packageManager, final String callingPackageHint, final String resourceName)
	{
		if (packageManager == null)
		{
			throw new IllegalArgumentException(String.format("%s.getTextResource(): packageManager param was null", LOGGING_CLASS_NAME)); //$NON-NLS-1$
		}

		if (TextUtils.isEmpty(resourceName))
		{
			throw new IllegalArgumentException(String.format("%s.getTextResource(): resourceName param was null or empty", LOGGING_CLASS_NAME)); //$NON-NLS-1$
		}

		final String compatiblePackage = PackageUtilities.getCompatiblePackage(packageManager, callingPackageHint);

		if (compatiblePackage != null)
		{
			try
			{
				final Resources localeResources = packageManager.getResourcesForApplication(compatiblePackage);

				return localeResources.getText(localeResources.getIdentifier(resourceName, "string", compatiblePackage)); //$NON-NLS-1$
			}
			catch (final Exception e)
			{
				/*
				 * In an ideal world, this error will never happen. This catch is necessary, however, due to a TOCTOU error where
				 * the compatible package could be uninstalled at any time.
				 */
				return null;
			}
		}

		return null;
	}

	/**
	 * Helper method to load resources from the <i>Locale</i> Developer Platform host.
	 *
	 * @param packageManager an instance of {@code PackageManager}. Cannot be null.
	 * @param callingPackageHint hint as to which package is the calling package, from which resources might be preferred. This
	 *            parameter may be null.
	 * @param resourceName the {@code String} name of the resource to load. This must be one of the strings defined as a static
	 *            constant in this class. Cannot be null or empty.
	 * @return the resource requested.
	 */
	public static Drawable getDrawableResource(final PackageManager packageManager, final String callingPackageHint, final String resourceName)
	{
		if (packageManager == null)
		{
			throw new IllegalArgumentException(String.format("%s.getDrawableResource(): packageManager param was null", LOGGING_CLASS_NAME)); //$NON-NLS-1$
		}

		if (TextUtils.isEmpty(resourceName))
		{
			throw new IllegalArgumentException(String.format("%s.getDrawableResource(): resourceName param was null or empty", LOGGING_CLASS_NAME)); //$NON-NLS-1$
		}

		final String compatiblePackage = PackageUtilities.getCompatiblePackage(packageManager, callingPackageHint);

		if (compatiblePackage != null)
		{
			try
			{
				final Resources localeResources = packageManager.getResourcesForApplication(compatiblePackage);

				return localeResources.getDrawable(localeResources.getIdentifier(resourceName, "drawable", compatiblePackage)); //$NON-NLS-1$
			}
			catch (final Exception e)
			{
				/*
				 * In an ideal world, this error will never happen. This catch is necessary however, due to a TOCTOU error where
				 * the compatible package could be uninstalled at any time.
				 */
				return null;
			}
		}

		return null;
	}
}