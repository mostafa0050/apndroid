package com.google.code.apndroid;

/**
 * 
 * @author Julien Muniak <julien.muniak@gmail.com>
 */
public final class LocaleConstants {
    
    private LocaleConstants()
    {
        throw new UnsupportedOperationException("LocaleConstants(): Cannot instantiate Constants");
    }

    /**
     * Constant used for storing and retrieving the message text from store-and-forward {@code Intent}s.
     */
    protected static final String INTENT_EXTRA_STATE = "com.google.code.apndroid.localestate";
    protected static final String INTENT_EXTRA_SHOW_NOTIFICATION = "com.google.code.apndroid.localshownotifcation"; 
}