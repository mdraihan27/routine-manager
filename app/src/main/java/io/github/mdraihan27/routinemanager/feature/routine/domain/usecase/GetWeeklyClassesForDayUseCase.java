package io.github.mdraihan27.routinemanager.feature.routine.domain.usecase;

import androidx.annotation.NonNull;

import java.time.DayOfWeek;
import java.util.List;

import javax.inject.Inject;

import io.github.mdraihan27.routinemanager.feature.routine.domain.model.WeeklyClassWithCourse;
import io.github.mdraihan27.routinemanager.feature.routine.domain.repository.RoutineRepository;
import io.reactivex.rxjava3.core.Flowable;

public final class GetWeeklyClassesForDayUseCase {

    private final RoutineRepository repository;

    @Inject
    public GetWeeklyClassesForDayUseCase(@NonNull RoutineRepository repository) {
        this.repository = repository;
    }

    @NonNull
    public Flowable<List<WeeklyClassWithCourse>> execute(@NonNull DayOfWeek dayOfWeek) {
        return repository.getWeeklyClassesForDay(dayOfWeek);
    }
}
