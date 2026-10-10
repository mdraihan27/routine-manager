package io.github.mdraihan27.routinemanager.feature.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.core.content.ContextCompat;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.github.mdraihan27.routinemanager.R;
import io.github.mdraihan27.routinemanager.feature.routine.domain.model.WeeklyClassWithCourse;
import io.github.mdraihan27.routinemanager.feature.course.presentation.CourseColorResolver;
import io.github.mdraihan27.routinemanager.feature.schedule.domain.usecase.GetWeeklyClassesOverviewUseCase;

@AndroidEntryPoint
public class RoutineWidgetService extends RemoteViewsService {

    @Inject
    GetWeeklyClassesOverviewUseCase getWeeklyClassesOverviewUseCase;

    @Inject
    CourseColorResolver colorResolver;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RoutineRemoteViewsFactory(this.getApplicationContext(), getWeeklyClassesOverviewUseCase, colorResolver);
    }
}

class RoutineRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private final Context context;
    private final GetWeeklyClassesOverviewUseCase getWeeklyClassesOverviewUseCase;
    private final CourseColorResolver colorResolver;
    
    private List<DayOfWeek> activeDays = new ArrayList<>();
    private List<Integer> sortedTimes = new ArrayList<>();
    private Map<DayOfWeek, List<WeeklyClassWithCourse>> classesByDay = new HashMap<>();

    public RoutineRemoteViewsFactory(Context context, GetWeeklyClassesOverviewUseCase getWeeklyClassesOverviewUseCase, CourseColorResolver colorResolver) {
        this.context = context;
        this.getWeeklyClassesOverviewUseCase = getWeeklyClassesOverviewUseCase;
        this.colorResolver = colorResolver;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        try {
            List<WeeklyClassWithCourse> classes = getWeeklyClassesOverviewUseCase.execute().blockingFirst();
            activeDays.clear();
            sortedTimes.clear();
            classesByDay.clear();
            
            if (classes != null && !classes.isEmpty()) {
                for (WeeklyClassWithCourse wc : classes) {
                    DayOfWeek day = wc.getWeeklyClass().getDayOfWeek();
                    int time = wc.getWeeklyClass().getStartHour() * 60 + wc.getWeeklyClass().getStartMinute();
                    
                    if (!activeDays.contains(day)) activeDays.add(day);
                    if (!sortedTimes.contains(time)) sortedTimes.add(time);
                    
                    if (!classesByDay.containsKey(day)) {
                        classesByDay.put(day, new ArrayList<>());
                    }
                    classesByDay.get(day).add(wc);
                }
                
                Collections.sort(activeDays);
                Collections.sort(sortedTimes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        activeDays.clear();
        sortedTimes.clear();
        classesByDay.clear();
    }

    @Override
    public int getCount() {
        if (sortedTimes.isEmpty()) return 0;
        return sortedTimes.size() + 1; // +1 for the header row
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position >= getCount()) return null;

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_routine_item);
        
        int[] cellIds = {R.id.tvCell1, R.id.tvCell2, R.id.tvCell3, R.id.tvCell4, R.id.tvCell5, R.id.tvCell6, R.id.tvCell7};

        if (position == 0) {
            // Header row
            views.setTextViewText(R.id.tvRowTime, "");
            for (int i = 0; i < 7; i++) {
                if (i < activeDays.size()) {
                    views.setViewVisibility(cellIds[i], View.VISIBLE);
                    String dayName = activeDays.get(i).getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.getDefault());
                    views.setTextViewText(cellIds[i], dayName);
                    views.setTextColor(cellIds[i], ContextCompat.getColor(context, R.color.text_on_surface));
                } else {
                    views.setViewVisibility(cellIds[i], View.GONE);
                }
            }
        } else {
            // Time row
            int time = sortedTimes.get(position - 1);
            int h = time / 60;
            int m = time % 60;
            String timeStr = io.github.mdraihan27.routinemanager.core.util.DateTimeFormatter.formatTime12Hour(h, m);
            views.setTextViewText(R.id.tvRowTime, timeStr);

            for (int i = 0; i < 7; i++) {
                if (i < activeDays.size()) {
                    views.setViewVisibility(cellIds[i], View.VISIBLE);
                    DayOfWeek day = activeDays.get(i);
                    WeeklyClassWithCourse matchingClass = null;
                    
                    List<WeeklyClassWithCourse> dayClasses = classesByDay.get(day);
                    if (dayClasses != null) {
                        for (WeeklyClassWithCourse c : dayClasses) {
                            if ((c.getWeeklyClass().getStartHour() * 60 + c.getWeeklyClass().getStartMinute()) == time) {
                                matchingClass = c;
                                break;
                            }
                        }
                    }

                    if (matchingClass != null) {
                        views.setTextViewText(cellIds[i], matchingClass.getCourse().getCode());
                        try {
                            int color = colorResolver.resolve(matchingClass.getCourse().getColor());
                            views.setTextColor(cellIds[i], color);
                        } catch (Exception e) {
                            views.setTextColor(cellIds[i], ContextCompat.getColor(context, R.color.text_on_surface));
                        }
                    } else {
                        views.setTextViewText(cellIds[i], "-");
                        views.setTextColor(cellIds[i], ContextCompat.getColor(context, R.color.text_muted));
                    }
                } else {
                    views.setViewVisibility(cellIds[i], View.GONE);
                }
            }
        }

        Intent fillInIntent = new Intent();
        views.setOnClickFillInIntent(R.id.widget_routine_item_root, fillInIntent);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1; // Technically 2 (header and row), but RemoteViews framework only cares if we return completely different layouts
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
