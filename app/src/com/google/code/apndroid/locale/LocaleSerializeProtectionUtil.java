package com.google.code.apndroid.locale;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.google.code.apndroid.Constants;

/**
 * @author pavlov
 * @since 06.04.11
 */
public class LocaleSerializeProtectionUtil {
    /**
     * Checking for custom serializable attack according to Locale guidelines and examples
     *
     * @param intent
     * @return {@code true} if attack detected and {@code false} otherwise
     */
    public static boolean checkForCustomSerializableAttack(Intent intent) {
        return checkForCustomSerializableAttack(intent.getExtras());
    }

    public static boolean checkForCustomSerializableAttackInExtraBundle(Intent intent) {
        return checkForCustomSerializableAttack(intent.getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE));
    }

    public static boolean checkForCustomSerializableAttack(Bundle extras) {
        //     This is a hack to work around a custom serializable classloader attack. This check must come before any of the bundle
        //     content are examined.
        try {
            if (extras != null) {
                // if a custom serializable exists, this will throw an exception
                extras.containsKey(null);
            }
        } catch (final Exception e) {
            Log.e(Constants.APP_LOG, "Custom serializable attack detected; do not send custom Serializable subclasses to this receiver", e);
            return true;
        }
        return false;
    }
}
