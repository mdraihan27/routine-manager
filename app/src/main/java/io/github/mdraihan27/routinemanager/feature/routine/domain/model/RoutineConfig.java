package io.github.mdraihan27.routinemanager.feature.routine.domain.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class RoutineConfig implements Serializable {

    private final Set<DayOfWeek> weeklyHolidays;
    private final boolean hasDefaultBreak;
    private final int breakStartHour;
    private final int breakStartMinute;
    private final int breakEndHour;
    private final int breakEndMinute;
    private final boolean configured;

    public RoutineConfig(@NonNull Set<DayOfWeek> weeklyHolidays,
                         boolean hasDefaultBreak,
                         int breakStartHour,
                         int breakStartMinute,
                         int breakEndHour,
                         int breakEndMinute,
                         boolean configured) {
        this.weeklyHolidays = Collections.unmodifiableSet(new HashSet<>(Objects.requireNonNull(weeklyHolidays)));
        this.hasDefaultBreak = hasDefaultBreak;
        this.breakStartHour = breakStartHour;
        this.breakStartMinute = breakStartMinute;
        this.breakEndHour = breakEndHour;
        this.breakEndMinute = breakEndMinute;
        this.configured = configured;
    }

    public static RoutineConfig empty() {
        return new RoutineConfig(Collections.emptySet(), false, 13, 0, 14, 0, false);
    }

    @NonNull
    public Set<DayOfWeek> getWeeklyHolidays() {
        return weeklyHolidays;
    }

    public boolean isHoliday(@NonNull DayOfWeek day) {
        return weeklyHolidays.contains(day);
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

    public boolean isConfigured() {
        return configured;
    }
}
