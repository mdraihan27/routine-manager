package io.github.mdraihan27.routinemanager.feature.routine.domain.usecase;

import androidx.annotation.NonNull;

import javax.inject.Inject;

import io.github.mdraihan27.routinemanager.feature.routine.domain.repository.RoutineRepository;
import io.reactivex.rxjava3.core.Completable;

public final class DeleteWeeklyClassUseCase {

    private final RoutineRepository repository;

    @Inject
    public DeleteWeeklyClassUseCase(@NonNull RoutineRepository repository) {
        this.repository = repository;
    }

    @NonNull
    public Completable execute(long id) {
        return repository.deleteWeeklyClass(id);
    }
}
