package io.github.mdraihan27.routinemanager.feature.settings.presentation;

import androidx.annotation.NonNull;

import java.util.Objects;

import io.github.mdraihan27.routinemanager.core.base.UiEvent;
import io.github.mdraihan27.routinemanager.core.designsystem.motion.AnimationIntensity;

public abstract class SettingsUiEvent implements UiEvent {

    public static final class SetIntensity extends SettingsUiEvent {
        private final AnimationIntensity intensity;

        public SetIntensity(@NonNull AnimationIntensity intensity) {
            this.intensity = Objects.requireNonNull(intensity);
        }

        @NonNull
        public AnimationIntensity getIntensity() {
            return intensity;
        }
    }

    public static final class ReplayOnboarding extends SettingsUiEvent {
    }

    public static final class ResetOnboarding extends SettingsUiEvent {
    }

    public static final class SetRoutineOverviewVertical extends SettingsUiEvent {
        private final boolean vertical;

        public SetRoutineOverviewVertical(boolean vertical) {
            this.vertical = vertical;
        }

        public boolean isVertical() {
            return vertical;
        }
    }

    public static final class SetPersistentNotification extends SettingsUiEvent {
        private final boolean enabled;

        public SetPersistentNotification(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isEnabled() {
            return enabled;
        }
    }
}
