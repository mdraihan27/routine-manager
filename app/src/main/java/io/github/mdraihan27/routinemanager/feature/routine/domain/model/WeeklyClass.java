package io.github.mdraihan27.routinemanager.feature.routine.domain.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.Objects;

public final class WeeklyClass implements Serializable {

    private final long id;
    private final long courseId;
    private final DayOfWeek dayOfWeek;
    private final int startHour;
    private final int startMinute;
    private final int endHour;
    private final int endMinute;

    public WeeklyClass(long id,
                       long courseId,
                       @NonNull DayOfWeek dayOfWeek,
                       int startHour,
                       int startMinute,
                       int endHour,
                       int endMinute) {
        this.id = id;
        this.courseId = courseId;
        this.dayOfWeek = Objects.requireNonNull(dayOfWeek);
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
    }

    public long getId() {
        return id;
    }

    public long getCourseId() {
        return courseId;
    }

    @NonNull
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
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

    public int getStartTotalMinutes() {
        return startHour * 60 + startMinute;
    }

    public int getEndTotalMinutes() {
        return endHour * 60 + endMinute;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeeklyClass that = (WeeklyClass) o;
        return id == that.id &&
                courseId == that.courseId &&
                startHour == that.startHour &&
                startMinute == that.startMinute &&
                endHour == that.endHour &&
                endMinute == that.endMinute &&
                dayOfWeek == that.dayOfWeek;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, courseId, dayOfWeek, startHour, startMinute, endHour, endMinute);
    }
}
