package com.google.code.apndroid;

import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.ComponentName;
import android.widget.RemoteViews;
import android.widget.ToggleButton;
import android.os.Bundle;

/**
 * @author Pavlov "Zelgadis" Dmitry
 */
public class StatusWidget extends AppWidgetProvider{

    private static final String WIDGET_SETTINGS = "com.google.code.apndroid.widget.SETTINGS";
    private static final String WIDGET_STATUS = "com.google.code.apndroid.widget.STATUS";
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (StatusReceiver.APN_DROID_STATUS.equals(intent.getAction())){
            Bundle extras = intent.getExtras();
            if (extras != null && extras.containsKey(StatusReceiver.APN_DROID_STATUS_EXTRA)){
                boolean isNetEnabled = extras.getBoolean(StatusReceiver.APN_DROID_STATUS_EXTRA);
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
        if (prefs.contains(WIDGET_STATUS)){
            isNetEnabled = prefs.getBoolean(WIDGET_STATUS,true);
        }else{
            isNetEnabled = DbUtil.getApnState(context.getContentResolver());
        }
        showWidget(context, appWidgetManager, ints, isNetEnabled);
    }

    private void showWidget(Context context, AppWidgetManager manager, int[] widgetIds, boolean status){
        RemoteViews views = createRemoteViews(context, status);
        manager.updateAppWidget(widgetIds, views);
    }

    private RemoteViews createRemoteViews(Context context, boolean status){
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        views.setTextViewText(R.id.widgetButton, status ? "checked" : "unchecked");
        return views;
    }

}
