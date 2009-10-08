package com.google.code.apndroid;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.app.PendingIntent;

/**
 * @author Pavlov "Zelgadis" Dmitry
 */
public class StatusWidget extends AppWidgetProvider {

    private static final String WIDGET_SETTINGS = "com.google.code.apndroid.widget.SETTINGS";
    private static final String WIDGET_STATUS = "com.google.code.apndroid.widget.STATUS";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (ApplicationConstants.APN_DROID_STATUS.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            if (extras != null && extras.containsKey(ApplicationConstants.APN_DROID_STATUS_EXTRA)) {
                boolean isNetEnabled = extras.getBoolean(ApplicationConstants.APN_DROID_STATUS_EXTRA);
                AppWidgetManager manager = AppWidgetManager.getInstance(context);
                int[] widgetIds = manager.getAppWidgetIds(new ComponentName(context, StatusWidget.class));
                showWidget(context, manager, widgetIds, isNetEnabled);
                storeStatus(context, isNetEnabled);
            }
        }
    }

    private void storeStatus(Context context, boolean netEnabled) {
        SharedPreferences.Editor editor = context.getSharedPreferences(WIDGET_SETTINGS, Context.MODE_PRIVATE).edit();
        editor.putBoolean(WIDGET_STATUS, netEnabled);
        editor.commit();
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] ints) {
        super.onUpdate(context, appWidgetManager, ints);
        SharedPreferences prefs = context.getSharedPreferences(WIDGET_SETTINGS, Context.MODE_PRIVATE);
        boolean isNetEnabled;
        if (prefs.contains(WIDGET_STATUS)) {
            isNetEnabled = prefs.getBoolean(WIDGET_STATUS, true);
        } else {
            isNetEnabled = DbUtil.getApnState(context.getContentResolver());
        }
        showWidget(context, appWidgetManager, ints, isNetEnabled);
    }

    private void showWidget(Context context, AppWidgetManager manager, int[] widgetIds, boolean status) {
        RemoteViews views = createRemoteViews(context, status);
        manager.updateAppWidget(widgetIds, views);
    }

    private RemoteViews createRemoteViews(Context context, boolean status) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        int iconId = status ? R.drawable.apndroid_widget_on : R.drawable.apndroid_widget_off;
        views.setImageViewResource(R.id.widgetCanvas, iconId);

        Intent msg = new Intent(ApplicationConstants.APN_DROID_CHANGE_STATUS);
        PendingIntent intent  = PendingIntent.getBroadcast(context, -1 /*not used*/, msg, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widgetCanvas, intent);
        return views;
    }

}
