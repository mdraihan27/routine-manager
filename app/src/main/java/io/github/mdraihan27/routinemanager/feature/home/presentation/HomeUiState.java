package io.github.mdraihan27.routinemanager.feature.home.presentation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import io.github.mdraihan27.routinemanager.core.base.UiState;
import io.github.mdraihan27.routinemanager.feature.schedule.domain.model.DailyClassItem;

public final class HomeUiState implements UiState {

    private final boolean loading;
    private final boolean showOnboarding;
    private final boolean showGestureGuide;
    private final boolean hasCourses;
    private final boolean hasRoutine;
    private final boolean holiday;
    private final boolean breakActive;
    private final List<DailyClassItem> classes;
    private final int currentIndex;
    private final boolean canUndoCancel;
    private final long lastCancelledClassId;
    private final java.util.Map<java.time.DayOfWeek, List<io.github.mdraihan27.routinemanager.feature.routine.domain.model.WeeklyClassWithCourse>> weeklyClasses;
    private final boolean routineOverviewVertical;
    private final String message;

    public HomeUiState(boolean loading,
                       boolean showOnboarding,
                       boolean showGestureGuide,
                       boolean hasCourses,
                       boolean hasRoutine,
                       boolean holiday,
                       boolean breakActive,
                       @NonNull List<DailyClassItem> classes,
                       int currentIndex,
                       boolean canUndoCancel,
                       long lastCancelledClassId,
                       @NonNull java.util.Map<java.time.DayOfWeek, List<io.github.mdraihan27.routinemanager.feature.routine.domain.model.WeeklyClassWithCourse>> weeklyClasses,
                       boolean routineOverviewVertical,
                       @Nullable String message) {
        this.loading = loading;
        this.showOnboarding = showOnboarding;
        this.showGestureGuide = showGestureGuide;
        this.hasCourses = hasCourses;
        this.hasRoutine = hasRoutine;
        this.holiday = holiday;
        this.breakActive = breakActive;
        this.classes = Collections.unmodifiableList(Objects.requireNonNull(classes));
        this.currentIndex = currentIndex;
        this.canUndoCancel = canUndoCancel;
        this.lastCancelledClassId = lastCancelledClassId;
        this.weeklyClasses = java.util.Collections.unmodifiableMap(new java.util.HashMap<>(Objects.requireNonNull(weeklyClasses)));
        this.routineOverviewVertical = routineOverviewVertical;
        this.message = message;
    }

    public static HomeUiState initial() {
        return new HomeUiState(
                true,
                false,
                false,
                false,
                false,
                false,
                false,
                Collections.emptyList(),
                0,
                false,
                -1L,
                java.util.Collections.emptyMap(),
                true,
                null
        );
    }

    public boolean isLoading() {
        return loading;
    }

    public boolean isShowOnboarding() {
        return showOnboarding;
    }

    public boolean isShowGestureGuide() {
        return showGestureGuide;
    }

    public boolean hasCourses() {
        return hasCourses;
    }

    public boolean hasRoutine() {
        return hasRoutine;
    }

    public boolean isHoliday() {
        return holiday;
    }

    public boolean isBreakActive() {
        return breakActive;
    }

    @NonNull
    public List<DailyClassItem> getClasses() {
        return classes;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public boolean isCanUndoCancel() {
        return canUndoCancel;
    }

    public long getLastCancelledClassId() {
        return lastCancelledClassId;
    }

    @NonNull
    public java.util.Map<java.time.DayOfWeek, List<io.github.mdraihan27.routinemanager.feature.routine.domain.model.WeeklyClassWithCourse>> getWeeklyClasses() {
        return weeklyClasses;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    public boolean isRoutineOverviewVertical() {
        return routineOverviewVertical;
    }

    public boolean hasClasses() {
        return !classes.isEmpty();
    }

    @Nullable
    public DailyClassItem getCurrentClass() {
        if (classes.isEmpty() || currentIndex < 0 || currentIndex >= classes.size()) {
            return null;
        }
        return classes.get(currentIndex);
    }

    public boolean isFirstClass() {
        return currentIndex <= 0;
    }

    public boolean isLastClass() {
        return currentIndex >= classes.size() - 1;
    }

    public HomeUiState copyWithIndex(int newIndex) {
        return new HomeUiState(
                loading,
                showOnboarding,
                showGestureGuide,
                hasCourses,
                hasRoutine,
                holiday,
                breakActive,
                classes,
                newIndex,
                canUndoCancel,
                lastCancelledClassId,
                weeklyClasses,
                routineOverviewVertical,
                message
        );
    }

    public HomeUiState copyWithUndo(boolean canUndo, long cancelledId) {
        return new HomeUiState(
                loading,
                showOnboarding,
                showGestureGuide,
                hasCourses,
                hasRoutine,
                holiday,
                breakActive,
                classes,
                currentIndex,
                canUndo,
                cancelledId,
                weeklyClasses,
                routineOverviewVertical,
                message
        );
    }

    public HomeUiState copyWithGuides(boolean onboarding, boolean gestureGuide) {
        return new HomeUiState(
                loading,
                onboarding,
                gestureGuide,
                hasCourses,
                hasRoutine,
                holiday,
                breakActive,
                classes,
                currentIndex,
                canUndoCancel,
                lastCancelledClassId,
                weeklyClasses,
                routineOverviewVertical,
                message
        );
    }
}
