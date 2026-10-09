package io.github.mdraihan27.routinemanager.core.background;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public final class BackgroundKeepAliveManagerImpl implements BackgroundKeepAliveManager {

    private static final String WORK_NAME = "keep_alive_periodic_work";

    private final Context context;

    @Inject
    public BackgroundKeepAliveManagerImpl(@NonNull @ApplicationContext Context context) {
        this.context = context;
    }

    @Override
    public void startKeepAlive() {
        Intent serviceIntent = new Intent(context, AppKeepAliveService.class);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
        } catch (Exception ignored) {
        }

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                KeepAliveWorker.class,
                15,
                TimeUnit.MINUTES
        ).build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
        );
    }

    @SuppressLint("BatteryLife")
    @Override
    public void requestDisableBatteryOptimizations(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager powerManager = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
            if (powerManager != null && !powerManager.isIgnoringBatteryOptimizations(activity.getPackageName())) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                try {
                    activity.startActivity(intent);
                } catch (Exception ignored) {
                }
            }
        }
    }
}
