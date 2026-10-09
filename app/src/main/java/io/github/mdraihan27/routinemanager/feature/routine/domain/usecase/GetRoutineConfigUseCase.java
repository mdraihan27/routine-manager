package io.github.mdraihan27.routinemanager.feature.routine.domain.usecase;

import androidx.annotation.NonNull;

import javax.inject.Inject;

import io.github.mdraihan27.routinemanager.feature.routine.domain.model.RoutineConfig;
import io.github.mdraihan27.routinemanager.feature.routine.domain.repository.RoutineRepository;
import io.reactivex.rxjava3.core.Flowable;

public final class GetRoutineConfigUseCase {

    private final RoutineRepository repository;

    @Inject
    public GetRoutineConfigUseCase(@NonNull RoutineRepository repository) {
        this.repository = repository;
    }

    @NonNull
    public Flowable<RoutineConfig> execute() {
        return repository.getRoutineConfig();
    }
}
