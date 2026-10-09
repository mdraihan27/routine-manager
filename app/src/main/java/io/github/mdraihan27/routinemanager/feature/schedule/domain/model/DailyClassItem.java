package io.github.mdraihan27.routinemanager.feature.schedule.domain.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

import io.github.mdraihan27.routinemanager.feature.course.domain.model.CourseColor;

public final class DailyClassItem implements Serializable {

    private final long weeklyClassId;
    private final long courseId;
    private final String courseCode;
    private final String courseName;
    private final String teacherName;
    private final CourseColor courseColor;
    private final int startHour;
    private final int startMinute;
    private final int endHour;
    private final int endMinute;
    private final ClassStatus status;
    private final boolean exception;

    public DailyClassItem(long weeklyClassId,
                          long courseId,
                          @NonNull String courseCode,
                          @NonNull String courseName,
                          @NonNull String teacherName,
                          @NonNull CourseColor courseColor,
                          int startHour,
                          int startMinute,
                          int endHour,
                          int endMinute,
                          @NonNull ClassStatus status,
                          boolean exception) {
        this.weeklyClassId = weeklyClassId;
        this.courseId = courseId;
        this.courseCode = Objects.requireNonNull(courseCode);
        this.courseName = Objects.requireNonNull(courseName);
        this.teacherName = Objects.requireNonNull(teacherName);
        this.courseColor = Objects.requireNonNull(courseColor);
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.status = Objects.requireNonNull(status);
        this.exception = exception;
    }

    public long getWeeklyClassId() {
        return weeklyClassId;
    }

    public long getCourseId() {
        return courseId;
    }

    @NonNull
    public String getCourseCode() {
        return courseCode;
    }

    @NonNull
    public String getCourseName() {
        return courseName;
    }

    @NonNull
    public String getTeacherName() {
        return teacherName;
    }

    @NonNull
    public CourseColor getCourseColor() {
        return courseColor;
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

    @NonNull
    public ClassStatus getStatus() {
        return status;
    }

    public boolean isException() {
        return exception;
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
        DailyClassItem that = (DailyClassItem) o;
        return weeklyClassId == that.weeklyClassId &&
                courseId == that.courseId &&
                startHour == that.startHour &&
                startMinute == that.startMinute &&
                endHour == that.endHour &&
                endMinute == that.endMinute &&
                exception == that.exception &&
                courseCode.equals(that.courseCode) &&
                courseName.equals(that.courseName) &&
                teacherName.equals(that.teacherName) &&
                courseColor.equals(that.courseColor) &&
                status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(weeklyClassId, courseId, courseCode, startHour, startMinute, endHour, endMinute, status);
    }
}
