package com.google.code.apndroid.dao;

public final class ApnQuery {

    public static String[] SWITCH_PROJECTION = {
            ApnColumns._ID,
            ApnColumns.APN,
            ApnColumns.TYPE
    };

    public static String[] EXTENDED_PROJECTION = {
            ApnColumns._ID,
            ApnColumns.APN,
            ApnColumns.TYPE,
            ApnColumns.NAME,
            ApnColumns.PROXY,
            ApnColumns.PORT,
            ApnColumns.MMSC,
            ApnColumns.MCC,
            ApnColumns.MNC,
            ApnColumns.AUTH_TYPE
    };

    public static final int _ID = 0;
    public static final int APN = 1;
    public static final int TYPE = 2;
    public static final int NAME = 3;
    public static final int PROXY = 4;
    public static final int PORT = 5;
    public static final int MMSC = 6;
    public static final int MCC = 7;
    public static final int MNC = 8;
    public static final int AUTH_TYPE = 9;

}
