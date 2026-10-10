package io.github.mdraihan27.routinemanager.feature.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import io.github.mdraihan27.routinemanager.MainActivity;
import io.github.mdraihan27.routinemanager.R;

public class DailyClassWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_NEXT = "io.github.mdraihan27.routinemanager.ACTION_NEXT";
    public static final String ACTION_PREV = "io.github.mdraihan27.routinemanager.ACTION_PREV";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        
        if (ACTION_NEXT.equals(action) || ACTION_PREV.equals(action)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisWidget = new ComponentName(context, DailyClassWidgetProvider.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_daily_class);
            if (ACTION_NEXT.equals(action)) {
                views.showNext(R.id.flipper);
            } else {
                views.showPrevious(R.id.flipper);
            }
            appWidgetManager.updateAppWidget(thisWidget, views);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Intent intent = new Intent(context, DailyClassWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_daily_class);
        views.setRemoteAdapter(R.id.flipper, intent);
        views.setEmptyView(R.id.flipper, R.id.empty_view);

        // Next/Prev buttons
        Intent nextIntent = new Intent(context, DailyClassWidgetProvider.class);
        nextIntent.setAction(ACTION_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.btnNext, nextPendingIntent);

        Intent prevIntent = new Intent(context, DailyClassWidgetProvider.class);
        prevIntent.setAction(ACTION_PREV);
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(context, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.btnPrev, prevPendingIntent);

        // Action Receiver Template for Cancel/Reschedule/Undo
        Intent actionIntent = new Intent(context, DailyClassWidgetActionReceiver.class);
        actionIntent.setAction(DailyClassWidgetActionReceiver.ACTION_WIDGET_BUTTON);
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 0, actionIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE); // Must be mutable for fillInIntent
        views.setPendingIntentTemplate(R.id.flipper, actionPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}
