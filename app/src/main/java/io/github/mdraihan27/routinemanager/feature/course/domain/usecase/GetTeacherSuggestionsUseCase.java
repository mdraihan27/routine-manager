package io.github.mdraihan27.routinemanager.feature.course.domain.usecase;

import androidx.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;

import io.github.mdraihan27.routinemanager.feature.course.domain.repository.CourseRepository;
import io.reactivex.rxjava3.core.Flowable;

public final class GetTeacherSuggestionsUseCase {

    private final CourseRepository repository;

    @Inject
    public GetTeacherSuggestionsUseCase(@NonNull CourseRepository repository) {
        this.repository = repository;
    }

    @NonNull
    public Flowable<List<String>> execute() {
        return repository.getTeacherSuggestions();
    }
}
