package io.github.mdraihan27.routinemanager.core.background;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.time.LocalDate;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.github.mdraihan27.routinemanager.MainActivity;
import io.github.mdraihan27.routinemanager.R;
import io.github.mdraihan27.routinemanager.core.threading.AppSchedulers;
import io.github.mdraihan27.routinemanager.core.time.AppClock;
import io.github.mdraihan27.routinemanager.feature.schedule.domain.model.DailyClassItem;
import io.github.mdraihan27.routinemanager.feature.schedule.domain.model.DailySchedule;
import io.github.mdraihan27.routinemanager.feature.schedule.domain.usecase.CancelClassOccurrenceUseCase;
import io.github.mdraihan27.routinemanager.feature.schedule.domain.usecase.GetTodayScheduleUseCase;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@AndroidEntryPoint
public class AppKeepAliveService extends Service {

    public static final String CHANNEL_ID = "keep_alive_channel";
    public static final int NOTIFICATION_ID = 1001;

    public static final String ACTION_NEXT = "ACTION_NEXT";
    public static final String ACTION_PREV = "ACTION_PREV";
    public static final String ACTION_CANCEL_CLASS = "ACTION_CANCEL_CLASS";

    @Inject GetTodayScheduleUseCase getTodayScheduleUseCase;
    @Inject CancelClassOccurrenceUseCase cancelClassOccurrenceUseCase;
    @Inject AppSchedulers appSchedulers;
    @Inject AppClock appClock;
    @Inject io.github.mdraihan27.routinemanager.feature.schedule.background.ClassAlarmManager classAlarmManager;

