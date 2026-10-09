package io.github.mdraihan27.routinemanager.feature.course.domain.usecase;

import androidx.annotation.NonNull;

import javax.inject.Inject;

import io.github.mdraihan27.routinemanager.feature.course.domain.repository.CourseRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public final class DeleteCourseUseCase {

    private final CourseRepository repository;

    @Inject
    public DeleteCourseUseCase(@NonNull CourseRepository repository) {
        this.repository = repository;
    }

    @NonNull
    public Completable execute(long courseId) {
        return repository.deleteCourse(courseId);
    }

    @NonNull
    public Single<Boolean> isCourseReferencedInRoutine(long courseId) {
        return repository.isCourseReferencedInRoutine(courseId);
    }
}
