package io.github.mdraihan27.routinemanager.feature.course.presentation.edit;

import androidx.annotation.NonNull;

import io.github.mdraihan27.routinemanager.core.base.UiEvent;
import io.github.mdraihan27.routinemanager.feature.course.domain.model.CourseColor;

public abstract class CourseEditUiEvent implements UiEvent {

    CourseEditUiEvent() {
    }

    public static final class LoadCourse extends CourseEditUiEvent {
        private final long courseId;

        public LoadCourse(long courseId) {
            this.courseId = courseId;
        }

        public long getCourseId() {
            return courseId;
        }
    }

    public static final class SaveCourse extends CourseEditUiEvent {
        private final long id;
        private final String code;
        private final String name;
        private final String teacherName;
        private final CourseColor color;

        public SaveCourse(long id,
                          @NonNull String code,
                          @NonNull String name,
                          @NonNull String teacherName,
                          @NonNull CourseColor color) {
            this.id = id;
            this.code = code;
            this.name = name;
            this.teacherName = teacherName;
            this.color = color;
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
    }

    public static final class DeleteCourse extends CourseEditUiEvent {
        private final long courseId;

        public DeleteCourse(long courseId) {
            this.courseId = courseId;
        }

        public long getCourseId() {
            return courseId;
        }
    }
}
