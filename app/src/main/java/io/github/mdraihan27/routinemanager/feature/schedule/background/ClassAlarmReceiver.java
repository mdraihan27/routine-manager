package io.github.mdraihan27.routinemanager.feature.schedule.background;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import io.github.mdraihan27.routinemanager.R;

public class ClassAlarmReceiver extends BroadcastReceiver {

    public static final String ACTION_CLASS_START = "io.github.mdraihan27.routinemanager.ACTION_CLASS_START";
    public static final String ACTION_PRE_FIRST_CLASS = "io.github.mdraihan27.routinemanager.ACTION_PRE_FIRST_CLASS";

    public static final String EXTRA_COURSE_CODE = "extra_course_code";
    public static final String EXTRA_COURSE_NAME = "extra_course_name";
    public static final String EXTRA_CLASS_ID = "extra_class_id";

    private static final String CHANNEL_ID_ALARM = "class_alarm_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;

        createChannel(context);

        String courseCode = intent.getStringExtra(EXTRA_COURSE_CODE);
        String courseName = intent.getStringExtra(EXTRA_COURSE_NAME);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return;

        if (ACTION_CLASS_START.equals(action)) {
            Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID_ALARM)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Class Starting: " + courseCode)
                    .setContentText(courseName)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setAutoCancel(true)
                    .build();
            nm.notify(courseCode.hashCode(), notification);

        } else if (ACTION_PRE_FIRST_CLASS.equals(action)) {
            Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID_ALARM)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Upcoming First Class: " + courseCode)
                    .setContentText(courseName)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                    .setAutoCancel(true)
                    .build();
            nm.notify("PRE_FIRST_CLASS".hashCode(), notification);
        }
    }

    private void createChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID_ALARM,
                    "Class Alarms",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for class times");
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null) nm.createNotificationChannel(channel);
        }
    }
}
