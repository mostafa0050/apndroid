package com.google.code.apndroid.stats;

import com.google.code.apndroid.model.ExtendedApnInfo;

import java.util.List;

/**
 * @author pavlov
 * @since 24.08.11
 */
public class StatisticsData {

    public enum PhoneRadioType {NONE, GSM, CDMA;}

    private PhoneRadioType phoneRadioType;

    private String networkOperatorName;
    private String networkOperatorCode;
    private String simOperatorName;
    private String simOperatorCode;
    private String networkCountry;
    private String simCountry;

    private String phoneModel;
    private String phoneManufacturer;
    private String osReleaseVersion;
    private int sdkVersion;

    private List<ExtendedApnInfo> registeredApns;
    private Long currentActiveApnId;

    private String userComment;

    public PhoneRadioType getPhoneRadioType() {
        return phoneRadioType;
    }

    public void setPhoneRadioType(PhoneRadioType phoneRadioType) {
        this.phoneRadioType = phoneRadioType;
    }

    public String getNetworkOperatorName() {
        return networkOperatorName;
    }

    public void setNetworkOperatorName(String networkOperatorName) {
        this.networkOperatorName = networkOperatorName;
    }

    public String getNetworkOperatorCode() {
        return networkOperatorCode;
    }

    public void setNetworkOperatorCode(String networkOperatorCode) {
        this.networkOperatorCode = networkOperatorCode;
    }

    public String getSimOperatorName() {
        return simOperatorName;
    }

    public void setSimOperatorName(String simOperatorName) {
        this.simOperatorName = simOperatorName;
    }

    public String getSimOperatorCode() {
        return simOperatorCode;
    }

    public void setSimOperatorCode(String simOperatorCode) {
        this.simOperatorCode = simOperatorCode;
    }

    public String getNetworkCountry() {
        return networkCountry;
    }

    public void setNetworkCountry(String networkCountry) {
        this.networkCountry = networkCountry;
    }

    public String getSimCountry() {
        return simCountry;
    }

    public void setSimCountry(String simCountry) {
        this.simCountry = simCountry;
    }

    public String getPhoneModel() {
        return phoneModel;
    }

    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }

    public String getPhoneManufacturer() {
        return phoneManufacturer;
    }

    public void setPhoneManufacturer(String phoneManufacturer) {
        this.phoneManufacturer = phoneManufacturer;
    }

    public String getOsReleaseVersion() {
        return osReleaseVersion;
    }

    public void setOsReleaseVersion(String osReleaseVersion) {
        this.osReleaseVersion = osReleaseVersion;
    }

    public int getSdkVersion() {
        return sdkVersion;
    }

    public void setSdkVersion(int sdkVersion) {
        this.sdkVersion = sdkVersion;
    }

    public List<ExtendedApnInfo> getRegisteredApns() {
        return registeredApns;
    }

    public void setRegisteredApns(List<ExtendedApnInfo> registeredApns) {
        this.registeredApns = registeredApns;
    }

    public Long getCurrentActiveApnId() {
        return currentActiveApnId;
    }

    public void setCurrentActiveApnId(Long currentActiveApnId) {
        this.currentActiveApnId = currentActiveApnId;
    }

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }
}
