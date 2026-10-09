package io.github.mdraihan27.routinemanager.feature.course.presentation.edit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.List;

import io.github.mdraihan27.routinemanager.core.base.UiState;
import io.github.mdraihan27.routinemanager.feature.course.domain.model.Course;

public final class CourseEditUiState implements UiState {

    private final Course initialCourse;
    private final List<String> teacherSuggestions;
    private final boolean saving;
    private final boolean saved;
    private final boolean deleted;
    private final boolean referencedInRoutine;
    private final String errorMessage;

    public CourseEditUiState(@Nullable Course initialCourse,
                             @NonNull List<String> teacherSuggestions,
                             boolean saving,
                             boolean saved,
                             boolean deleted,
                             boolean referencedInRoutine,
                             @Nullable String errorMessage) {
        this.initialCourse = initialCourse;
        this.teacherSuggestions = Collections.unmodifiableList(teacherSuggestions);
        this.saving = saving;
        this.saved = saved;
        this.deleted = deleted;
        this.referencedInRoutine = referencedInRoutine;
        this.errorMessage = errorMessage;
    }

    public static CourseEditUiState initial() {
        return new CourseEditUiState(null, Collections.emptyList(), false, false, false, false, null);
    }

    @Nullable
    public Course getInitialCourse() {
        return initialCourse;
    }

    @NonNull
    public List<String> getTeacherSuggestions() {
        return teacherSuggestions;
    }

    public boolean isSaving() {
        return saving;
    }

    public boolean isSaved() {
        return saved;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public boolean isReferencedInRoutine() {
        return referencedInRoutine;
    }

    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }
}
