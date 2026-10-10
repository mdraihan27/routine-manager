package io.github.mdraihan27.routinemanager.feature.widget;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.github.mdraihan27.routinemanager.R;
import io.github.mdraihan27.routinemanager.feature.schedule.domain.model.ClassStatus;
import io.github.mdraihan27.routinemanager.feature.schedule.domain.model.DailyClassItem;
import io.github.mdraihan27.routinemanager.feature.schedule.domain.model.DailySchedule;
import io.github.mdraihan27.routinemanager.feature.schedule.domain.usecase.GetTodayScheduleUseCase;

@AndroidEntryPoint
public class DailyClassWidgetService extends RemoteViewsService {

    @Inject
    GetTodayScheduleUseCase getTodayScheduleUseCase;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new DailyClassRemoteViewsFactory(this.getApplicationContext(), getTodayScheduleUseCase);
    }
}

class DailyClassRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private final Context context;
    private final GetTodayScheduleUseCase getTodayScheduleUseCase;
    private List<DailyClassItem> classItems = new ArrayList<>();

    public DailyClassRemoteViewsFactory(Context context, GetTodayScheduleUseCase getTodayScheduleUseCase) {
        this.context = context;
        this.getTodayScheduleUseCase = getTodayScheduleUseCase;
    }

    @Override
    public void onCreate() {}

    @Override
    public void onDataSetChanged() {
        try {
            DailySchedule schedule = getTodayScheduleUseCase.execute().blockingFirst();
            if (schedule != null) {
                classItems = schedule.getClasses();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        classItems.clear();
    }

    @Override
    public int getCount() {
        return classItems.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position >= classItems.size()) return null;

        DailyClassItem item = classItems.get(position);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_daily_class_item);

        views.setTextViewText(R.id.tvClassCourseCode, item.getCourseCode());
        views.setTextViewText(R.id.tvClassCourseName, item.getCourseName());
        views.setTextViewText(R.id.tvClassTeacherName, item.getTeacherName() != null ? item.getTeacherName() : "");
        
        boolean isCancelled = item.getStatus() == ClassStatus.CANCELLED;
        views.setTextViewText(R.id.tvClassStatusLabel, item.getStatus() != null ? item.getStatus().name() : "");

        String time = io.github.mdraihan27.routinemanager.core.util.DateTimeFormatter.formatTime12Hour(item.getStartHour(), item.getStartMinute()) + " - " + io.github.mdraihan27.routinemanager.core.util.DateTimeFormatter.formatTime12Hour(item.getEndHour(), item.getEndMinute());
        views.setTextViewText(R.id.tvClassTime, time);
        
        String positionStr = (position + 1) + " of " + classItems.size();
        views.setTextViewText(R.id.tvClassPosition, positionStr);

        if (isCancelled) {
            views.setViewVisibility(R.id.layoutActionButtons, View.GONE);
            views.setViewVisibility(R.id.btnWidgetUndo, View.VISIBLE);
        } else {
            views.setViewVisibility(R.id.layoutActionButtons, View.VISIBLE);
            views.setViewVisibility(R.id.btnWidgetUndo, View.GONE);
        }

        // Cancel Intent
        Intent cancelIntent = new Intent();
        cancelIntent.putExtra("EXTRA_CLASS_ID", item.getWeeklyClassId());
        cancelIntent.putExtra("EXTRA_ACTION_TYPE", "CANCEL");
        views.setOnClickFillInIntent(R.id.btnWidgetCancel, cancelIntent);

        // Undo Intent
        Intent undoIntent = new Intent();
        undoIntent.putExtra("EXTRA_CLASS_ID", item.getWeeklyClassId());
        undoIntent.putExtra("EXTRA_ACTION_TYPE", "UNDO");
        views.setOnClickFillInIntent(R.id.btnWidgetUndo, undoIntent);

        // Reschedule Intent
        Intent rescheduleIntent = new Intent();
        rescheduleIntent.putExtra("EXTRA_CLASS_ID", item.getWeeklyClassId());
        rescheduleIntent.putExtra("EXTRA_ACTION_TYPE", "RESCHEDULE");
        views.setOnClickFillInIntent(R.id.btnWidgetReschedule, rescheduleIntent);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() { return null; }

    @Override
    public int getViewTypeCount() { return 1; }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public boolean hasStableIds() { return true; }
}
