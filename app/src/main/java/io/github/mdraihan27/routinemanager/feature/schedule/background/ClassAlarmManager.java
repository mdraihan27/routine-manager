package io.github.mdraihan27.routinemanager.feature.schedule.background;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import io.github.mdraihan27.routinemanager.core.datastore.PreferencesDataSource;
import io.github.mdraihan27.routinemanager.core.time.AppClock;
import io.github.mdraihan27.routinemanager.feature.schedule.domain.model.ClassStatus;
import io.github.mdraihan27.routinemanager.feature.schedule.domain.model.DailyClassItem;
import io.github.mdraihan27.routinemanager.feature.schedule.domain.model.DailySchedule;

@Singleton
public class ClassAlarmManager {

    private final Context context;
    private final AlarmManager alarmManager;
    private final PreferencesDataSource preferencesDataSource;
    private final AppClock appClock;

    @Inject
    public ClassAlarmManager(@NonNull @ApplicationContext Context context,
                             @NonNull PreferencesDataSource preferencesDataSource,
                             @NonNull AppClock appClock) {
        this.context = context;
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        this.preferencesDataSource = preferencesDataSource;
        this.appClock = appClock;
    }

    public void scheduleAlarms(@NonNull DailySchedule schedule) {
        if (alarmManager == null) return;

        LocalDate today = appClock.currentDate();
        List<DailyClassItem> classes = schedule.getClasses();
        
        // Cancel all existing alarms first to avoid duplicates
        // In a real app we might want to track pending intents, but for simplicity, we'll recreate them using fixed request codes
        
        boolean isFirstNonCanceledFound = false;

        for (int i = 0; i < classes.size(); i++) {
            DailyClassItem item = classes.get(i);

            // Cancel any existing alarm for this class index
            cancelAlarm(i, ClassAlarmReceiver.ACTION_CLASS_START);
            
            if (item.getStatus() == ClassStatus.CANCELLED) continue;

            LocalDateTime classStart = today.atTime(item.getStartHour(), item.getStartMinute());
            long classStartMillis = classStart.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

            // Only schedule if it's in the future
            if (classStartMillis > System.currentTimeMillis()) {
                setExactAlarm(i, classStartMillis, ClassAlarmReceiver.ACTION_CLASS_START, item);
            }

            if (!isFirstNonCanceledFound) {
                isFirstNonCanceledFound = true;
                
                // Cancel existing pre-first-class alarm
                cancelAlarm(1000, ClassAlarmReceiver.ACTION_PRE_FIRST_CLASS);

                if (preferencesDataSource.isClassAlarmEnabled()) {
                    int minutesBefore = preferencesDataSource.getClassAlarmMinutesBefore();
                    LocalDateTime preAlarmTime = classStart.minusMinutes(minutesBefore);
                    long preAlarmMillis = preAlarmTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

                    if (preAlarmMillis > System.currentTimeMillis()) {
                        setExactAlarm(1000, preAlarmMillis, ClassAlarmReceiver.ACTION_PRE_FIRST_CLASS, item);
                    }
                }
            }
        }
    }

    private void setExactAlarm(int requestCode, long timeMillis, String action, DailyClassItem item) {
        Intent intent = new Intent(context, ClassAlarmReceiver.class);
        intent.setAction(action);
        intent.putExtra(ClassAlarmReceiver.EXTRA_COURSE_CODE, item.getCourseCode());
        intent.putExtra(ClassAlarmReceiver.EXTRA_COURSE_NAME, item.getCourseName());
        intent.putExtra(ClassAlarmReceiver.EXTRA_CLASS_ID, item.getWeeklyClassId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeMillis, pendingIntent);
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeMillis, pendingIntent);
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeMillis, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeMillis, pendingIntent);
            }
        } catch (SecurityException ignored) {
            // Handle devices that throw SecurityException when missing SCHEDULE_EXACT_ALARM
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeMillis, pendingIntent);
        }
    }

    private void cancelAlarm(int requestCode, String action) {
        Intent intent = new Intent(context, ClassAlarmReceiver.class);
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE
        );

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }
}
