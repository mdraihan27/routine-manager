package io.github.mdraihan27.routinemanager.feature.settings.presentation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import io.github.mdraihan27.routinemanager.core.base.UiState;
import io.github.mdraihan27.routinemanager.core.designsystem.motion.AnimationIntensity;

public final class SettingsUiState implements UiState {

    private final AnimationIntensity intensity;
    private final boolean routineOverviewVertical;
    private final boolean persistentNotificationEnabled;
    private final boolean classAlarmEnabled;
    private final int classAlarmMinutes;
    private final String themeMode;
    private final boolean loading;
    private final String message;

    public SettingsUiState(@NonNull AnimationIntensity intensity,
                           boolean routineOverviewVertical,
                           boolean persistentNotificationEnabled,
                           boolean classAlarmEnabled,
                           int classAlarmMinutes,
                           @NonNull String themeMode,
                           boolean loading,
                           @Nullable String message) {
        this.intensity = Objects.requireNonNull(intensity);
        this.routineOverviewVertical = routineOverviewVertical;
        this.persistentNotificationEnabled = persistentNotificationEnabled;
        this.classAlarmEnabled = classAlarmEnabled;
        this.classAlarmMinutes = classAlarmMinutes;
        this.themeMode = Objects.requireNonNull(themeMode);
        this.loading = loading;
        this.message = message;
    }

    public static SettingsUiState initial() {
        return new SettingsUiState(AnimationIntensity.BALANCED, true, false, false, 15, "Auto", false, null);
    }

    @NonNull
    public AnimationIntensity getIntensity() {
        return intensity;
    }

    public boolean isRoutineOverviewVertical() {
        return routineOverviewVertical;
    }

    public boolean isLoading() {
        return loading;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    public boolean isPersistentNotificationEnabled() {
        return persistentNotificationEnabled;
    }

    public boolean isClassAlarmEnabled() {
        return classAlarmEnabled;
    }

    public int getClassAlarmMinutes() {
        return classAlarmMinutes;
    }

    @NonNull
    public String getThemeMode() {
        return themeMode;
    }

    public SettingsUiState copyWithIntensity(@NonNull AnimationIntensity newIntensity) {
        return new SettingsUiState(newIntensity, routineOverviewVertical, persistentNotificationEnabled, classAlarmEnabled, classAlarmMinutes, themeMode, loading, message);
    }

    public SettingsUiState copyWithRoutineOverviewVertical(boolean vertical) {
        return new SettingsUiState(intensity, vertical, persistentNotificationEnabled, classAlarmEnabled, classAlarmMinutes, themeMode, loading, message);
    }
    
    public SettingsUiState copyWithPersistentNotificationEnabled(boolean enabled) {
        return new SettingsUiState(intensity, routineOverviewVertical, enabled, classAlarmEnabled, classAlarmMinutes, themeMode, loading, message);
    }

    public SettingsUiState copyWithClassAlarmEnabled(boolean enabled) {
        return new SettingsUiState(intensity, routineOverviewVertical, persistentNotificationEnabled, enabled, classAlarmMinutes, themeMode, loading, message);
    }

    public SettingsUiState copyWithClassAlarmMinutes(int minutes) {
        return new SettingsUiState(intensity, routineOverviewVertical, persistentNotificationEnabled, classAlarmEnabled, minutes, themeMode, loading, message);
    }

    public SettingsUiState copyWithThemeMode(@NonNull String mode) {
        return new SettingsUiState(intensity, routineOverviewVertical, persistentNotificationEnabled, classAlarmEnabled, classAlarmMinutes, mode, loading, message);
    }

    public SettingsUiState copyWithMessage(@Nullable String message) {
        return new SettingsUiState(intensity, routineOverviewVertical, persistentNotificationEnabled, classAlarmEnabled, classAlarmMinutes, themeMode, loading, message);
    }
}