    private final CompositeDisposable disposables = new CompositeDisposable();
    private DailySchedule currentSchedule = null;
    private int currentOffset = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        observeSchedule();
    }

    private void observeSchedule() {
        disposables.add(
                getTodayScheduleUseCase.execute()
                        .subscribeOn(appSchedulers.io())
                        .observeOn(appSchedulers.main())
                        .subscribe(schedule -> {
                            currentSchedule = schedule;
                            updateNotification();
                            classAlarmManager.scheduleAlarms(schedule);
                        }, throwable -> {})
        );
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            handleAction(intent.getAction());
        }

        Notification notification = buildNotification();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
        } else {
            startForeground(NOTIFICATION_ID, notification);
        }
        return START_STICKY;
    }

    private void handleAction(String action) {
        if (currentSchedule == null) return;
        List<DailyClassItem> classes = currentSchedule.getClasses();
        if (classes.isEmpty()) return;

        int baseIndex = currentSchedule.getCurrentClassIndex();
        int currentIndex = baseIndex + currentOffset;
        if (currentIndex < 0) currentIndex = 0;
        if (currentIndex >= classes.size()) currentIndex = classes.size() - 1;

        if (ACTION_NEXT.equals(action)) {
            if (currentIndex < classes.size() - 1) {
                currentOffset++;
                updateNotification();
            }
        } else if (ACTION_PREV.equals(action)) {
            if (currentIndex > 0) {
                currentOffset--;
                updateNotification();
            }
        } else if (ACTION_CANCEL_CLASS.equals(action)) {
            DailyClassItem item = classes.get(currentIndex);
            LocalDate today = appClock.currentDate();
            disposables.add(
                    cancelClassOccurrenceUseCase.execute(item.getWeeklyClassId(), today)
                            .subscribeOn(appSchedulers.io())
                            .observeOn(appSchedulers.main())
                            .subscribe(() -> {}, throwable -> {})
            );
        }
    }

    private void updateNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, buildNotification());
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.notification_keep_alive_title),
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription(getString(R.string.notification_keep_alive_desc));
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private Notification buildNotification() {
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.notification_daily_class);

        if (currentSchedule != null && !currentSchedule.getClasses().isEmpty()) {
            List<DailyClassItem> classes = currentSchedule.getClasses();
            int baseIndex = currentSchedule.getCurrentClassIndex();
            boolean isAllDone = (baseIndex == -1);
            
            int currentIndex = isAllDone ? classes.size() + currentOffset : baseIndex + currentOffset;
            
            if (currentIndex < 0) {
                currentIndex = 0;
                currentOffset = isAllDone ? -classes.size() : -baseIndex;
            }
            if (currentIndex > classes.size()) {
                currentIndex = classes.size();
                currentOffset = isAllDone ? 0 : classes.size() - baseIndex;
            }
            
            String dateStr = currentSchedule.getDate().format(java.time.format.DateTimeFormatter.ofPattern("EEEE, MMM d", java.util.Locale.getDefault()));
            views.setTextViewText(R.id.tvNotifDate, dateStr);
            
            if (currentIndex == classes.size()) {
                views.setTextViewText(R.id.tvNotifCourseCode, "No more classes today");
                views.setTextViewText(R.id.tvNotifCourseName, "Enjoy your day!");
                views.setTextViewText(R.id.tvNotifTime, "");
                
                views.setViewVisibility(R.id.btnNotifPrev, View.VISIBLE);
                views.setViewVisibility(R.id.btnNotifNext, View.INVISIBLE);
                views.setViewVisibility(R.id.layoutNotifActions, View.GONE);
                
                Intent prevIntent = new Intent(this, AppKeepAliveService.class).setAction(ACTION_PREV);
                PendingIntent prevPending = PendingIntent.getService(this, 1, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                views.setOnClickPendingIntent(R.id.btnNotifPrev, prevPending);
            } else {
                DailyClassItem item = classes.get(currentIndex);
                
                views.setTextViewText(R.id.tvNotifCourseCode, item.getCourseCode());
                views.setTextViewText(R.id.tvNotifCourseName, item.getCourseName());
                
                String time = io.github.mdraihan27.routinemanager.core.util.DateTimeFormatter.formatTime12Hour(item.getStartHour(), item.getStartMinute()) + " - " + io.github.mdraihan27.routinemanager.core.util.DateTimeFormatter.formatTime12Hour(item.getEndHour(), item.getEndMinute());
                views.setTextViewText(R.id.tvNotifTime, time);
    
                views.setViewVisibility(R.id.btnNotifPrev, currentIndex > 0 ? View.VISIBLE : View.INVISIBLE);
                views.setViewVisibility(R.id.btnNotifNext, currentIndex < classes.size() ? View.VISIBLE : View.INVISIBLE);
                views.setViewVisibility(R.id.layoutNotifActions, View.VISIBLE);
    
                Intent prevIntent = new Intent(this, AppKeepAliveService.class).setAction(ACTION_PREV);
                PendingIntent prevPending = PendingIntent.getService(this, 1, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                views.setOnClickPendingIntent(R.id.btnNotifPrev, prevPending);
    
                Intent nextIntent = new Intent(this, AppKeepAliveService.class).setAction(ACTION_NEXT);
                PendingIntent nextPending = PendingIntent.getService(this, 2, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                views.setOnClickPendingIntent(R.id.btnNotifNext, nextPending);
    
                Intent cancelIntent = new Intent(this, AppKeepAliveService.class).setAction(ACTION_CANCEL_CLASS);
                PendingIntent cancelPending = PendingIntent.getService(this, 3, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                views.setOnClickPendingIntent(R.id.btnNotifCancel, cancelPending);
    
                Intent rescheduleIntent = new Intent(this, MainActivity.class);
                rescheduleIntent.setAction("ACTION_RESCHEDULE_FROM_NOTIF");
                rescheduleIntent.putExtra("EXTRA_CLASS_ID", item.getWeeklyClassId());
                rescheduleIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent reschedulePending = PendingIntent.getActivity(this, 4, rescheduleIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                views.setOnClickPendingIntent(R.id.btnNotifReschedule, reschedulePending);
            }
        } else {
            if (currentSchedule != null) {
                String dateStr = currentSchedule.getDate().format(java.time.format.DateTimeFormatter.ofPattern("EEEE, MMM d", java.util.Locale.getDefault()));
                views.setTextViewText(R.id.tvNotifDate, dateStr);
            } else {
                views.setTextViewText(R.id.tvNotifDate, "");
            }
            views.setTextViewText(R.id.tvNotifCourseCode, "No Classes");
            views.setTextViewText(R.id.tvNotifCourseName, "Enjoy your day!");
            views.setTextViewText(R.id.tvNotifTime, "");
            
            views.setViewVisibility(R.id.btnNotifPrev, View.INVISIBLE);
            views.setViewVisibility(R.id.btnNotifNext, View.INVISIBLE);
            views.setViewVisibility(R.id.layoutNotifActions, View.GONE);
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(views)
                .setCustomBigContentView(views)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .setAutoCancel(false)
                .build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        return notification;
    }

    @Override
    public void onDestroy() {
        disposables.clear();
        super.onDestroy();
    }
}
