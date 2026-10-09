package io.github.mdraihan27.routinemanager.feature.schedule.domain.usecase;

import androidx.annotation.NonNull;

import javax.inject.Inject;

import io.github.mdraihan27.routinemanager.feature.schedule.domain.model.DailySchedule;
import io.github.mdraihan27.routinemanager.feature.schedule.domain.repository.ScheduleRepository;
import io.reactivex.rxjava3.core.Flowable;

public final class GetTodayScheduleUseCase {

    private final ScheduleRepository repository;

    @Inject
    public GetTodayScheduleUseCase(@NonNull ScheduleRepository repository) {
        this.repository = repository;
    }

    @NonNull
    public Flowable<DailySchedule> execute() {
        return repository.getTodaySchedule();
    }
}
