package io.github.mdraihan27.routinemanager.feature.routine.presentation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.DayOfWeek;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import io.github.mdraihan27.routinemanager.core.base.UiState;
import io.github.mdraihan27.routinemanager.feature.course.domain.model.Course;
import io.github.mdraihan27.routinemanager.feature.routine.domain.model.WeeklyClassWithCourse;

public final class RoutineWizardUiState implements UiState {

    private final int currentStep;
    private final Set<DayOfWeek> holidays;
    private final boolean hasDefaultBreak;
    private final int breakStartHour;
    private final int breakStartMinute;
    private final int breakEndHour;
    private final int breakEndMinute;
    private final DayOfWeek selectedDay;
    private final List<WeeklyClassWithCourse> dayClasses;
    private final List<Course> allCourses;
    private final boolean saved;
    private final String errorMessage;

    public RoutineWizardUiState(int currentStep,
                                @NonNull Set<DayOfWeek> holidays,
                                boolean hasDefaultBreak,
                                int breakStartHour,
                                int breakStartMinute,
                                int breakEndHour,
                                int breakEndMinute,
                                @NonNull DayOfWeek selectedDay,
                                @NonNull List<WeeklyClassWithCourse> dayClasses,
                                @NonNull List<Course> allCourses,
                                boolean saved,
                                @Nullable String errorMessage) {
        this.currentStep = currentStep;
        this.holidays = Collections.unmodifiableSet(new HashSet<>(Objects.requireNonNull(holidays)));
        this.hasDefaultBreak = hasDefaultBreak;
        this.breakStartHour = breakStartHour;
        this.breakStartMinute = breakStartMinute;
        this.breakEndHour = breakEndHour;
        this.breakEndMinute = breakEndMinute;
        this.selectedDay = Objects.requireNonNull(selectedDay);
        this.dayClasses = Collections.unmodifiableList(Objects.requireNonNull(dayClasses));
        this.allCourses = Collections.unmodifiableList(Objects.requireNonNull(allCourses));
        this.saved = saved;
        this.errorMessage = errorMessage;
    }

    public static RoutineWizardUiState initial() {
        Set<DayOfWeek> defaultHolidays = new HashSet<>();
        defaultHolidays.add(DayOfWeek.FRIDAY);
        return new RoutineWizardUiState(
                0,
                defaultHolidays,
                true,
                13,
                0,
                14,
                0,
                DayOfWeek.SATURDAY,
                Collections.emptyList(),
                Collections.emptyList(),
                false,
                null
        );
    }

    public int getCurrentStep() {
        return currentStep;
    }

    @NonNull
    public Set<DayOfWeek> getHolidays() {
        return holidays;
    }

    public boolean hasDefaultBreak() {
        return hasDefaultBreak;
    }

    public int getBreakStartHour() {
        return breakStartHour;
    }

    public int getBreakStartMinute() {
        return breakStartMinute;
    }

    public int getBreakEndHour() {
        return breakEndHour;
    }

    public int getBreakEndMinute() {
        return breakEndMinute;
    }

    @NonNull
    public DayOfWeek getSelectedDay() {
        return selectedDay;
    }

    @NonNull
    public List<WeeklyClassWithCourse> getDayClasses() {
        return dayClasses;
    }

    @NonNull
    public List<Course> getAllCourses() {
        return allCourses;
    }

    public boolean isSaved() {
        return saved;
    }

    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }
}
