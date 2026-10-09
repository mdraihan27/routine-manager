package io.github.mdraihan27.routinemanager.feature.settings.presentation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import io.github.mdraihan27.routinemanager.core.base.UiState;
import io.github.mdraihan27.routinemanager.core.designsystem.motion.AnimationIntensity;

public final class SettingsUiState implements UiState {

    private final AnimationIntensity intensity;
    private final boolean loading;
    private final String message;

    public SettingsUiState(@NonNull AnimationIntensity intensity,
                           boolean loading,
                           @Nullable String message) {
        this.intensity = Objects.requireNonNull(intensity);
        this.loading = loading;
        this.message = message;
    }

    public static SettingsUiState initial() {
        return new SettingsUiState(AnimationIntensity.BALANCED, false, null);
    }

    @NonNull
    public AnimationIntensity getIntensity() {
        return intensity;
    }

    public boolean isLoading() {
        return loading;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    public SettingsUiState copyWithIntensity(@NonNull AnimationIntensity newIntensity) {
        return new SettingsUiState(newIntensity, loading, message);
    }
}
