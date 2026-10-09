package io.github.mdraihan27.routinemanager.feature.course.domain.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public final class Course implements Serializable {

    private final long id;
    private final String code;
    private final String name;
    private final String teacherName;
    private final CourseColor color;

    public Course(long id,
                  @NonNull String code,
                  @NonNull String name,
                  @NonNull String teacherName,
                  @NonNull CourseColor color) {
        this.id = id;
        this.code = Objects.requireNonNull(code);
        this.name = Objects.requireNonNull(name);
        this.teacherName = Objects.requireNonNull(teacherName);
        this.color = Objects.requireNonNull(color);
    }

    public long getId() {
        return id;
    }

    @NonNull
    public String getCode() {
        return code;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getTeacherName() {
        return teacherName;
    }

    @NonNull
    public CourseColor getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return id == course.id &&
                code.equals(course.code) &&
                name.equals(course.name) &&
                teacherName.equals(course.teacherName) &&
                color.equals(course.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, name, teacherName, color);
    }
}
