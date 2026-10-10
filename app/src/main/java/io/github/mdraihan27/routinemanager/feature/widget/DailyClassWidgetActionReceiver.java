package io.github.mdraihan27.routinemanager.feature.widget;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.time.LocalDate;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.github.mdraihan27.routinemanager.feature.schedule.domain.usecase.CancelClassOccurrenceUseCase;
import io.github.mdraihan27.routinemanager.feature.schedule.domain.usecase.RestoreClassOccurrenceUseCase;
import io.reactivex.rxjava3.schedulers.Schedulers;

@AndroidEntryPoint
public class DailyClassWidgetActionReceiver extends BroadcastReceiver {

    public static final String ACTION_WIDGET_BUTTON = "io.github.mdraihan27.routinemanager.ACTION_WIDGET_BUTTON";

    @Inject
    CancelClassOccurrenceUseCase cancelUseCase;
    
    @Inject
    RestoreClassOccurrenceUseCase restoreUseCase;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || !ACTION_WIDGET_BUTTON.equals(intent.getAction())) return;

        long classId = intent.getLongExtra("EXTRA_CLASS_ID", -1);
        String actionType = intent.getStringExtra("EXTRA_ACTION_TYPE");

        if (classId != -1 && actionType != null) {
            if ("CANCEL".equals(actionType)) {
                cancelUseCase.execute(classId, LocalDate.now())
                        .subscribeOn(Schedulers.io())
                        .subscribe(() -> updateWidget(context), Throwable::printStackTrace);
            } else if ("UNDO".equals(actionType)) {
                restoreUseCase.execute(classId, LocalDate.now())
                        .subscribeOn(Schedulers.io())
                        .subscribe(() -> updateWidget(context), Throwable::printStackTrace);
            } else if ("RESCHEDULE".equals(actionType)) {
                Intent rescheduleIntent = new Intent(context, io.github.mdraihan27.routinemanager.MainActivity.class);
                rescheduleIntent.setAction("ACTION_RESCHEDULE_FROM_NOTIF");
                rescheduleIntent.putExtra("EXTRA_CLASS_ID", classId);
                rescheduleIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(rescheduleIntent);
            }
        }
    }

    private void updateWidget(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, DailyClassWidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, io.github.mdraihan27.routinemanager.R.id.flipper);
    }
}
