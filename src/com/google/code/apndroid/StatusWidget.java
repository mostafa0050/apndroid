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

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.code.apndroid.dao.DaoFactory;
import com.google.code.apndroid.preferences.Prefs;

/**
 * @author Pavlov Dmitry <pavlov.dmitry.n@gmail.com>
 */
public class StatusWidget extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (Constants.STATUS_CHANGED_MESSAGE.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            if (extras != null && extras.containsKey(Constants.STATUS_EXTRA)) {
                boolean isNetEnabled = extras.getBoolean(Constants.STATUS_EXTRA);
                
                AppWidgetManager manager = AppWidgetManager.getInstance(context);
                int[] widgetIds = manager.getAppWidgetIds(new ComponentName(context, StatusWidget.class));
                showWidget(context, manager, widgetIds, isNetEnabled);
                storeStatus(context, isNetEnabled);
            }
        } else if (Constants.STATUS_SWITCH_IN_PROGRESS_MESSAGE.equals(intent.getAction())) {
            Log.d(Constants.APP_LOG, "sending switch in progress broadcast received in widget handler");
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            int[] widgetIds = manager.getAppWidgetIds(new ComponentName(context, StatusWidget.class));
            showInProgressWidget(context, manager, widgetIds);
        }
    }

    private void storeStatus(Context context, boolean netEnabled) {
        Prefs prefs = new Prefs(context);
        prefs.setLastStatus(netEnabled);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] ints) {
        super.onUpdate(context, appWidgetManager, ints);
        Prefs prefs = new Prefs(context);
        boolean isNetEnabled;
        if (prefs.hasLastStatus()) {
            isNetEnabled = prefs.isLastStatusConnected();
        } else {
            isNetEnabled = DaoFactory.getDao(context).isDataEnabled();
        }
        showWidget(context, appWidgetManager, ints, isNetEnabled);
    }

    private void showWidget(Context context, AppWidgetManager manager, int[] widgetIds, boolean status) {
        RemoteViews views = createRemoteViews(context, status);
        manager.updateAppWidget(widgetIds, views);
    }

    private void showInProgressWidget(Context context, AppWidgetManager manager, int[] widgetIds) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        int iconId = R.drawable.data_in_progress;
        views.setImageViewResource(R.id.widgetCanvas, iconId);
        views.setTextViewText(R.id.widgetButton, "");
        manager.updateAppWidget(widgetIds, views);
    }

    private RemoteViews createRemoteViews(Context context, boolean status) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        int iconId = status ? R.drawable.data_on : R.drawable.data_off;
        String text = status ? "DATA\nON" : "DATA\nOFF";
        views.setImageViewResource(R.id.widgetCanvas, iconId);
        views.setTextViewText(R.id.widgetButton, text);

        Intent msg = new Intent(Constants.CHANGE_STATUS_REQUEST);
        PendingIntent intent = PendingIntent.getBroadcast(context, -1 /* not used */, msg, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widgetCanvas, intent);
        return views;
    }

}
