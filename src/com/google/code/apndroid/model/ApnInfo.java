package com.google.code.apndroid.model;

/**
 * Selection of few interesting columns from APN table
 */
public class ApnInfo {

    private long id;
    private String apn;
    private String type;

    public ApnInfo() {
    }

    public ApnInfo(long id, String apn, String type) {
        this.id = id;
        this.apn = apn;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getApn() {
        return apn;
    }

    public void setApn(String apn) {
        this.apn = apn;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
