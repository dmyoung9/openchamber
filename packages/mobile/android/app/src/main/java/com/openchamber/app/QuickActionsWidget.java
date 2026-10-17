package com.openchamber.app;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

public class QuickActionsWidget extends AppWidgetProvider {
    private static PendingIntent action(Context context, String path, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("openchamber://" + path));
        intent.setClass(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] widgetIds) {
        for (int id : widgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_quick_actions);
            views.setOnClickPendingIntent(R.id.widget_text, action(context, "new", 100));
            views.setOnClickPendingIntent(R.id.widget_voice, action(context, "new?mode=voice", 101));
            views.setOnClickPendingIntent(R.id.widget_image, action(context, "new?mode=image", 102));
            manager.updateAppWidget(id, views);
        }
    }
}
