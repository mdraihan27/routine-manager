package io.github.mdraihan27.routinemanager.feature.routine.presentation;

import androidx.annotation.NonNull;

import java.time.DayOfWeek;
import java.util.Set;

import io.github.mdraihan27.routinemanager.core.base.UiEvent;
import io.github.mdraihan27.routinemanager.feature.routine.domain.model.WeeklyClass;

public abstract class RoutineWizardUiEvent implements UiEvent {

    RoutineWizardUiEvent() {
    }

    public static final class ToggleHoliday extends RoutineWizardUiEvent {
        private final DayOfWeek day;

        public ToggleHoliday(@NonNull DayOfWeek day) {
            this.day = day;
        }

        @NonNull
        public DayOfWeek getDay() {
            return day;
        }
    }

    public static final class SetStep extends RoutineWizardUiEvent {
        private final int step;

        public SetStep(int step) {
            this.step = step;
        }

        public int getStep() {
            return step;
        }
    }

    public static final class SetBreakTime extends RoutineWizardUiEvent {
        private final boolean hasBreak;
        private final int startHour;
        private final int startMinute;
        private final int endHour;
        private final int endMinute;

        public SetBreakTime(boolean hasBreak, int startHour, int startMinute, int endHour, int endMinute) {
            this.hasBreak = hasBreak;
            this.startHour = startHour;
            this.startMinute = startMinute;
            this.endHour = endHour;
            this.endMinute = endMinute;
        }

        public boolean hasBreak() {
            return hasBreak;
        }

        public int getStartHour() {
            return startHour;
        }

        public int getStartMinute() {
            return startMinute;
        }

        public int getEndHour() {
            return endHour;
        }

        public int getEndMinute() {
            return endMinute;
        }
    }

    public static final class SelectDay extends RoutineWizardUiEvent {
        private final DayOfWeek day;

        public SelectDay(@NonNull DayOfWeek day) {
            this.day = day;
        }

        @NonNull
        public DayOfWeek getDay() {
            return day;
        }
    }

    public static final class AddClass extends RoutineWizardUiEvent {
        private final WeeklyClass weeklyClass;

        public AddClass(@NonNull WeeklyClass weeklyClass) {
            this.weeklyClass = weeklyClass;
        }

        @NonNull
        public WeeklyClass getWeeklyClass() {
            return weeklyClass;
        }
    }

    public static final class DeleteClass extends RoutineWizardUiEvent {
        private final long classId;

        public DeleteClass(long classId) {
            this.classId = classId;
        }

        public long getClassId() {
            return classId;
        }
    }

    public static final class FinishWizard extends RoutineWizardUiEvent {
    }
}
