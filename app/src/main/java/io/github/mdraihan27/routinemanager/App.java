package io.github.mdraihan27.routinemanager;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        android.content.SharedPreferences prefs = getSharedPreferences("app_preferences", android.content.Context.MODE_PRIVATE);
        String mode = prefs.getString("theme_mode", "Auto");
        int nightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        if ("Dark".equals(mode)) {
            nightMode = AppCompatDelegate.MODE_NIGHT_YES;
        } else if ("Light".equals(mode)) {
            nightMode = AppCompatDelegate.MODE_NIGHT_NO;
        }
        AppCompatDelegate.setDefaultNightMode(nightMode);
    }
}
