package io.github.mdraihan27.routinemanager.feature.routine.domain.usecase;

import androidx.annotation.NonNull;

import javax.inject.Inject;

import io.github.mdraihan27.routinemanager.feature.routine.domain.model.WeeklyClass;
import io.github.mdraihan27.routinemanager.feature.routine.domain.repository.RoutineRepository;
import io.reactivex.rxjava3.core.Completable;

public class UpdateWeeklyClassUseCase {

    private final RoutineRepository repository;

    @Inject
    public UpdateWeeklyClassUseCase(@NonNull RoutineRepository repository) {
        this.repository = repository;
    }

    @NonNull
    public Completable execute(@NonNull WeeklyClass weeklyClass) {
        return repository.updateWeeklyClass(weeklyClass);
    }
}
