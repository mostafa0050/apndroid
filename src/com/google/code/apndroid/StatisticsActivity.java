package com.google.code.apndroid;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import com.google.code.apndroid.dao.ApnInformationDao;
import com.google.code.apndroid.dao.DaoUtil;
import com.google.code.apndroid.stats.StatisticsData;

/**
 * Activity for gather phone information:
 * <ul>
 *     <li>Network name and country</li>
 *     <li>Phone model and os version</li>
 *     <li>Apn data and current active apn</li>
 *     <li>Results of switch test</li>
 * </ul>
 *
 * @author pavlov
 * @since 24.08.11
 */
public class StatisticsActivity extends Activity {

    private StatisticsData phoneData;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.phone_statistics);
    }

    @Override
    protected void onResume() {
        super.onResume();

        gatherPhoneInformation();
        initializeActivityFields();
    }

    private void gatherPhoneInformation() {
        phoneData = new StatisticsData();

        TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        phoneData.setPhoneRadioType(defineNetworkType(manager));
        phoneData.setNetworkOperatorName(manager.getNetworkOperatorName());
        phoneData.setNetworkOperatorCode(manager.getNetworkOperator());
        phoneData.setNetworkCountry(manager.getNetworkCountryIso());
        phoneData.setSimCountry(manager.getSimCountryIso());

        phoneData.setPhoneModel(Build.MODEL);
        phoneData.setPhoneManufacturer(Build.MANUFACTURER);
        phoneData.setOsReleaseVersion(Build.VERSION.RELEASE);
        phoneData.setSdkVersion(Build.VERSION.SDK_INT);

        ApnInformationDao dao = DaoUtil.getDaoFactory(getApplication()).getInformationDao(this);
        phoneData.setRegisteredApns(dao.findAllApns());
        phoneData.setCurrentActiveApnId(dao.getCurrentActiveApnId());

    }

    private StatisticsData.PhoneRadioType defineNetworkType(TelephonyManager manager) {
        int phoneType = manager.getPhoneType();
        if (phoneType == TelephonyManager.PHONE_TYPE_GSM) {
            return StatisticsData.PhoneRadioType.GSM;
        } else if (phoneType == TelephonyManager.PHONE_TYPE_CDMA) {
            return StatisticsData.PhoneRadioType.CDMA;
        } else {
            return StatisticsData.PhoneRadioType.NONE;
        }
    }

    private void initializeActivityFields() {
        findTextViewById(R.id.op_code).setText(phoneData.getNetworkOperatorCode());
        findTextViewById(R.id.op_name).setText(phoneData.getNetworkOperatorName());
        findTextViewById(R.id.apn_count_field).setText(phoneData.getRegisteredApns().size()+"");
        String activeApnId = "";
        if (phoneData.getCurrentActiveApnId() != null){
            activeApnId = phoneData.getCurrentActiveApnId().toString();
        }
        findTextViewById(R.id.active_apn).setText(activeApnId);
    }

    private TextView findTextViewById(int id){
        return (TextView) findViewById(id);
    }


}