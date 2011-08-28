package com.google.code.apndroid.stats;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.google.code.apndroid.model.ExtendedApnInfo;

/**
 * @author pavlov
 * @since 26.08.11
 */
public class MailStatisticsSender implements StatisticsSender {

    private final String destinationAddress;

    private Context context;

    public MailStatisticsSender(String destinationAddress, Context context) {
        this.destinationAddress = destinationAddress;
        this.context = context;
    }

    @Override
    public void sendStatistics(StatisticsData data) {

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"recipient@example.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "APNDroid stats");
        i.putExtra(Intent.EXTRA_TEXT, createMailBody(data));
        try {
            context.startActivity(Intent.createChooser(i, "Sending an email..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }


    }

    private String createMailBody(StatisticsData data) {
        StringBuilder b = new StringBuilder();
        appendNetworkAndSimInformation(b, data);
        appendPhoneModelInformation(b, data);
        appendApnsInformation(b, data);
        appendUserComments(b,data);
        return b.toString();
    }

    private void appendNetworkAndSimInformation(StringBuilder b, StatisticsData data) {
        b.append("Network info\n");

        b.append("Network (radio) type: ").append(data.getPhoneRadioType()).append("\n");
        b.append("Operator code: ").append(data.getNetworkOperatorCode()).append("\n");
        b.append("Operator name: ").append(data.getNetworkOperatorName()).append("\n");
        b.append("Operator country: ").append(data.getNetworkCountry()).append("\n");
        b.append("Sim code: ").append(data.getSimOperatorName()).append("\n");
        b.append("Sim name: ").append(data.getSimOperatorName()).append("\n");
        b.append("Sim country: ").append(data.getSimCountry()).append("\n");
    }

    private void appendPhoneModelInformation(StringBuilder b, StatisticsData data) {
        b.append("Phone model information\n");

        b.append("Model: ").append(data.getPhoneModel());
        b.append("Manufacturer: ").append(data.getPhoneManufacturer());
        b.append("OS version: ").append(data.getOsReleaseVersion());
        b.append("SDK version: ").append(data.getSdkVersion());
    }

    private void appendApnsInformation(StringBuilder b, StatisticsData data) {
        b.append("APN info\n");
        Long activeApn = data.getCurrentActiveApnId();
        b.append("Name; APN; Type; Proxy; Port; MMSC; MCC; MNC;Auth type;Active").append("\n");
        for (ExtendedApnInfo info : data.getRegisteredApns()) {
            b.append(info.getName()).append(";")
                    .append(info.getApn()).append(";")
                    .append(info.getType()).append(";")
                    .append(info.getProxy()).append(";")
                    .append(info.getProxy()).append(";")
                    .append(info.getPort()).append(";")
                    .append(info.getMmsc()).append(";")
                    .append(info.getMcc()).append(";")
                    .append(info.getMnc()).append(";")
                    .append(info.getAuthType()).append(";");
            if (activeApn != null && activeApn.equals(info.getId())){
                b.append("y");
            }else {
                b.append("n");
            }
            b.append("\n");
        }
    }

    private void appendUserComments(StringBuilder b, StatisticsData data) {
        b.append("User comments: ").append(data.getUserComment()).append("\n");
    }
}
