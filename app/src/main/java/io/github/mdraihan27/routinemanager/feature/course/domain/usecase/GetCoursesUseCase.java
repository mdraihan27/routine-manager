package io.github.mdraihan27.routinemanager.feature.course.domain.usecase;

import androidx.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;

import io.github.mdraihan27.routinemanager.feature.course.domain.model.Course;
import io.github.mdraihan27.routinemanager.feature.course.domain.repository.CourseRepository;
import io.reactivex.rxjava3.core.Flowable;

public final class GetCoursesUseCase {

    private final CourseRepository repository;

    @Inject
    public GetCoursesUseCase(@NonNull CourseRepository repository) {
        this.repository = repository;
    }

    @NonNull
    public Flowable<List<Course>> execute() {
        return repository.getCourses();
    }
}
