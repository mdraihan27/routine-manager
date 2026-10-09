package io.github.mdraihan27.routinemanager.feature.schedule.domain.usecase;

import androidx.annotation.NonNull;

import java.time.LocalDate;

import javax.inject.Inject;

import io.github.mdraihan27.routinemanager.feature.schedule.domain.repository.ScheduleRepository;
import io.reactivex.rxjava3.core.Completable;

public final class RescheduleClassUseCase {

    private final ScheduleRepository repository;

    @Inject
    public RescheduleClassUseCase(@NonNull ScheduleRepository repository) {
        this.repository = repository;
    }

    @NonNull
    public Completable execute(long weeklyClassId,
                               @NonNull LocalDate date,
                               boolean occurrenceOnly,
                               int startH,
                               int startM,
                               int endH,
                               int endM) {
        if (occurrenceOnly) {
            return repository.rescheduleOccurrenceOnly(weeklyClassId, date, startH, startM, endH, endM);
        } else {
            return repository.reschedulePermanent(weeklyClassId, startH, startM, endH, endM);
        }
    }
}
