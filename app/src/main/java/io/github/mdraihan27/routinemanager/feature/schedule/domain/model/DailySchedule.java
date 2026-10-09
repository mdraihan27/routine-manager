package io.github.mdraihan27.routinemanager.feature.schedule.domain.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class DailySchedule implements Serializable {

    private final LocalDate date;
    private final DayOfWeek dayOfWeek;
    private final boolean holiday;
    private final boolean breakActive;
    private final List<DailyClassItem> classes;
    private final int currentClassIndex;

    public DailySchedule(@NonNull LocalDate date,
                         @NonNull DayOfWeek dayOfWeek,
                         boolean holiday,
                         boolean breakActive,
                         @NonNull List<DailyClassItem> classes,
                         int currentClassIndex) {
        this.date = Objects.requireNonNull(date);
        this.dayOfWeek = Objects.requireNonNull(dayOfWeek);
        this.holiday = holiday;
        this.breakActive = breakActive;
        this.classes = Collections.unmodifiableList(Objects.requireNonNull(classes));
        this.currentClassIndex = currentClassIndex;
    }

    public static DailySchedule empty(@NonNull LocalDate date, @NonNull DayOfWeek dayOfWeek) {
        return new DailySchedule(date, dayOfWeek, false, false, Collections.emptyList(), 0);
    }

    @NonNull
    public LocalDate getDate() {
        return date;
    }

    @NonNull
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
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

    public int getCurrentClassIndex() {
        return currentClassIndex;
    }
}
