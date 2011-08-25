package com.google.code.apndroid.stats;

/**
 * @author pavlov
 * @since 25.08.11
 */
public class ApnInformation {

    private long id;
    private String apn;
    private String type;

    public ApnInformation() {
    }

    public ApnInformation(long id, String apn, String type) {
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
