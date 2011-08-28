package com.google.code.apndroid.model;

/**
 * Selection of few interesting columns from APN table
 */
public class ApnInfo {

    private long id;
    private String apn;
    private String type;

    public ApnInfo(long id, String apn, String type) {
        this.id = id;
        this.apn = apn;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public String getApn() {
        return apn;
    }

    public String getType() {
        return type;
    }

}
