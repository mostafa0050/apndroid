package com.google.code.apndroid.stats;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * @author pavlov
 * @since 26.08.11
 */
public class MailStatisticsSender implements StatisticsSender{

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
        return "";
    }
}
