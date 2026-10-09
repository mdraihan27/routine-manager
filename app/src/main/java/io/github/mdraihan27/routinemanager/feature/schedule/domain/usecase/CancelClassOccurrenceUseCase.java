package io.github.mdraihan27.routinemanager.feature.schedule.domain.usecase;

import androidx.annotation.NonNull;

import java.time.LocalDate;

import javax.inject.Inject;

import io.github.mdraihan27.routinemanager.feature.schedule.domain.repository.ScheduleRepository;
import io.reactivex.rxjava3.core.Completable;

public final class CancelClassOccurrenceUseCase {

    private final ScheduleRepository repository;

    @Inject
    public CancelClassOccurrenceUseCase(@NonNull ScheduleRepository repository) {
        this.repository = repository;
    }

    @NonNull
    public Completable execute(long weeklyClassId, @NonNull LocalDate date) {
        return repository.cancelOccurrence(weeklyClassId, date);
    }
}
