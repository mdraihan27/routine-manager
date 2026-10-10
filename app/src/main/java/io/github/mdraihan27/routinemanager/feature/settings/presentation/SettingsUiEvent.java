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

    public static final class SetClassAlarm extends SettingsUiEvent {
        private final boolean enabled;

        public SetClassAlarm(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isEnabled() {
            return enabled;
        }
    }

    public static final class SetClassAlarmMinutes extends SettingsUiEvent {
        private final int minutes;

        public SetClassAlarmMinutes(int minutes) {
            this.minutes = minutes;
        }

        public int getMinutes() {
            return minutes;
        }
    }

    public static final class SetThemeMode extends SettingsUiEvent {
        private final String mode;

        public SetThemeMode(@NonNull String mode) {
            this.mode = Objects.requireNonNull(mode);
        }

        @NonNull
        public String getMode() {
            return mode;
        }
    }

    public static final class ExportData extends SettingsUiEvent {
        private final android.content.Context context;
        private final android.net.Uri uri;

        public ExportData(android.content.Context context, android.net.Uri uri) {
            this.context = context;
            this.uri = uri;
        }

        public android.content.Context getContext() { return context; }
        public android.net.Uri getUri() { return uri; }
    }

    public static final class ImportData extends SettingsUiEvent {
        private final android.content.Context context;
        private final android.net.Uri uri;

        public ImportData(android.content.Context context, android.net.Uri uri) {
            this.context = context;
            this.uri = uri;
        }

        public android.content.Context getContext() { return context; }
        public android.net.Uri getUri() { return uri; }
    }

    public static final class ClearMessage extends SettingsUiEvent {
    }
}
