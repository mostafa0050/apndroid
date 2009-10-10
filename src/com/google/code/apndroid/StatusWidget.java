/*
 * This file is part of APNdroid.
 *
 * APNdroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * APNdroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with APNdroid. If not, see <http://www.gnu.org/licenses/>.
 */

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
