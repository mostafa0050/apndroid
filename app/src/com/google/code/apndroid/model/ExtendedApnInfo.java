package com.google.code.apndroid.model;

/**
 * @author pavlov
 * @since 26.08.11
 */
public class ExtendedApnInfo extends ApnInfo {

    private final String name;

    private final String proxy;

    private final String port;

    private final String mmsc;

    private final String mmc;

    private final String mnc;

    private final String authType;

    public ExtendedApnInfo(long id, String apn, String type, String name, String proxy, String port,
                           String mmsc, String mmc, String mnc, String authType) {
        super(id, apn, type);
        this.name = name;
        this.proxy = proxy;
        this.port = port;
        this.mmsc = mmsc;
        this.mmc = mmc;
        this.mnc = mnc;
        this.authType = authType;
    }

    public String getName() {
        return name;
    }

    public String getProxy() {
        return proxy;
    }

    public String getPort() {
        return port;
    }

    public String getMmsc() {
        return mmsc;
    }

    public String getMmc() {
        return mmc;
    }

    public String getMnc() {
        return mnc;
    }

    public String getAuthType() {
        return authType;
    }

}
