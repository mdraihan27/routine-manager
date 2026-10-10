package io.github.mdraihan27.routinemanager.core.background;

import android.app.Activity;

import androidx.annotation.NonNull;

public interface BackgroundKeepAliveManager {

    void startKeepAlive();

    void stopKeepAlive();

    void requestDisableBatteryOptimizations(@NonNull Activity activity);
}
