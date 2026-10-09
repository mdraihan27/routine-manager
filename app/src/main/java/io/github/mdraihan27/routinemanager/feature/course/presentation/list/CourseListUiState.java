package io.github.mdraihan27.routinemanager.feature.course.presentation.list;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import io.github.mdraihan27.routinemanager.core.base.UiState;
import io.github.mdraihan27.routinemanager.feature.course.domain.model.Course;

public final class CourseListUiState implements UiState {

    private final boolean loading;
    private final List<Course> courses;
    private final boolean empty;
    private final String errorMessage;

    public CourseListUiState(boolean loading,
                             @NonNull List<Course> courses,
                             boolean empty,
                             @Nullable String errorMessage) {
        this.loading = loading;
        this.courses = Collections.unmodifiableList(Objects.requireNonNull(courses));
        this.empty = empty;
        this.errorMessage = errorMessage;
    }

    public static CourseListUiState initial() {
        return new CourseListUiState(true, Collections.emptyList(), false, null);
    }

    public boolean isLoading() {
        return loading;
    }

    @NonNull
    public List<Course> getCourses() {
        return courses;
    }

    public boolean isEmpty() {
        return empty;
    }

    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }
}
