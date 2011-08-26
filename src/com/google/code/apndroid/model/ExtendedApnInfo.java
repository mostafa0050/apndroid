package com.google.code.apndroid.model;

/**
 * @author pavlov
 * @since 26.08.11
 */
public class ExtendedApnInfo extends ApnInfo{

    private String name;

    private String proxy;

    private String port;

    private String mmsc;

    private String mmc;

    private String mnc;

    private String authType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getMmsc() {
        return mmsc;
    }

    public void setMmsc(String mmsc) {
        this.mmsc = mmsc;
    }

    public String getMmc() {
        return mmc;
    }

    public void setMmc(String mmc) {
        this.mmc = mmc;
    }

    public String getMnc() {
        return mnc;
    }

    public void setMnc(String mnc) {
        this.mnc = mnc;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }
}
