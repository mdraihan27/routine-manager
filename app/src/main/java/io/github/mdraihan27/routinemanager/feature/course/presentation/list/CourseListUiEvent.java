package io.github.mdraihan27.routinemanager.feature.course.presentation.list;

import io.github.mdraihan27.routinemanager.core.base.UiEvent;

public abstract class CourseListUiEvent implements UiEvent {

    CourseListUiEvent() {
    }

    public static final class LoadCourses extends CourseListUiEvent {
    }

    public static final class CourseClicked extends CourseListUiEvent {
        private final long courseId;

        public CourseClicked(long courseId) {
            this.courseId = courseId;
        }

        public long getCourseId() {
            return courseId;
        }
    }

    public static final class AddCourseClicked extends CourseListUiEvent {
    }
}
