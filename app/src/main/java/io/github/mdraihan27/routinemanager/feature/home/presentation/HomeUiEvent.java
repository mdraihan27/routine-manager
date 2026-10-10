package io.github.mdraihan27.routinemanager.feature.home.presentation;

import androidx.annotation.NonNull;

import io.github.mdraihan27.routinemanager.core.base.UiEvent;

public abstract class HomeUiEvent implements UiEvent {

    public static final class LoadSchedule extends HomeUiEvent {
    }

    public static final class NextClass extends HomeUiEvent {
    }

    public static final class PreviousClass extends HomeUiEvent {
    }

    public static final class CancelCurrentClass extends HomeUiEvent {
    }

    public static final class UndoCancel extends HomeUiEvent {
        private final long classId;

        public UndoCancel(long classId) {
            this.classId = classId;
        }

        public long getClassId() {
            return classId;
        }
    }

    public static final class DismissOnboarding extends HomeUiEvent {
    }

    public static final class DismissGestureGuide extends HomeUiEvent {
    }

    public static final class RescheduleClass extends HomeUiEvent {
        private final long weeklyClassId;
        private final int startHour;
        private final int startMinute;
        private final int endHour;
        private final int endMinute;
        private final boolean permanent;

        public RescheduleClass(long weeklyClassId,
                               int startHour,
                               int startMinute,
                               int endHour,
                               int endMinute,
                               boolean permanent) {
            this.weeklyClassId = weeklyClassId;
            this.startHour = startHour;
            this.startMinute = startMinute;
            this.endHour = endHour;
            this.endMinute = endMinute;
            this.permanent = permanent;
        }

        public long getWeeklyClassId() {
            return weeklyClassId;
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

        public boolean isPermanent() {
            return permanent;
        }
    }
}
