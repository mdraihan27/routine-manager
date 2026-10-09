package io.github.mdraihan27.routinemanager.feature.course.domain.usecase;

import androidx.annotation.NonNull;

import javax.inject.Inject;

import io.github.mdraihan27.routinemanager.feature.course.domain.model.Course;
import io.github.mdraihan27.routinemanager.feature.course.domain.repository.CourseRepository;
import io.reactivex.rxjava3.core.Completable;

public final class AddCourseUseCase {

    private final CourseRepository repository;

    @Inject
    public AddCourseUseCase(@NonNull CourseRepository repository) {
        this.repository = repository;
    }

    @NonNull
    public Completable execute(@NonNull Course course) {
        return repository.addCourse(course);
    }
}
