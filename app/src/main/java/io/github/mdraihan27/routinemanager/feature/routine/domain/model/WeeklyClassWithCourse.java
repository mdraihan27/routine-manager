package io.github.mdraihan27.routinemanager.feature.routine.domain.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

import io.github.mdraihan27.routinemanager.feature.course.domain.model.Course;

public final class WeeklyClassWithCourse implements Serializable {

    private final WeeklyClass weeklyClass;
    private final Course course;

    public WeeklyClassWithCourse(@NonNull WeeklyClass weeklyClass, @NonNull Course course) {
        this.weeklyClass = Objects.requireNonNull(weeklyClass);
        this.course = Objects.requireNonNull(course);
    }

    @NonNull
    public WeeklyClass getWeeklyClass() {
        return weeklyClass;
    }

    @NonNull
    public Course getCourse() {
        return course;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeeklyClassWithCourse that = (WeeklyClassWithCourse) o;
        return weeklyClass.equals(that.weeklyClass) && course.equals(that.course);
    }

    @Override
    public int hashCode() {
        return Objects.hash(weeklyClass, course);
    }
}
